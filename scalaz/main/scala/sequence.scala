package shapeless.contrib.scalaz

import shapeless._

import scalaz._
import scalaz.syntax.apply._

// inspired by Travis Brown and Michael Pilquist
// <http://stackoverflow.com/a/16128064>
sealed trait Sequencer[I <: HList] {
  type FOut[x]
  type Out <: HList
  def apply(in: I): FOut[Out]
}

trait Sequencer0 {

  type Aux[F[_], I <: HList, O <: HList] = Sequencer[I] { type Out = O; type FOut[x] = F[x] }

  type UnapplyAux[TC[_[_]], MA, M0[_], A0] = Unapply[TC, MA] { type M[x] = M0[x]; type A = A0 }

  // Here be dragons
  // Trust me, you don't want to know
  implicit def consSequencerAux[G[_], GH, H, T <: HList, O <: HList](
    implicit ev1: UnapplyAux[Apply, GH, G, H],
             seq: Sequencer.Aux[G, T, O]
  ): Aux[G, GH :: T, H :: O] = new Sequencer[GH :: T] {
    type FOut[x] = G[x]
    type Out = H :: O
    def apply(in: GH :: T) = in match {
      case head :: tail =>
        ev1.TC.apply2(ev1(head), seq(tail)) { _ :: _ }
    }
  }

}

object Sequencer extends Sequencer0 {

  implicit def nilSequencerAux[F[_] : Applicative]: Aux[F, HNil, HNil] = new Sequencer[HNil] {
    type FOut[x] = F[x]
    type Out = HNil
    def apply(in: HNil) =
      Applicative[F].pure(HNil: HNil)
  }

  implicit def singleSequencerAux[FA](implicit ev: Unapply[Functor, FA]): Aux[ev.M, FA :: HNil, ev.A :: HNil] = new Sequencer[FA :: HNil] {
    type Out = ev.A :: HNil
    type FOut[x] = ev.M[x]
    def apply(in: FA :: HNil) = in match {
      case fa :: _ =>
        ev.TC.map(ev(fa)) { _ :: HNil }
    }
  }

}

trait SequenceFunctions {

  def sequence[I <: HList](in: I)(implicit sequencer: Sequencer[I]): sequencer.FOut[sequencer.Out] =
    sequencer(in)

}

// vim: expandtab:ts=2:sw=2
