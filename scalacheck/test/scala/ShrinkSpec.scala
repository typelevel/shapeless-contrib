package shapeless.contrib.scalachecktests

import org.scalacheck.{Arbitrary,Gen,Properties,Shrink,Test}
import org.scalacheck.Prop.forAll

import shapeless.contrib.scalacheck._

object ArbitrarySpec extends Properties("Arbitrary") {
  private val ok = (_: Any) => true

  sealed trait Tree[A]
  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  case class Leaf[A](item: A) extends Tree[A]

  property("leaf") = {
    forAll(implicitly[Arbitrary[Leaf[Int]]].arbitrary)(ok)
  }

  property("tree") = {
    forAll(implicitly[Arbitrary[Tree[Int]]].arbitrary)(ok)
  }
}

object ShrinkSpec extends Properties("Shrink") {

  case class ShrinkTest(one: String,
                        two: String)

  private def shrinkClosure[T : Shrink](x: T): Stream[T] = {
    val xs = Shrink.shrink[T](x)
    if(xs.isEmpty) xs
    else xs.append(xs.take(1).map(shrinkClosure[T]).flatten)
  }

  val emptyShrinkTest = ShrinkTest("","")

  property("derived shrink") = forAll {(shrinkMe: ShrinkTest) =>
    shrinkMe == emptyShrinkTest || shrinkClosure(shrinkMe).contains(emptyShrinkTest)
  }
}
