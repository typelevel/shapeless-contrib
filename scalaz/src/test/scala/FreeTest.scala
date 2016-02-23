//package shapeless.contrib.scalaz
package scalaz
// TODO make a public 'Suspend' smart constructor

import scalaz.Free._
import org.specs2.scalaz.Spec
import scalaz.scalacheck.ScalazProperties.order
import scalaz.scalacheck.ScalaCheckBinding._
import scalaz.std.AllInstances._
import org.scalacheck.{Arbitrary, Gen}
import shapeless.contrib.scalaz._

class FreeTest extends Spec {

  implicit def freeArbitrary[F[_]: Functor, A](implicit
    A: Arbitrary[A],
    F0: shapeless.Lazy[Arbitrary[F[Free[F, A]]]]
  ): Arbitrary[Free[F, A]] =
    Arbitrary(Gen.oneOf(
      Functor[Arbitrary].map(A)(Return[F, A](_)).arbitrary,
      Functor[Arbitrary].map(F0.value)(Suspend[F, A](_)).arbitrary
    ))

  type PairOpt[A] = Option[(A, A)]
  type FList[A] = Free[PairOpt, A] // Free Monad List

  implicit val pairOptFunctor: Functor[PairOpt] =
    new Functor[PairOpt]{
      def map[A, B](fa: PairOpt[A])(f: A => B) =
        fa.map{ t => (f(t._1), f(t._2)) }
    }

  implicit class ListOps[A](self: List[A]){
    def toFList: FList[A] = self match {
      case h :: t =>
        Suspend[PairOpt, A](Option((Return[PairOpt, A](h), t.toFList)))
      case Nil =>
        Suspend[PairOpt, A](None)
    }
  }

  checkAll(order.laws[FList[Int]])

  "Order[List[Int]] is Order[FList[Int]]" ! prop{ (a: List[Int], b: List[Int]) =>
    val aa = a.toFList
    val bb = b.toFList
    Equal[List[Int]].equal(a, b) must_== Equal[FList[Int]].equal(aa, bb)
    Order[List[Int]].order(a, b) must_== Order[FList[Int]].order(aa, bb)
  }

  "shows" ! prop{ a: FList[Int] =>
    Show[FList[Int]].shows(a) must_== a.toString
  }

}

