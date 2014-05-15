package shapeless.contrib.scalacheck

import org.scalacheck.{Arbitrary,Gen,Properties,Shrink,Test}
import org.scalacheck.Prop.forAll
import shapeless._
import shapeless.ops.coproduct._

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
