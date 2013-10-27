package shapeless.contrib

import shapeless._

import org.scalacheck.{Gen, Arbitrary}

package object scalacheck {

  implicit def ArbitraryI: TypeClass[Arbitrary] = new TypeClass[Arbitrary] {

    def emptyProduct = Arbitrary(Gen.value(HNil))

    def product[F, T <: HList](f: Arbitrary[F], t: Arbitrary[T]) =
      Arbitrary(for { fv <- f.arbitrary; tv <- t.arbitrary } yield fv :: tv)

    def coproduct[L, R <: Coproduct](l: => Arbitrary[L], r: => Arbitrary[R]) = {
      lazy val mappedL = l.arbitrary.map(Inl(_): L :+: R)
      lazy val mappedR = r.arbitrary.map(Inr(_): L :+: R)
      Arbitrary(for {
        which <- Gen.oneOf(false, true)
        result <- if (which) mappedL else mappedR
      } yield result)
    }

    def project[A, B](b: => Arbitrary[B], ab: A => B, ba: B => A) =
      Arbitrary(b.arbitrary.map(ba))

  }

  implicit def deriveArbitrary[T] = macro TypeClass.derive_impl[Arbitrary, T]

}

// vim: expandtab:ts=2:sw=2
