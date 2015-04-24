package shapeless.contrib

import shapeless._

import org.scalacheck.{Gen, Arbitrary, Shrink}

object scalacheck {
  object ArbitraryDerivedOrphans extends TypeClassCompanion[Arbitrary] {
    object typeClass extends TypeClass[Arbitrary] {
      // TODO this is terrible
      private lazy val _emptyCoproduct: Gen[Nothing] = Gen.fail

      def emptyProduct = Arbitrary(Gen.const(HNil: HNil))

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

      def emptyCoproduct = Arbitrary[CNil](_emptyCoproduct)

      def coproduct[L, R <: Coproduct](l: => Arbitrary[L], r: => Arbitrary[R]) = {
        val rGen = r.arbitrary
        val gens: List[Gen[L :+: R]] =
          l.arbitrary.map(Inl(_): L :+: R) ::
          (if (rGen == _emptyCoproduct) Nil else List(rGen.map(Inr(_): L :+: R)))
        Arbitrary(Gen.oneOf(gens).flatMap(identity))
      }

      def project[A, B](b: => Arbitrary[B], ab: A => B, ba: B => A) =
        Arbitrary(b.arbitrary.map(ba))
    }
  }

  object ShrinkDerivedOrphans extends TypeClassCompanion[Shrink] {
    object typeClass extends TypeClass[Shrink] {
      def emptyProduct = Shrink(_ => Stream.empty)

      def product[F, T <: HList](f: Shrink[F], t: Shrink[T]) = Shrink { case a :: b ⇒
        f.shrink(a).map( _ :: b) append
        t.shrink(b).map(a :: _)
      }

      def emptyCoproduct: Shrink[CNil] = Shrink(_ => Stream.empty)

      def coproduct[L, R <: Coproduct](sl: => Shrink[L], sr: => Shrink[R]) = Shrink { lr =>
        lr match {
          case Inl(l) ⇒ sl.shrink(l).map(Inl.apply)
          case Inr(r) ⇒ sr.shrink(r).map(Inr.apply)
        }
      }

      def project[A, B](b: => Shrink[B], ab: A => B, ba: B => A) = Shrink { a =>
        b.shrink(ab(a)).map(ba)
      }
    }
  }

  implicit def deriveArbitrary[T]
    (implicit orphan: Orphan[Arbitrary, ArbitraryDerivedOrphans.type, T]): Arbitrary[T] = orphan.instance

  implicit def deriveShrink[T]
    (implicit orphan: Orphan[Shrink, ShrinkDerivedOrphans.type, T]): Shrink[T] = orphan.instance
}
