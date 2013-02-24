package shapeless.contrib.scalacheck

import shapeless._

import scalaz._
import scalaz.syntax.apply._
import scalaz.scalacheck.ScalaCheckBinding._

import org.scalacheck.{Gen, Arbitrary}

trait TypeClasses {

  implicit def ArbitraryI: TypeClass[Arbitrary] = new TypeClass[Arbitrary] {

    def emptyProduct = Arbitrary(Gen.value(HNil))

    def product[F, T <: HList](f: Arbitrary[F], t: Arbitrary[T]) =
      (f |@| t) { _ :: _ }

    def derive[A, B](b: Arbitrary[B], ab: Iso[A, B]) =
      b.map(ab.from)

  }

  implicit def deriveArbitrary[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Arbitrary, G]): Arbitrary[F] =
    TypeClass.deriveFromIso[Arbitrary, F, G]

}

object typeClasses extends TypeClasses

// vim: expandtab:ts=2:sw=2
