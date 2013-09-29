package shapeless.contrib.scalaz

import scalaz._

// this should go into scalaz proper, once codegen for Unapply is in place
trait UnapplyAux[TC[_[_]], FA, F[_], A] {
  def TC: TC[F]
  def apply(fa: FA): F[A]
}

object UnapplyAux {
  implicit def unapplyAux[TC[_[_]], FA](implicit ev: Unapply[TC, FA]): UnapplyAux[TC, FA, ev.M, ev.A] = new UnapplyAux[TC, FA, ev.M, ev.A] {
    def TC = ev.TC
    def apply(fa: FA) = ev(fa)
  }
}

// vim: expandtab:ts=2:sw=2
