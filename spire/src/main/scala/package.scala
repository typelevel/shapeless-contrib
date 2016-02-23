package shapeless.contrib

import _root_.spire.math._
import _root_.spire.algebra._

import shapeless._

package object spire {

  // Instances

  object EqDerivedOrphans extends ProductTypeClassCompanion[Eq] {
    object typeClass extends ProductTypeClass[Eq] with Empty {
      def product[F, T <: HList](f: Eq[F], t: Eq[T]) =
        new ProductEq[F, T] { def F = f; def T = t }
      def project[A, B](b: => Eq[B], ab: A => B, ba: B => A) =
        b on ab
    }
  }

  object OrderDerivedOrphans extends ProductTypeClassCompanion[Order] {
    object typeClass extends ProductTypeClass[Order] with Empty {
      def product[F, T <: HList](f: Order[F], t: Order[T]) =
        new ProductOrder[F, T] { def F = f; def T = t }
      def project[A, B](b: => Order[B], ab: A => B, ba: B => A) =
        b on ab
    }
  }

  object SemigroupDerivedOrphans extends ProductTypeClassCompanion[Semigroup] {
    object typeClass extends ProductTypeClass[Semigroup] with Empty {
      def product[F, T <: HList](f: Semigroup[F], t: Semigroup[T]) =
        new ProductSemigroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => Semigroup[B], ab: A => B, ba: B => A) =
        new IsomorphicSemigroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object MonoidDerivedOrphans extends ProductTypeClassCompanion[Monoid] {
    object typeClass extends ProductTypeClass[Monoid] with Empty {
      def product[F, T <: HList](f: Monoid[F], t: Monoid[T]) =
        new ProductMonoid[F, T] { def F = f; def T = t }
      def project[A, B](b: => Monoid[B], ab: A => B, ba: B => A) =
        new IsomorphicMonoid[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object GroupDerivedOrphans extends ProductTypeClassCompanion[Group] {
    object typeClass extends ProductTypeClass[Group] with Empty {
      def product[F, T <: HList](f: Group[F], t: Group[T]) =
        new ProductGroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => Group[B], ab: A => B, ba: B => A) =
        new IsomorphicGroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object AbGroupDerivedOrphans extends ProductTypeClassCompanion[AbGroup] {
    object typeClass extends ProductTypeClass[AbGroup] with Empty {
      def product[F, T <: HList](f: AbGroup[F], t: AbGroup[T]) =
        new ProductAbGroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => AbGroup[B], ab: A => B, ba: B => A) =
        new IsomorphicAbGroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object AdditiveSemigroupDerivedOrphans extends ProductTypeClassCompanion[AdditiveSemigroup] {
    object typeClass extends ProductTypeClass[AdditiveSemigroup] with Empty {
      def product[F, T <: HList](f: AdditiveSemigroup[F], t: AdditiveSemigroup[T]) =
        new ProductAdditiveSemigroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => AdditiveSemigroup[B], ab: A => B, ba: B => A) =
        new IsomorphicAdditiveSemigroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object AdditiveMonoidDerivedOrphans extends ProductTypeClassCompanion[AdditiveMonoid] {
    object typeClass extends ProductTypeClass[AdditiveMonoid] with Empty {
      def product[F, T <: HList](f: AdditiveMonoid[F], t: AdditiveMonoid[T]) =
        new ProductAdditiveMonoid[F, T] { def F = f; def T = t }
      def project[A, B](b: => AdditiveMonoid[B], ab: A => B, ba: B => A) =
        new IsomorphicAdditiveMonoid[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object AdditiveGroupDerivedOrphans extends ProductTypeClassCompanion[AdditiveGroup] {
    object typeClass extends ProductTypeClass[AdditiveGroup] with Empty {
      def product[F, T <: HList](f: AdditiveGroup[F], t: AdditiveGroup[T]) =
        new ProductAdditiveGroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => AdditiveGroup[B], ab: A => B, ba: B => A) =
        new IsomorphicAdditiveGroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object AdditiveAbGroupDerivedOrphans extends ProductTypeClassCompanion[AdditiveAbGroup] {
    object typeClass extends ProductTypeClass[AdditiveAbGroup] with Empty {
      def product[F, T <: HList](f: AdditiveAbGroup[F], t: AdditiveAbGroup[T]) =
        new ProductAdditiveAbGroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => AdditiveAbGroup[B], ab: A => B, ba: B => A) =
        new IsomorphicAdditiveAbGroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object MultiplicativeSemigroupDerivedOrphans extends ProductTypeClassCompanion[MultiplicativeSemigroup] {
    object typeClass extends ProductTypeClass[MultiplicativeSemigroup] with Empty {
      def product[F, T <: HList](f: MultiplicativeSemigroup[F], t: MultiplicativeSemigroup[T]) =
        new ProductMultiplicativeSemigroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => MultiplicativeSemigroup[B], ab: A => B, ba: B => A) =
        new IsomorphicMultiplicativeSemigroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object MultiplicativeMonoidDerivedOrphans extends ProductTypeClassCompanion[MultiplicativeMonoid] {
    object typeClass extends ProductTypeClass[MultiplicativeMonoid] with Empty {
      def product[F, T <: HList](f: MultiplicativeMonoid[F], t: MultiplicativeMonoid[T]) =
        new ProductMultiplicativeMonoid[F, T] { def F = f; def T = t }
      def project[A, B](b: => MultiplicativeMonoid[B], ab: A => B, ba: B => A) =
        new IsomorphicMultiplicativeMonoid[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object MultiplicativeGroupDerivedOrphans extends ProductTypeClassCompanion[MultiplicativeGroup] {
    object typeClass extends ProductTypeClass[MultiplicativeGroup] with Empty {
      def product[F, T <: HList](f: MultiplicativeGroup[F], t: MultiplicativeGroup[T]) =
        new ProductMultiplicativeGroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => MultiplicativeGroup[B], ab: A => B, ba: B => A) =
        new IsomorphicMultiplicativeGroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object MultiplicativeAbGroupDerivedOrphans extends ProductTypeClassCompanion[MultiplicativeAbGroup] {
    object typeClass extends ProductTypeClass[MultiplicativeAbGroup] with Empty {
      def product[F, T <: HList](f: MultiplicativeAbGroup[F], t: MultiplicativeAbGroup[T]) =
        new ProductMultiplicativeAbGroup[F, T] { def F = f; def T = t }
      def project[A, B](b: => MultiplicativeAbGroup[B], ab: A => B, ba: B => A) =
        new IsomorphicMultiplicativeAbGroup[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  implicit def deriveEq[T]
    (implicit orphan: Orphan[Eq, EqDerivedOrphans.type, T]): Eq[T] = orphan.instance

  implicit def deriveOrder[T]
    (implicit orphan: Orphan[Order, OrderDerivedOrphans.type, T]): Order[T] = orphan.instance

  implicit def deriveSemigroup[T]
    (implicit orphan: Orphan[Semigroup, SemigroupDerivedOrphans.type, T]): Semigroup[T] = orphan.instance

  implicit def deriveMonoid[T]
    (implicit orphan: Orphan[Monoid, MonoidDerivedOrphans.type, T]): Monoid[T] = orphan.instance

  implicit def deriveGroup[T]
    (implicit orphan: Orphan[Group, GroupDerivedOrphans.type, T]): Group[T] = orphan.instance

  implicit def deriveAbGroup[T]
    (implicit orphan: Orphan[AbGroup, AbGroupDerivedOrphans.type, T]): AbGroup[T] = orphan.instance

  implicit def deriveAdditiveSemigroup[T]
    (implicit orphan: Orphan[AdditiveSemigroup, AdditiveSemigroupDerivedOrphans.type, T]): AdditiveSemigroup[T] =
      orphan.instance

  implicit def deriveAdditiveMonoid[T]
    (implicit orphan: Orphan[AdditiveMonoid, AdditiveMonoidDerivedOrphans.type, T]): AdditiveMonoid[T] =
      orphan.instance

  implicit def deriveAdditiveGroup[T]
    (implicit orphan: Orphan[AdditiveGroup, AdditiveGroupDerivedOrphans.type, T]): AdditiveGroup[T] =
      orphan.instance

  implicit def deriveAdditiveAbGroup[T]
    (implicit orphan: Orphan[AdditiveAbGroup, AdditiveAbGroupDerivedOrphans.type, T]): AdditiveAbGroup[T] =
      orphan.instance

  implicit def deriveMultiplicativeSemigroup[T]
    (implicit orphan: Orphan[MultiplicativeSemigroup, MultiplicativeSemigroupDerivedOrphans.type, T]):
      MultiplicativeSemigroup[T] = orphan.instance

  implicit def deriveMultiplicativeMonoid[T]
    (implicit orphan: Orphan[MultiplicativeMonoid, MultiplicativeMonoidDerivedOrphans.type, T]):
      MultiplicativeMonoid[T] = orphan.instance

  implicit def deriveMultiplicativeGroup[T]
    (implicit orphan: Orphan[MultiplicativeGroup, MultiplicativeGroupDerivedOrphans.type, T]):
      MultiplicativeGroup[T] = orphan.instance

  implicit def deriveMultiplicativeAbGroup[T]
    (implicit orphan: Orphan[MultiplicativeAbGroup, MultiplicativeAbGroupDerivedOrphans.type, T]):
      MultiplicativeAbGroup[T] = orphan.instance
}
