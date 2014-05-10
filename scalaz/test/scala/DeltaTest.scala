package shapeless.contrib.scalaz

import org.specs2.scalaz.{Spec, ScalazMatchers}
import scalaz.{Equal, Lens, Show, \/}
import shapeless._

import scalaz.std.either._


class DeltaTest extends Spec with ScalazMatchers {
  import scalaz.std.AllInstances._
  import Delta._

  "int delta" in {
    import Delta.std.int._

    2.delta(10) must equal(8)
    10.delta(2) must equal(-8)
  }

  "either delta" in {
    import Delta.std.int._
    import Delta.std.either._

    type E  = Either[Int, Int]
    type EP = EitherPatch[Int, Int, Int, Int]

    def left(l: Int): E = Left(l)
    def right(r: Int): E = Right(r)
    def bothLeft(out: Int): EP = BothLeft[Int](out)
    def bothRight(out: Int): EP = BothRight[Int](out)
    def wasLeft(l: Int, r: Int): EP = WasLeft[Int, Int](l, r)
    def wasRight(r: Int, l: Int): EP = WasRight[Int, Int](r, l)

    left(2).delta(left(10))   must equal(bothLeft(8))
    right(2).delta(right(10)) must equal(bothRight(8))
    left(2).delta(right(10))  must equal(wasLeft(2, 10))
    right(2).delta(left(10))  must equal(wasRight(2, 10))
  }

  "\\/ delta" in {
    import Delta.std.int._
    import Delta.std.either._

    type E  = \/[Int, Int]
    type EP = EitherPatch[Int, Int, Int, Int]

    def left(l: Int): E = \/.left[Int, Int](l)
    def right(r: Int): E = \/.right[Int, Int](r)
    def bothLeft(out: Int): EP = BothLeft[Int](out)
    def bothRight(out: Int): EP = BothRight[Int](out)
    def wasLeft(l: Int, r: Int): EP = WasLeft[Int, Int](l, r)
    def wasRight(r: Int, l: Int): EP = WasRight[Int, Int](r, l)

    left(2).delta(left(10))   must equal(bothLeft(8))
    right(2).delta(right(10)) must equal(bothRight(8))
    left(2).delta(right(10))  must equal(wasLeft(2, 10))
    right(2).delta(left(10))  must equal(wasRight(2, 10))
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

  import Delta.std.either._

  implicit def eitherPatchEqual[L, R, LOut, ROut](
    implicit lEqual: Equal[L], rEqual: Equal[R], loutEqual: Equal[LOut], routEqual: Equal[ROut]
  ): Equal[EitherPatch[L, R, LOut, ROut]] = new Equal[EitherPatch[L, R, LOut, ROut]] {
    type EP = EitherPatch[L, R, LOut, ROut]

    def equal(before: EP, after: EP): Boolean = (before, after) match {
      case (BothLeft(blBefore), BothLeft(blAfter)) => {
        loutEqual.equal(blBefore, blAfter)
      }
      case (BothRight(brBefore), BothRight(brAfter)) => {
        routEqual.equal(brBefore, brAfter)
      }
      case (WasLeft(lBefore, rBefore), WasLeft(lAfter, rAfter)) => {
        lEqual.equal(lBefore, lAfter) && rEqual.equal(rBefore, rAfter)
      }
      case (WasRight(rBefore, lBefore), WasRight(rAfter, lAfter)) => {
        rEqual.equal(rBefore, rAfter) && lEqual.equal(lBefore, lAfter)
      }
      case _ => false
    }
  }

  implicit def eitherPatchShow[L, R, LOut, ROut](
    implicit lshow: Show[L], rshow: Show[R], loutShow: Show[LOut], routShow: Show[ROut]
  ): Show[EitherPatch[L, R, LOut, ROut]] = new Show[EitherPatch[L, R, LOut, ROut]] {
    override def shows(in: EitherPatch[L, R, LOut, ROut]): String = in match {
      case BothLeft(out)         => s"BothLeft(${loutShow.show(out)})"
      case BothRight(out)        => s"BothRight(${routShow.show(out)})"
      case WasLeft(left, right)  => s"WasLeft(${lshow.show(left)}, ${rshow.show(right)})"
      case WasRight(right, left) => s"WasRight(${rshow.show(right)}, S{lshow.show(left)})"
    }
  }
}

// vim: expandtab:ts=2:sw=2
