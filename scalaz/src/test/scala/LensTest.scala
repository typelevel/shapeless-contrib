package shapeless.contrib.scalaz

import shapeless.Lens
import shapeless.Nat._
import shapeless.contrib.scalacheck._

import org.specs2.scalaz.Spec

import scalaz.scalacheck.ScalazProperties._

class LensTest extends Spec {

  import scalaz.std.anyVal._
  import scalaz.std.string._

  case class TwoElem(n: Int, x: String)

  val nLens = shapeless.lens[TwoElem] >> 'n
  val xLens = shapeless.lens[TwoElem] >> 'x

  checkAll("case class >> 'n", lens.laws(nLens.asScalaz))
  checkAll("case class >> 'x", lens.laws(xLens.asScalaz))

  case class Nested(a: Int, b: TwoElem)

  val bnLens = shapeless.lens[Nested] >> 'b >> 'n
  val bxLens = shapeless.lens[Nested] >> 'b >> 'x

  checkAll("nested case class >> 'b >> 'n", lens.laws(bnLens.asScalaz))
  checkAll("nested case class >> 'b >> 'x", lens.laws(bxLens.asScalaz))

}
