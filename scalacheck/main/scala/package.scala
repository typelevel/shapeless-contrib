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
          val half = size.abs/2
          val resizedH = Gen.resize(half, h.arbitrary)
          val resizedT = Gen.resize(half, t.arbitrary)
          for { h <- resizedH; t <- resizedT }
            yield h :: t
        }})

    def coproduct[L, R <: Coproduct](l: => Arbitrary[L], r: => Arbitrary[R]) = {
      val gens: List[Gen[L :+: R]] =
        (if (l.arbitrary == _emptyCoproduct) Nil else List(l.arbitrary.map(Inl(_): L :+: R))) ++
        (if (r.arbitrary == _emptyCoproduct) Nil else List(r.arbitrary.map(Inr(_): L :+: R)))
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
