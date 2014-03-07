package shapeless.contrib.scalaz

import scalaz.Lens


trait Delta[In, Out] {
  def apply(before: In, after: In): Out

  def lens[Container](lens: Lens[Container, In]): Delta[Container, Out] =
    new LensDelta[Container, In, Out](lens, this)
}

object Delta {
  def apply[In] = new {
    def delta[Out](implicit delta: Delta[In, Out]): Delta[In, Out] = delta
  }

  implicit class DeltaOps[In](val before: In) extends AnyVal {
    def delta[Out](after: In)(implicit delta: Delta[In, Out]): Out = delta(before, after)
  }

  implicit object deltaInt extends Delta[Int, Int] {
    def apply(before: Int, after: Int): Int = after - before
  }

  implicit def deltaSet[A]: Delta[Set[A], SetPatch[A]] = new Delta[Set[A], SetPatch[A]] {
    def apply(before: Set[A], after: Set[A]): SetPatch[A] =
      SetPatch[A](added = after -- before, removed = before -- after)
  }

  implicit def deltaMap[K, V, VOut](implicit deltaV: Delta[V, VOut]):
    Delta[Map[K, V], MapPatch[K, V, VOut]] = new Delta[Map[K, V], MapPatch[K, V, VOut]] {
      def apply(before: Map[K, V], after: Map[K, V]): MapPatch[K, V, VOut] = {
        val changed: Map[K, VOut] = (before.keySet & after.keySet).map(k => {
          k -> deltaV(before(k), after(k))
        })(scala.collection.breakOut)

        MapPatch[K, V, VOut](after -- before.keySet, before -- after.keySet, changed)
      }
    }

  case class SetPatch[A](added: Set[A], removed: Set[A])

  case class MapPatch[K, V, VOut](added: Map[K, V], removed: Map[K, V], changed: Map[K, VOut])
}

private class LensDelta[Container, In, Out](lens: Lens[Container, In], delta: Delta[In, Out])
  extends Delta[Container, Out] {

  def apply(left: Container, right: Container): Out = delta(lens.get(left), lens.get(right))
}

// vim: expandtab:ts=2:sw=2
