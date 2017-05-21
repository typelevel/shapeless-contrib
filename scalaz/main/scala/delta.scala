package shapeless.contrib.scalaz

import scalaz.{Equal, Lens, Show, \/, -\/, \/-}
import shapeless._


trait Delta[In, Out] {
  def apply(before: In, after: In): Out

  def lens[Container](lens: Lens[Container, In]): Delta[Container, Out] =
    new LensDelta[Container, In, Out](lens, this)

  def map[B](f: Out => B): Delta[In, B] = new MappedDelta[In, Out, B](f, this)
}

object Delta {
  def apply[In] = new {
    def delta[Out](implicit delta: Delta[In, Out]): Delta[In, Out] = delta
  }

  def from[In] = new {
    def apply[Out](f: (In, In) => Out): Delta[In, Out] = new FunctionDelta[In, Out](f)
  }

  def generic[In, Out](gen: Generic[In])(implicit genDelta: Delta[gen.Repr, Out]): Delta[In, Out] =
    from[In].apply[Out] {
      case (before, after) => genDelta(gen.to(before), gen.to(after))
    }

  implicit class DeltaOps[In](val before: In) extends AnyVal {
    def delta[Out](after: In)(implicit delta: Delta[In, Out]): Out = delta(before, after)
  }

  object std {
    object int {
      implicit val deltaInt: Delta[Int, Int] = Delta.from[Int] { case (before, after) => after - before }
    }

    object either {
      implicit def deltaEither[L, R, LOut, ROut](
        implicit ldelta: Delta[L, LOut], rdelta: Delta[R, ROut]
      ): Delta[Either[L, R], EitherPatch[L, R, LOut, ROut]] = {
        new Delta[Either[L, R], EitherPatch[L, R, LOut, ROut]] {
          def apply(before: Either[L, R], after: Either[L, R]): EitherPatch[L, R, LOut, ROut] = {
            (before, after) match {
              case (Left(before),  Left(after))  => BothLeft[LOut](ldelta(before, after))
              case (Right(before), Right(after)) => BothRight[ROut](rdelta(before, after))
              case (Left(before),  Right(after)) => WasLeft(before, after)
              case (Right(before), Left(after))  => WasRight(before, after)
            }
          }
        }
      }

      implicit def deltaV[L, R, LOut, ROut](
        implicit ldelta: Delta[L, LOut], rdelta: Delta[R, ROut]
      ): Delta[\/[L, R], EitherPatch[L, R, LOut, ROut]] = {
        new Delta[L \/ R, EitherPatch[L, R, LOut, ROut]] {
          def apply(before: L \/ R, after: L \/ R): EitherPatch[L, R, LOut, ROut] = {
            (before, after) match {
              case (-\/(before), -\/(after)) => BothLeft[LOut](ldelta(before, after))
              case (\/-(before), \/-(after)) => BothRight[ROut](rdelta(before, after))
              case (-\/(before), \/-(after)) => WasLeft(before, after)
              case (\/-(before), -\/(after)) => WasRight(before, after)
            }
          }
        }
      }

      trait EitherPatch[+L, +R, +LOut, +ROut]
      case class BothLeft[LOut](out: LOut) extends EitherPatch[Nothing, Nothing, LOut, Nothing]
      case class BothRight[ROut](out: ROut) extends EitherPatch[Nothing, Nothing, Nothing, ROut]
      case class WasLeft[L, R](left: L, right: R) extends EitherPatch[L, R, Nothing, Nothing]
      case class WasRight[L, R](right: R, left: L) extends EitherPatch[L, R, Nothing, Nothing]
    }

    object map {
      implicit def deltaMap[K, V, VOut](implicit deltaV: Delta[V, VOut]):
        Delta[Map[K, V], MapPatch[K, V, VOut]] = new Delta[Map[K, V], MapPatch[K, V, VOut]] {
          def apply(before: Map[K, V], after: Map[K, V]): MapPatch[K, V, VOut] = {
            val changed: Map[K, VOut] = (before.keySet & after.keySet).map(k => {
              k -> deltaV(before(k), after(k))
            })(scala.collection.breakOut)

            MapPatch[K, V, VOut](after -- before.keySet, before -- after.keySet, changed)
          }
        }

      case class MapPatch[K, V, VOut](added: Map[K, V], removed: Map[K, V], changed: Map[K, VOut])
    }

    object set {
      implicit def deltaSet[A]: Delta[Set[A], SetPatch[A]] = new Delta[Set[A], SetPatch[A]] {
        def apply(before: Set[A], after: Set[A]): SetPatch[A] =
          SetPatch[A](added = after -- before, removed = before -- after)
      }

      case class SetPatch[A](added: Set[A], removed: Set[A])
    }
  }

  object hlist {
    implicit object HNILDelta extends Delta[HNil, HNil] {
      def apply(before: HNil, after: HNil): HNil = HNil
    }

    trait DeltaAux[In] {
      type Out

      def apply(before: In, after: In): Out
    }

    implicit def deltaToAux[In, Out0](implicit delta: Delta[In, Out0]) = new DeltaAux[In] {
      type Out = Out0

      def apply(before: In, after: In): Out = delta(before, after)
    }

    object deltaPoly extends Poly2 {
      implicit def delta[In](implicit delta: DeltaAux[In]) = at[In, In] {
        case (before, after) => delta(before, after)
      }
    }

    implicit def HConsDelta[H, T <: HList, Out, TOut <: HList](
      implicit deltaH: Delta[H, Out], deltaT: Delta[T, TOut]): Delta[H :: T, Out :: TOut] =
        new Delta[H :: T, Out :: TOut] {
          def apply(before: H :: T, after: H :: T): Out :: TOut = {
            deltaH(before.head, after.head) :: deltaT(before.tail, after.tail)
          }
        }
  }
}

private class LensDelta[Container, In, Out](lens: Lens[Container, In], delta: Delta[In, Out])
  extends Delta[Container, Out] {

  def apply(left: Container, right: Container): Out = delta(lens.get(left), lens.get(right))
}

private class FunctionDelta[In, Out](f: (In, In) => Out) extends Delta[In, Out] {
  def apply(left: In, right: In): Out = f(left, right)
}

private class MappedDelta[In, Out, B](f: Out => B, delta: Delta[In, Out]) extends Delta[In, B] {
  def apply(left: In, right: In): B = f(delta(left, right))
}

// vim: expandtab:ts=2:sw=2
