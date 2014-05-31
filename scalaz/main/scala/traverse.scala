package shapeless.contrib.scalaz

import shapeless._
import shapeless.Poly._

sealed trait Traverser[L <: HList, P] {
  type Out
  def apply(in: L): Out
}

object Traverser {
  type Aux[L <: HList, P, Out0] = Traverser[L, P] { type Out = Out0 }

  implicit def mkTraverser[L <: HList, P, S <: HList](
    implicit
      mapper: MapperAux[P, L, S],
      sequencer: Sequencer[S]
  ): Aux[L, P, sequencer.Out] =
    new Traverser[L, P] {
      type Out = sequencer.Out
      def apply(in: L): Out = sequencer(mapper(in))
    }
}

trait TraverseFunctions {
  def traverse[L <: HList](in: L)(f: Poly)
    (implicit traverser: Traverser[L, f.type]): traverser.Out = traverser(in)
}
