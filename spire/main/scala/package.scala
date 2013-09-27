package shapeless.contrib

import _root_.spire.math._
import _root_.spire.algebra._

import shapeless._

package object spire {

  // Instances

  implicit def EqI: ProductTypeClass[Eq] = new ProductTypeClass[Eq] with Empty {
    def product[F, T <: HList](f: Eq[F], t: Eq[T]) =
      new ProductEq[F, T] { def F = f; def T = t }
    def project[A, B](b: => Eq[B], ab: A => B, ba: B => A) =
      b on ab
  }

  implicit def OrderI: ProductTypeClass[Order] = new ProductTypeClass[Order] with Empty {
    def product[F, T <: HList](f: Order[F], t: Order[T]) =
      new ProductOrder[F, T] { def F = f; def T = t }
    def project[A, B](b: => Order[B], ab: A => B, ba: B => A) =
      b on ab
  }

  implicit def SemigroupI: ProductTypeClass[Semigroup] = new ProductTypeClass[Semigroup] with Empty {
    def product[F, T <: HList](f: Semigroup[F], t: Semigroup[T]) =
      new ProductSemigroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => Semigroup[B], ab: A => B, ba: B => A) =
      new IsomorphicSemigroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def MonoidI: ProductTypeClass[Monoid] = new ProductTypeClass[Monoid] with Empty {
    def product[F, T <: HList](f: Monoid[F], t: Monoid[T]) =
      new ProductMonoid[F, T] { def F = f; def T = t }
    def project[A, B](b: => Monoid[B], ab: A => B, ba: B => A) =
      new IsomorphicMonoid[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def GroupI: ProductTypeClass[Group] = new ProductTypeClass[Group] with Empty {
    def product[F, T <: HList](f: Group[F], t: Group[T]) =
      new ProductGroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => Group[B], ab: A => B, ba: B => A) =
      new IsomorphicGroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def AbGroupI: ProductTypeClass[AbGroup] = new ProductTypeClass[AbGroup] with Empty {
    def product[F, T <: HList](f: AbGroup[F], t: AbGroup[T]) =
      new ProductAbGroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => AbGroup[B], ab: A => B, ba: B => A) =
      new IsomorphicAbGroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def AdditiveSemigroupI: ProductTypeClass[AdditiveSemigroup] = new ProductTypeClass[AdditiveSemigroup] with Empty {
    def product[F, T <: HList](f: AdditiveSemigroup[F], t: AdditiveSemigroup[T]) =
      new ProductAdditiveSemigroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => AdditiveSemigroup[B], ab: A => B, ba: B => A) =
      new IsomorphicAdditiveSemigroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def AdditiveMonoidI: ProductTypeClass[AdditiveMonoid] = new ProductTypeClass[AdditiveMonoid] with Empty {
    def product[F, T <: HList](f: AdditiveMonoid[F], t: AdditiveMonoid[T]) =
      new ProductAdditiveMonoid[F, T] { def F = f; def T = t }
    def project[A, B](b: => AdditiveMonoid[B], ab: A => B, ba: B => A) =
      new IsomorphicAdditiveMonoid[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def AdditiveGroupI: ProductTypeClass[AdditiveGroup] = new ProductTypeClass[AdditiveGroup] with Empty {
    def product[F, T <: HList](f: AdditiveGroup[F], t: AdditiveGroup[T]) =
      new ProductAdditiveGroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => AdditiveGroup[B], ab: A => B, ba: B => A) =
      new IsomorphicAdditiveGroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def AdditiveAbGroupI: ProductTypeClass[AdditiveAbGroup] = new ProductTypeClass[AdditiveAbGroup] with Empty {
    def product[F, T <: HList](f: AdditiveAbGroup[F], t: AdditiveAbGroup[T]) =
      new ProductAdditiveAbGroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => AdditiveAbGroup[B], ab: A => B, ba: B => A) =
      new IsomorphicAdditiveAbGroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def MultiplicativeSemigroupI: ProductTypeClass[MultiplicativeSemigroup] = new ProductTypeClass[MultiplicativeSemigroup] with Empty {
    def product[F, T <: HList](f: MultiplicativeSemigroup[F], t: MultiplicativeSemigroup[T]) =
      new ProductMultiplicativeSemigroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => MultiplicativeSemigroup[B], ab: A => B, ba: B => A) =
      new IsomorphicMultiplicativeSemigroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def MultiplicativeMonoidI: ProductTypeClass[MultiplicativeMonoid] = new ProductTypeClass[MultiplicativeMonoid] with Empty {
    def product[F, T <: HList](f: MultiplicativeMonoid[F], t: MultiplicativeMonoid[T]) =
      new ProductMultiplicativeMonoid[F, T] { def F = f; def T = t }
    def project[A, B](b: => MultiplicativeMonoid[B], ab: A => B, ba: B => A) =
      new IsomorphicMultiplicativeMonoid[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def MultiplicativeGroupI: ProductTypeClass[MultiplicativeGroup] = new ProductTypeClass[MultiplicativeGroup] with Empty {
    def product[F, T <: HList](f: MultiplicativeGroup[F], t: MultiplicativeGroup[T]) =
      new ProductMultiplicativeGroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => MultiplicativeGroup[B], ab: A => B, ba: B => A) =
      new IsomorphicMultiplicativeGroup[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def MultiplicativeAbGroupI: ProductTypeClass[MultiplicativeAbGroup] = new ProductTypeClass[MultiplicativeAbGroup] with Empty {
    def product[F, T <: HList](f: MultiplicativeAbGroup[F], t: MultiplicativeAbGroup[T]) =
      new ProductMultiplicativeAbGroup[F, T] { def F = f; def T = t }
    def project[A, B](b: => MultiplicativeAbGroup[B], ab: A => B, ba: B => A) =
      new IsomorphicMultiplicativeAbGroup[A, B] { def B = b; def to = ab; def from = ba }
  }


  // Boilerplate

  implicit def deriveEq[T] = macro TypeClass.derive_impl[Eq, T]

  implicit def deriveOrder[T] = macro TypeClass.derive_impl[Order, T]

  implicit def deriveSemigroup[T] =
    macro TypeClass.derive_impl[Semigroup, T]

  implicit def deriveMonoid[T] = macro TypeClass.derive_impl[Monoid, T]

  implicit def deriveGroup[T] = macro TypeClass.derive_impl[Group, T]

  implicit def deriveAbGroup[T] = macro TypeClass.derive_impl[AbGroup, T]

  implicit def deriveAdditiveSemigroup[T] =
    macro TypeClass.derive_impl[AdditiveSemigroup, T]

  implicit def deriveAdditiveMonoid[T] =
    macro TypeClass.derive_impl[AdditiveMonoid, T]

  implicit def deriveAdditiveGroup[T] =
    macro TypeClass.derive_impl[AdditiveGroup, T]

  implicit def deriveAdditiveAbGroup[T] =
    macro TypeClass.derive_impl[AdditiveAbGroup, T]

  implicit def deriveMultiplicativeSemigroup[T] =
    macro TypeClass.derive_impl[MultiplicativeSemigroup, T]

  implicit def deriveMultiplicativeMonoid[T] =
    macro TypeClass.derive_impl[MultiplicativeMonoid, T]

  implicit def deriveMultiplicativeGroup[T] =
    macro TypeClass.derive_impl[MultiplicativeGroup, T]

  implicit def deriveMultiplicativeAbGroup[T] =
    macro TypeClass.derive_impl[MultiplicativeAbGroup, T]
}

// vim: expandtab:ts=2:sw=2
