package shapeless.contrib.scalaz

import shapeless.Lens
import shapeless.Lens._
import shapeless.Nat._
import shapeless.contrib.scalacheck._

import org.specs2.scalaz.Spec

import scalaz.scalacheck.ScalazProperties._

class LensTest extends Spec {

  import scalaz.std.anyVal._
  import scalaz.std.string._

  case class TwoElem(n: Int, x: String)

  val nLens = Lens[TwoElem] >> _0
  val xLens = Lens[TwoElem] >> _1

  checkAll("case class >>  _0", lens.laws(nLens.asScalaz))
  checkAll("case class >>  _1", lens.laws(xLens.asScalaz))

  case class Nested(a: Int, b: TwoElem)

  val bnLens = Lens[Nested] >> _1 >> _0
  val bxLens = Lens[Nested] >> _1 >> _1

  checkAll("nested case class >> _0", lens.laws(bnLens.asScalaz))
  checkAll("nested case class >> _1", lens.laws(bxLens.asScalaz))

}

// vim: expandtab:ts=2:sw=2
