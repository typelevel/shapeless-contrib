package shapeless.contrib.scalaz

import shapeless._
import shapeless.Poly._
import scalaz.Applicative

trait TraverseFunctions {
  sealed trait TraverserAux[I <: HList, O1 <: HList, O2 <: HList, F[_], P] {
    def apply(in: I): F[O2]
  }

  sealed trait Traverser[I <: HList, O <: HList, F[_], P] {
    def apply(in: I): F[O]
  }

  object Traverser {
    implicit def fromTraverserAux[I <: HList, O <: HList, F[_], P](implicit traverserAux: TraverserAux[I, _,  O, F, P]): Traverser[I, O, F, P] = new Traverser[I, O, F, P] {
      def apply(in: I) = traverserAux.apply(in)
    }
  }

  object TraverserAux {
    implicit def fromSequencerAndMapper[I <: HList, O1 <: HList, O2 <: HList, F[_], P](implicit mapper: MapperAux[P, I, O1], sequencer: Sequencer[F, O1, O2]): TraverserAux[I, O1, O2, F, P] = new TraverserAux[I, O1, O2, F, P] {
      def apply(in: I) = sequencer(mapper(in))
    }
  }

  def traverse[I <: HList, O <: HList, F[_]](in: I)(f: Poly)(implicit traverser: Traverser[I, O, F, f.type]): F[O] = traverser(in)
}

object TraverseFunctions extends TraverseFunctions
