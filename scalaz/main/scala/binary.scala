package shapeless.contrib.scalaz

import scala.annotation.tailrec
import scala.collection.breakOut
import scala.collection.generic.CanBuildFrom

import java.util.zip.Checksum
import java.nio.ByteBuffer

import shapeless._
import shapeless.ops.hlist._

import scalaz.{Coproduct => _, _}
import scalaz.std.tuple._
import scalaz.syntax.bifunctor._
import scalaz.syntax.id._
import scalaz.syntax.std.boolean._

trait Binary[A] { outer =>

  def encode(a: A): Vector[Byte]

  def decode(bytes: Vector[Byte]): Option[(A, Vector[Byte])]

  final def project[B](from: B => A, to: A => B): Binary[B] = new Binary[B] {
    def encode(b: B) = outer encode (from(b))
    def decode(bytes: Vector[Byte]) = outer decode bytes map { _ leftMap to }
  }

  final def repeatSeq[C[X] <: Seq[X]](implicit cbf: CanBuildFrom[Nothing, A, C[A]]) = repeatColl[C[A]](identity, cbf)

  final def repeatColl[Coll](implicit f: Coll => Seq[A], cbf: CanBuildFrom[Nothing, A, Coll]): Binary[Coll] = new Binary[Coll] {

    def encode(coll: Coll) =
      (Binary[Int] encode coll.length) ++
      coll.flatMap(outer.encode)(breakOut)

    def decode(bytes: Vector[Byte]) = {
      val builder = cbf()

      @tailrec def consume(count: Int, rest: Vector[Byte]): Option[Vector[Byte]] =
        if (count > 0)
          outer decode rest match {
            case Some((a, rest)) =>
              builder += a
              consume(count - 1, rest)
            case None =>
              None
          }
        else
          Some(rest)

      Binary[Int] decode bytes flatMap { case (length, rest) =>
        if (length < 0) {
          None
        }
        else {
          builder.sizeHint(length)
          consume(length, rest) map { (builder.result, _) }
        }
      }
    }

  }

  final def withChecksum(summer: => Checksum): Binary[A] = new Binary[A] {

    private def hash(v: Vector[Byte]) = {
      val summer0 = summer
      v.foreach(b => summer0.update(b: Int))
      summer0.getValue()
    }

    private val BinaryByteVec = Binary[Byte].repeatSeq[Vector]

    def encode(a: A) = {
      val encoded = outer encode a
      val header  = Binary[Long]  encode hash(encoded)
      val payload = BinaryByteVec encode encoded
      header ++ payload
    }

    def decode(bytes: Vector[Byte]) = {
      def reconstruct(header: Long, payload: Vector[Byte]) =
        if (header == hash(payload))
          outer decode payload
        else
          None

      for {
        (header,  r0) <- Binary[Long]  decode bytes
        (payload, r1) <- BinaryByteVec decode r0
        // We discard the rest here, because there shouldn't be any
        (result,  _)  <- reconstruct(header, payload)
      } yield (result, r1)
    }

  }

}

object Binary extends TypeClassCompanion[Binary] {

  @inline def apply[A](implicit A: Binary[A]) = A

  // Ops

  case class BinaryEncodeOps[A : Binary](a: A) {
    def encode = Binary[A] encode a
  }

  case class BinaryDecodeOps(bytes: Vector[Byte]) {
    def decode[A : Binary] = Binary[A] decode bytes
    def decodeOnly[A : Binary] = Binary[A] decode bytes map { _._1 }
  }

  // Binary is a TypeClass

  implicit val BinaryInstance: TypeClass[Binary] = new TypeClass[Binary] {

    def emptyProduct = new Binary[HNil] {
      def encode(hnil: HNil) = Vector()
      def decode(bytes: Vector[Byte]) = Some(HNil, bytes)
    }

    def project[F, G](instance: => Binary[G], to: F => G, from: G => F) =
      instance.project(to, from)

    def product[H, T <: HList](BHead: Binary[H], BTail: Binary[T]) = new ProductBinary[H :: T, H, T] {
      def A = BHead
      def B = BTail
      def fold[X](p: H :: T)(f: (H, T) => X) = f(p.head, p.tail)
      def prod(h: H, t: T) = h :: t
    }

    def coproduct[L, R <: Coproduct](CL: => Binary[L],CR: => Binary[R]) = new SumBinary[L :+: R, L, R] {
      def A = CL
      def B = CR
      def fold[X](s: L :+: R)(left: L => X, right: R => X) = s match {
        case Inl(l) => left(l)
        case Inr(r) => right(r)
      }
      def left(l: L) = Inl(l)
      def right(r: R) = Inr(r)
    }


  }

