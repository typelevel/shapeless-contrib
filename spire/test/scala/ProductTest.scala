package shapeless.contrib.spire

import shapeless.contrib.scalacheck._

import spire.algebra._
import spire.std.int._
import spire.std.long._
import spire.laws._

class ProductTest extends LawChecker {

  case class OneElem(n: Int)

  checkAll("one element", GroupLaws[OneElem].additiveAbGroup)
  checkAll("one element", RingLaws[OneElem].multiplicativeSemigroup)

  case class TwoElem(n: Int, m: Long)

  checkAll("two elements", GroupLaws[TwoElem].additiveAbGroup)
  checkAll("two elements", RingLaws[OneElem].multiplicativeSemigroup)

}

// vim: expandtab:ts=2:sw=2
