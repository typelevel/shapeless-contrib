package shapeless.contrib.scalaz

import shapeless._
import shapeless.ops.hlist._
import shapeless.Poly._

import scalaz.Applicative

sealed trait TraverserAux[I <: HList, P, F[_], O <: HList] {
  def apply(in: I): F[O]
}

object TraverserAux {

  implicit def fromSequencerAndMapper[I <: HList, P, F[_], S <: HList, O <: HList](
    implicit mapper: Mapper.Aux[P, I, S],
             sequencer: SequencerAux[F, S, O]
  ): TraverserAux[I, P, F, O] = new TraverserAux[I, P, F, O] {
    def apply(in: I) = sequencer(mapper(in))
  }

}

sealed trait Traverser[I <: HList, P, F[_]] {
  type O <: HList
  def apply(in: I): F[O]
}

object Traverser {

  implicit def fromTraverserAux[I <: HList, P, F[_], O1 <: HList](implicit traverserAux: TraverserAux[I, P, F, O1]): Traverser[I, P, F] = new Traverser[I, P, F] {
    type O = O1
    def apply(in: I) = traverserAux.apply(in)
  }

}

trait TraverseFunctions {

  def traverse[I <: HList, F[_], O <: HList](in: I)(f: Poly)(implicit traverser: TraverserAux[I, f.type, F, O]): F[O] =
    traverser(in)

}

// vim: expandtab:ts=2:sw=2
