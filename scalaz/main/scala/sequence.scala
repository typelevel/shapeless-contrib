package shapeless.contrib.scalaz

import shapeless._

import scalaz._
import scalaz.syntax.apply._

// inspired by Travis Brown and Michael Pilquist
// <http://stackoverflow.com/a/16128064>
sealed trait SequencerAux[F[_], I <: HList, O <: HList] {
  def apply(in: I): F[O]
}

trait SequencerAux0 {

  // Here be dragons
  // Trust me, you don't want to know
  implicit def consSequencerAux[F[_], G[_], GH, H, T <: HList, O <: HList](
    implicit ev1: UnapplyAux[Apply, GH, G, H],
             seq: SequencerAux[F, T, O],
             ev2: F[O] <:< G[O]
  ): SequencerAux[G, GH :: T, H :: O] = new SequencerAux[G, GH :: T, H :: O] {
    def apply(in: GH :: T) = in match {
      case head :: tail =>
        ev1.TC.apply2(ev1(head), ev2(seq(tail))) { _ :: _ }
    }
  }

}

object SequencerAux extends SequencerAux0 {

  implicit def nilSequencerAux[F[_] : Applicative]: SequencerAux[F, HNil, HNil] = new SequencerAux[F, HNil, HNil] {
    def apply(in: HNil) =
      Applicative[F].pure(HNil: HNil)
  }

  implicit def singleSequencerAux[FA](implicit ev: Unapply[Functor, FA]): SequencerAux[ev.M, FA :: HNil, ev.A :: HNil] = new SequencerAux[ev.M, FA :: HNil, ev.A :: HNil] {
    def apply(in: FA :: HNil) = in match {
      case fa :: _ =>
        ev.TC.map(ev(fa)) { _ :: HNil }
    }
  }

}

trait SequenceFunctions {

  def sequence[I <: HList, O <: HList, F[_]](in: I)(implicit sequencer: SequencerAux[F, I, O]): F[O] =
    sequencer(in)

}

// vim: expandtab:ts=2:sw=2
