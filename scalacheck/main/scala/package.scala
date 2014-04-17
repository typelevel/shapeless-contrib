package shapeless.contrib

import shapeless._

import org.scalacheck.{Gen, Arbitrary}

package object scalacheck {

  // TODO this is terrible
  private lazy val _emptyCoproduct: Gen[Nothing] = Gen(_ => None)

  implicit def ArbitraryI: TypeClass[Arbitrary] = new TypeClass[Arbitrary] {

    def emptyProduct = Arbitrary(Gen.value(HNil))

    def product[H, T <: HList](h: Arbitrary[H], t: Arbitrary[T]) =
      Arbitrary(Gen.sized { size =>
        if (size == 0)
          Gen.fail
        else {
          val resizedH = Gen.resize(size.abs/2, h.arbitrary)
          val resizedT = Gen.resize(size.abs - size.abs/2, t.arbitrary)
          for { h <- resizedH; t <- resizedT }
            yield h :: t
        }})

    def coproduct[L, R <: Coproduct](l: => Arbitrary[L], r: => Arbitrary[R]) = {
      val rGen = r.arbitrary
      val gens: List[Gen[L :+: R]] =
        l.arbitrary.map(Inl(_): L :+: R) ::
        (if (rGen == _emptyCoproduct) Nil else List(rGen.map(Inr(_): L :+: R)))
      Arbitrary(Gen.oneOf(gens).flatMap(identity))
    }

    def emptyCoproduct =
      Arbitrary(_emptyCoproduct)

    def project[A, B](b: => Arbitrary[B], ab: A => B, ba: B => A) =
      Arbitrary(b.arbitrary.map(ba))

  }

  implicit def deriveArbitrary[T](implicit ev: TypeClass[Arbitrary]): Arbitrary[T] =
    macro GenericMacros.deriveInstance[Arbitrary, T]

}
