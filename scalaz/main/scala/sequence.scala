package shapeless.contrib.scalaz

import shapeless._

import scalaz._
import scalaz.syntax.apply._

// inspired by Travis Brown and Michael Pilquist
// <http://stackoverflow.com/a/16128064>
trait SequenceFunctions {

  // Here be dragons
  // Trust me, you don't want to know

  sealed trait Sequencer[F[_], I <: HList, O <: HList] {
    def apply(in: I): F[O]
  }

  trait Sequencer0 {

    implicit def consSequencer[F[_], G[_], GH, H, T <: HList, O <: HList](
      implicit ev1: UnapplyAux[Apply, GH, G, H],
               seq: Sequencer[F, T, O],
               ev2: F[O] <:< G[O]
    ): Sequencer[G, GH :: T, H :: O] = new Sequencer[G, GH :: T, H :: O] {
      def apply(in: GH :: T) = in match {
        case head :: tail =>
          ev1.TC.apply2(ev1(head), ev2(seq(tail))) { _ :: _ }
      }
    }

  }

  object Sequencer extends Sequencer0 {

    implicit def nilSequencer[F[_] : Applicative]: Sequencer[F, HNil, HNil] = new Sequencer[F, HNil, HNil] {
      def apply(in: HNil) =
        Applicative[F].pure(HNil: HNil)
    }

    implicit def singleSequencer[FA](implicit ev: Unapply[Functor, FA]): Sequencer[ev.M, FA :: HNil, ev.A :: HNil] = new Sequencer[ev.M, FA :: HNil, ev.A :: HNil] {
      def apply(in: FA :: HNil) = in match {
        case fa :: _ =>
          ev.TC.map(ev(fa)) { _ :: HNil }
      }
    }

  }

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

  def sequence[I <: HList, O <: HList, F[_]](in: I)(implicit sequencer: Sequencer[F, I, O]): F[O] =
    sequencer(in)

}

// vim: expandtab:ts=2:sw=2
