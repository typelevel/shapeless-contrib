package shapeless.contrib.scalaz

import shapeless.contrib.scalacheck._

import org.specs2.matcher.OptionMatchers
import org.specs2.scalaz.Spec

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._

import scalaz.{Equal, \/}
import scalaz.scalacheck.ScalazArbitrary._

class BinaryTest extends Spec with OptionMatchers {

  import scalaz.std.AllInstances._

  implicit val ByteVectorArbitrary = Arbitrary(arbitrary[List[Byte]] map { _.toVector })

  def binaryLaws[A : Binary : Equal : Arbitrary](name: String) =
    name ! prop { (a: A, rest: Vector[Byte]) =>
      val encoded = Binary[A] encode a
      val decoded = Binary[A] decode (encoded ++ rest)
      Equal[Option[(A, Vector[Byte])]].equal(decoded, Some((a, rest)))
    }

  "simple instances" should {
    binaryLaws[Int]("Int")
    binaryLaws[(Int, Int)]("(Int, Int)")
    binaryLaws[Int \/ Long]("Int \\/ Long")
    binaryLaws[List[Int]]("List[Int]")
    binaryLaws[String]("String")
  }

  case class OneElem(n: Int)
  case class TwoElem(n: Int, x: String)
  case class Complex(n: Int, x: TwoElem \/ String, z: List[OneElem])

  "case class instances" should {
    import Binary.auto._

    binaryLaws[OneElem]("OneElem")
    binaryLaws[TwoElem]("TwoElem")
    binaryLaws[Complex]("Complex")
    binaryLaws[Complex]("Complex + checksum")(Binary[Complex].withChecksum(new java.util.zip.CRC32), implicitly, implicitly)

    {
      implicit val instance = Binary.auto.derive[(Int, String)]
      binaryLaws[(Int, String)]("Tuple2")
    }
  }

  sealed trait Cases[A, B]
  case class Case1[A, B](a: A) extends Cases[A, B]
  case class Case2[A, B](b: B) extends Cases[A, B]

  sealed trait Tree[A]
  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  case class Leaf[A](item: A) extends Tree[A]

  "multi-case class instances" should {
    import Binary.auto._

    binaryLaws[Cases[OneElem, TwoElem]]("Cases[OneElem, TwoElem]")
    binaryLaws[Cases[Complex, Complex]]("Cases[Complex, Complex]")

    binaryLaws[Tree[Int]]("Tree[Int]")
    binaryLaws[Tree[Complex]]("Tree[Complex]")
  }

  "checksum" should {
    "complain when broken" ! prop { (n: Long) =>
      val binary = Binary[Long].withChecksum(new java.util.zip.CRC32)
      val encoded = binary encode n
      // let's manipulate the last byte of the checksum
      val manipulated = encoded.init :+ (encoded.last + 1).toByte
      binary decode manipulated must beNone
    }
  }

}

// vim: expandtab:ts=2:sw=2