  // Instances for data types

  private trait ByteBufferBinary[A <: AnyVal] extends Binary[A] {
    def put(a: A, buf: ByteBuffer): ByteBuffer
    def get(buf: ByteBuffer): A
    val length: Int

    def encode(a: A) =
      put(a, ByteBuffer allocate length).array().toVector

    def decode(bytes: Vector[Byte]) = {
      val (xs, rest) = bytes splitAt length
      if (xs.length < length)
        None
      else
        Some((get(ByteBuffer wrap xs.toArray), rest))
    }
  }

  implicit def ByteBinary: Binary[Byte] = new Binary[Byte] {
    def encode(byte: Byte) = Vector(byte)
    def decode(bytes: Vector[Byte]) = bytes match {
      case byte +: rest => Some((byte, rest))
      case _ => None
    }
  }

  implicit def IntBinary: Binary[Int] = new ByteBufferBinary[Int] {
    def put(n: Int, buf: ByteBuffer) = buf.putInt(n)
    def get(buf: ByteBuffer) = buf.getInt()
    val length = 4
  }

  implicit def LongBinary: Binary[Long] = new ByteBufferBinary[Long] {
    def put(n: Long, buf: ByteBuffer) = buf.putLong(n)
    def get(buf: ByteBuffer) = buf.getLong()
    val length = 8
  }

  implicit def StringBinary: Binary[String] =
    Binary[Byte].repeatColl[Array[Byte]].project(_.getBytes, new String(_))

  implicit def ListBinary[A : Binary]: Binary[List[A]] =
    Binary[A].repeatSeq[List]

  implicit def PairBinary[A : Binary, B : Binary]: Binary[(A, B)] = new ProductBinary[(A, B), A, B] {
    def A = Binary[A]
    def B = Binary[B]
    def fold[X](p: (A, B))(f: (A, B) => X) = f.tupled(p)
    def prod(a: A, b: B) = (a, b)
  }

  implicit def EitherBinary[A : Binary, B : Binary]: Binary[A \/ B] = new SumBinary[A \/ B, A, B] {
    def A = Binary[A]
    def B = Binary[B]
    def fold[X](s: A \/ B)(left: A => X, right: B => X) = s.fold(left, right)
    def left(a: A) = \/ left a
    def right(b: B) = \/ right b
  }

}

trait BinarySyntax {

  import Binary._

  implicit def ToEncodeOps[A : Binary](a: A) = BinaryEncodeOps(a)
  implicit def ToDecodeOps(bytes: Vector[Byte]) = BinaryDecodeOps(bytes)

}

private trait SumBinary[S, A, B] extends Binary[S] {

  def A: Binary[A]
  def B: Binary[B]

  def fold[X](s: S)(left: A => X, right: B => X): X
  def left(a: A): S
  def right(b: B): S

  def encode(s: S) =
    fold(s)(
      a => 0.toByte +: (A encode a),
      b => 1.toByte +: (B encode b)
    )

  def decode(bytes: Vector[Byte]) = Binary[Byte] decode bytes match {
    case Some((0, rest)) => A decode rest map { _ leftMap left }
    case Some((1, rest)) => B decode rest map { _ leftMap right }
    case _ => None
  }

}

private trait ProductBinary[P, A, B] extends Binary[P] {

  def A: Binary[A]
  def B: Binary[B]

  def fold[X](p: P)(f: (A, B) => X): X
  def prod(a: A, b: B): P

  def encode(p: P) =
    fold(p)((a, b) => (A encode a) ++ (B encode b))

  def decode(r0: Vector[Byte]) =
    for {
      (a, r1) <- A decode r0
      (b, r2) <- B decode r1
    } yield (prod(a, b), r2)

}

// vim: expandtab:ts=2:sw=2
