package shapeless.contrib.scalaz

import org.specs2.scalaz.{Spec, ScalazMatchers}


class DeltaTest extends Spec with ScalazMatchers {
  import Delta._
  import scalaz.std.AllInstances._

  "int delta" in {
    2.delta(10) must equal(8)
    10.delta(2) must equal(-8)
  }

  "set delta" in {
    val expected = SetPatch(removed = Set(1), added = Set(3))

    Set(1, 2).delta(Set(2, 3)) must equal(expected)
  }

  "map delta" in {
    val before = Map(1 -> 1, 2 -> 2)
    val after  = Map(2 -> 22, 3 -> 3)

    val expected = MapPatch(
      added   = Map(3 -> 3),
      removed = Map(1 -> 1),
      changed = Map(2 -> 2.delta(22))
    )

    before.delta(after) must equal(expected)

    val nested = Map("a" -> Map(1 -> 1), "b" -> before).delta(Map("b" -> after, "c" -> Map(3 -> 3)))

    nested must equal(MapPatch(
      added   = Map("c" -> Map(3 -> 3)),
      removed = Map("a" -> Map(1 -> 1)),
      changed = Map("b" -> expected)
    ))
  }
}

// vim: expandtab:ts=2:sw=2
