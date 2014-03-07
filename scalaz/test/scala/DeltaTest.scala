package shapeless.contrib.scalaz

import org.specs2.scalaz.{Spec, ScalazMatchers}
import scalaz.Lens


class DeltaTest extends Spec with ScalazMatchers {
  import scalaz.std.AllInstances._
  import Delta._

  "int delta" in {
    import Delta.std.int._

    2.delta(10) must equal(8)
    10.delta(2) must equal(-8)
  }

  "set delta" in {
    import Delta.std.set._

    val expected = SetPatch(removed = Set(1), added = Set(3))

    Set(1, 2).delta(Set(2, 3)) must equal(expected)
  }

  "map delta" in {
    import Delta.std.int._
    import Delta.std.map._

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

  "lens delta" in {
    import Delta.std.int._

    case class HasInt(i: Int)
    val lens = Lens.lensu[HasInt, Int]({ case (hasInt, int) => hasInt.copy(i = int) }, _.i)

    implicit val hasIntDelta = Delta[Int].delta.lens(lens)

    HasInt(1).delta(HasInt(2)) must equal(1.delta(2))
  }

  "create delta from function" in {
    implicit val doubleDelta = Delta.from[Double] { case (before, after) => after - before }

    1.5.delta(2.0) must equal(0.5)
  }

  "can map over delta" in {
    implicit val intDeltaAsString: Delta[Int, String] = Delta.std.int.deltaInt.map(_.toString)

    1.delta(2) must equal("1")
  }
}

// vim: expandtab:ts=2:sw=2
