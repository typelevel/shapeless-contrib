package shapeless.contrib.scalaz

import scalaz._
import scalaz.syntax.applicative._

import shapeless._
import shapeless.ops.hlist._

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

    def liftA[F, PI, R, I <: HList](f: PI => R)(
      implicit lifter: LifterAux[G, I, R, I],
               tupler: Tupler.Aux[I, PI], gen: Generic.Aux[PI, I]
    ): PI => G[R] =
      pi => lifter(instance.pure((i: I) => f(tupler(i))))(instance)(gen.to(pi))

  }

}

// vim: expandtab:ts=2:sw=2
