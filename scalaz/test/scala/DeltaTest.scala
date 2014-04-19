package shapeless.contrib.scalaz

import org.specs2.scalaz.{Spec, ScalazMatchers}
import scalaz.Lens
import shapeless._


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


    beforeM.delta(afterM) must equal(expectedM)

    val nested = Map("a" -> Map(1 -> 1), "b" -> beforeM).delta(Map("b" -> afterM, "c" -> Map(3 -> 3)))

    nested must equal(MapPatch(
      added   = Map("c" -> Map(3 -> 3)),
      removed = Map("a" -> Map(1 -> 1)),
      changed = Map("b" -> expectedM)
    ))
  }

  "lens delta" in {
    import Delta.std.int._

    val lens = Lens.lensu[HasInt, Int]({ case (hasInt, int) => hasInt.copy(i = int) }, _.i)

    implicit val hasIntDelta = Delta[Int].delta.lens(lens)

    HasInt(1).delta(HasInt(2)) must equal(1.delta(2))
  }

  "hlist delta" in {
    import Delta.std.int._
    import Delta.hlist._

    val delta = (1 :: 10 :: HNil).delta(2 :: 20 :: HNil).toList

    delta must equal(List(1.delta(2), 10.delta(20)))

    val result = (1 :: 10 :: HNil).zipWith(2 :: 20 :: HNil)(deltaPoly).toList

    result must equal(List(1.delta(2), 10.delta(20)))
  }

  "generic delta" in {
    import Delta.std.int._
    import Delta.std.map._
    import Delta.hlist._

    implicit val hasIntDelta = Delta.generic(Generic[HasInt])

    HasInt(1).delta(HasInt(2)).toList must equal(List(1.delta(2)))

    implicit val mapAndIntDelta = Delta.generic(Generic[MapAndInt])

    val actual = MapAndInt(1, beforeM).delta(MapAndInt(2, afterM))

    actual.head must equal(1.delta(2))
    actual.tail.head must equal(expectedM)
  }

  "create delta from function" in {
    implicit val doubleDelta = Delta.from[Double] { case (before, after) => after - before }

    1.5.delta(2.0) must equal(0.5)
  }

  "can map over delta" in {
    implicit val intDeltaAsString: Delta[Int, String] = Delta.std.int.deltaInt.map(_.toString)

    1.delta(2) must equal("1")
  }

  case class HasInt(i: Int)
  case class MapAndInt(i: Int, m: Map[Int, Int])

  val beforeM = Map(1 -> 1, 2 -> 2)
  val afterM  = Map(2 -> 22, 3 -> 3)

  val expectedM = Delta.std.map.MapPatch(
    added   = Map(3 -> 3),
    removed = Map(1 -> 1),
    changed = Map(2 -> Delta.std.int.deltaInt(2, 22))
  )
}

// vim: expandtab:ts=2:sw=2
