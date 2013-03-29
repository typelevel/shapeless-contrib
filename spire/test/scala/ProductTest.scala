package shapeless.contrib.spire

import shapeless.Iso
import shapeless.contrib.scalacheck._

import spire.algebra.{Laws, LawChecker}
import spire.std.int._
import spire.std.long._

class ProductTest extends LawChecker {

  case class OneElem(n: Int)
  implicit def OneIso = Iso.hlist(OneElem.apply _, OneElem.unapply _)

  checkAll("one element", Laws[OneElem].additiveAbGroup)
  checkAll("one element", Laws[OneElem].multiplicativeSemigroup)

  case class TwoElem(n: Int, m: Long)
  implicit def TwoIso = Iso.hlist(TwoElem.apply _, TwoElem.unapply _)

  checkAll("two elements", Laws[TwoElem].additiveAbGroup)
  checkAll("two elements", Laws[OneElem].multiplicativeSemigroup)

}

// vim: expandtab:ts=2:sw=2
