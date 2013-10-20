package shapeless.contrib

import shapeless._

import scalaz.{Coproduct => _, _}
import scalaz.syntax.apply._
import scalaz.scalacheck.ScalaCheckBinding._

import org.scalacheck.{Gen, Arbitrary}

package object scalacheck {

  implicit def ArbitraryI: TypeClass[Arbitrary] = new TypeClass[Arbitrary] {

    def emptyProduct = Arbitrary(Gen.value(HNil))

    def product[F, T <: HList](f: Arbitrary[F], t: Arbitrary[T]) =
      (f |@| t) { _ :: _ }

    def coproduct[L, R <: Coproduct](l: => Arbitrary[L], r: => Arbitrary[R]) =
      Arbitrary(Gen.oneOf(
        l.arbitrary.map(Inl(_): L :+: R), r.arbitrary.map(Inr(_): L :+: R)
      ))

    def project[A, B](b: => Arbitrary[B], ab: A => B, ba: B => A) =
      b.map(ba)

  }

  implicit def deriveArbitrary[T] = macro TypeClass.derive_impl[Arbitrary, T]

}

// vim: expandtab:ts=2:sw=2
