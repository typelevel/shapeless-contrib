package shapeless.contrib.scalaz

import scalaz._
import scalaz.syntax.applicative._

import shapeless._
import shapeless.Functions._

trait LifterAux[T[_], H <: HList, R, O <: HList] {
  def apply(tf: T[H => R]): O => T[R]
}

object LifterAux {

  implicit def liftZero[T[_], R](implicit T: Apply[T]): LifterAux[T, HNil, R, HNil] = new LifterAux[T, HNil, R, HNil] {
    def apply(tf: T[HNil => R]) = hnil => tf.map(_(hnil))
  }

  implicit def liftCons[T[_], A, H <: HList, R, O <: HList](implicit T: Apply[T], tail: LifterAux[T, H, R, O]): LifterAux[T, A :: H, R, T[A] :: O] = new LifterAux[T, A :: H, R, T[A] :: O] {
    def apply(tf: T[(A :: H) => R]) = hcons =>
      tail(^(hcons.head, tf) { (a, f) => h => f(a :: h) })(hcons.tail)
  }

}

trait LiftFunctions {

  def lift[T[_], F, R, L <: HList, OL <: HList, OF](f: T[F])(implicit T: Apply[T], hlister: FnHListerAux[F, L => R], lifter: LifterAux[T, L, R, OL], unhlister: FnUnHListerAux[OL => T[R], OF]): OF =
    lifter(f.map(_.hlisted)).unhlisted

}

// vim: expandtab:ts=2:sw=2
