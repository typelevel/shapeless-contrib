package shapeless.contrib.scalaz

import scalaz._
import scalaz.syntax.applicative._

import shapeless._
import shapeless.Functions._

trait Lifts {

  trait LifterAux[G[_], I <: HList, R, GI <: HList] {
    def apply(gf: G[I => R])(implicit G: Apply[G]): GI => G[R]
  }

  object LifterAux {

    implicit def liftZero[G[_], R]: LifterAux[G, HNil, R, HNil] = new LifterAux[G, HNil, R, HNil] {
      def apply(gf: G[HNil => R])(implicit G: Apply[G]) = _ =>
        gf map { _(HNil) }
    }

    implicit def liftCons[G[_], H, T <: HList, R, GI <: HList](implicit tail: LifterAux[G, T, R, GI]): LifterAux[G, H :: T, R, G[H] :: GI] = new LifterAux[G, H :: T, R, G[H] :: GI] {
      def apply(gf: G[(H :: T) => R])(implicit G: Apply[G]) = {
        case gh :: gi =>
          tail(G.apply2(gh, gf) { (h, f) => t => f(h :: t) })(G)(gi)
      }
    }

  }

  implicit class ApplicativeOps[G[_]](instance: Applicative[G]) {

    def liftA[F, R, I <: HList, GI <: HList, OF](f: F)(
      implicit hlister: FnHListerAux[F, I => R],
               lifter: LifterAux[G, I, R, GI],
               unhlister: FnUnHListerAux[GI => G[R], OF]
    ): OF =
      lifter(instance.pure(f.hlisted))(instance).unhlisted

  }

}

// vim: expandtab:ts=2:sw=2
