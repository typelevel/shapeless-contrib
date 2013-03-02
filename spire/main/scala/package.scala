package shapeless.contrib

import _root_.spire.math._
import _root_.spire.algebra._

import shapeless._

package object spire {

  // Instances

  implicit def EqI: TypeClass[Eq] = new TypeClass[Eq] with Empty {
    def product[F, T <: HList](f: Eq[F], t: Eq[T]) =
      new ProductEq[F, T] { def F = f; def T = t }
    def derive[A, B](b: Eq[B], ab: Iso[A, B]) =
      b on ab.to
  }

  implicit def OrderI: TypeClass[Order] = new TypeClass[Order] with Empty {
    def product[F, T <: HList](f: Order[F], t: Order[T]) =
      new ProductOrder[F, T] { def F = f; def T = t }
    def derive[A, B](b: Order[B], ab: Iso[A, B]) =
      b on ab.to
  }

  implicit def SemigroupI: TypeClass[Semigroup] = new TypeClass[Semigroup] with Empty {
    def product[F, T <: HList](f: Semigroup[F], t: Semigroup[T]) =
      new ProductSemigroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: Semigroup[B], ab: Iso[A, B]) =
      new IsomorphicSemigroup[A, B] { def B = b; def iso = ab }
  }

  implicit def MonoidI: TypeClass[Monoid] = new TypeClass[Monoid] with Empty {
    def product[F, T <: HList](f: Monoid[F], t: Monoid[T]) =
      new ProductMonoid[F, T] { def F = f; def T = t }
    def derive[A, B](b: Monoid[B], ab: Iso[A, B]) =
      new IsomorphicMonoid[A, B] { def B = b; def iso = ab }
  }

  implicit def GroupI: TypeClass[Group] = new TypeClass[Group] with Empty {
    def product[F, T <: HList](f: Group[F], t: Group[T]) =
      new ProductGroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: Group[B], ab: Iso[A, B]) =
      new IsomorphicGroup[A, B] { def B = b; def iso = ab }
  }

  implicit def AbGroupI: TypeClass[AbGroup] = new TypeClass[AbGroup] with Empty {
    def product[F, T <: HList](f: AbGroup[F], t: AbGroup[T]) =
      new ProductAbGroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: AbGroup[B], ab: Iso[A, B]) =
      new IsomorphicAbGroup[A, B] { def B = b; def iso = ab }
  }

  implicit def AdditiveSemigroupI: TypeClass[AdditiveSemigroup] = new TypeClass[AdditiveSemigroup] with Empty {
    def product[F, T <: HList](f: AdditiveSemigroup[F], t: AdditiveSemigroup[T]) =
      new ProductAdditiveSemigroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: AdditiveSemigroup[B], ab: Iso[A, B]) =
      new IsomorphicAdditiveSemigroup[A, B] { def B = b; def iso = ab }
  }

  implicit def AdditiveMonoidI: TypeClass[AdditiveMonoid] = new TypeClass[AdditiveMonoid] with Empty {
    def product[F, T <: HList](f: AdditiveMonoid[F], t: AdditiveMonoid[T]) =
      new ProductAdditiveMonoid[F, T] { def F = f; def T = t }
    def derive[A, B](b: AdditiveMonoid[B], ab: Iso[A, B]) =
      new IsomorphicAdditiveMonoid[A, B] { def B = b; def iso = ab }
  }

  implicit def AdditiveGroupI: TypeClass[AdditiveGroup] = new TypeClass[AdditiveGroup] with Empty {
    def product[F, T <: HList](f: AdditiveGroup[F], t: AdditiveGroup[T]) =
      new ProductAdditiveGroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: AdditiveGroup[B], ab: Iso[A, B]) =
      new IsomorphicAdditiveGroup[A, B] { def B = b; def iso = ab }
  }

  implicit def AdditiveAbGroupI: TypeClass[AdditiveAbGroup] = new TypeClass[AdditiveAbGroup] with Empty {
    def product[F, T <: HList](f: AdditiveAbGroup[F], t: AdditiveAbGroup[T]) =
      new ProductAdditiveAbGroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: AdditiveAbGroup[B], ab: Iso[A, B]) =
      new IsomorphicAdditiveAbGroup[A, B] { def B = b; def iso = ab }
  }

  implicit def MultiplicativeSemigroupI: TypeClass[MultiplicativeSemigroup] = new TypeClass[MultiplicativeSemigroup] with Empty {
    def product[F, T <: HList](f: MultiplicativeSemigroup[F], t: MultiplicativeSemigroup[T]) =
      new ProductMultiplicativeSemigroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: MultiplicativeSemigroup[B], ab: Iso[A, B]) =
      new IsomorphicMultiplicativeSemigroup[A, B] { def B = b; def iso = ab }
  }

  implicit def MultiplicativeMonoidI: TypeClass[MultiplicativeMonoid] = new TypeClass[MultiplicativeMonoid] with Empty {
    def product[F, T <: HList](f: MultiplicativeMonoid[F], t: MultiplicativeMonoid[T]) =
      new ProductMultiplicativeMonoid[F, T] { def F = f; def T = t }
    def derive[A, B](b: MultiplicativeMonoid[B], ab: Iso[A, B]) =
      new IsomorphicMultiplicativeMonoid[A, B] { def B = b; def iso = ab }
  }

  implicit def MultiplicativeGroupI: TypeClass[MultiplicativeGroup] = new TypeClass[MultiplicativeGroup] with Empty {
    def product[F, T <: HList](f: MultiplicativeGroup[F], t: MultiplicativeGroup[T]) =
      new ProductMultiplicativeGroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: MultiplicativeGroup[B], ab: Iso[A, B]) =
      new IsomorphicMultiplicativeGroup[A, B] { def B = b; def iso = ab }
  }

  implicit def MultiplicativeAbGroupI: TypeClass[MultiplicativeAbGroup] = new TypeClass[MultiplicativeAbGroup] with Empty {
    def product[F, T <: HList](f: MultiplicativeAbGroup[F], t: MultiplicativeAbGroup[T]) =
      new ProductMultiplicativeAbGroup[F, T] { def F = f; def T = t }
    def derive[A, B](b: MultiplicativeAbGroup[B], ab: Iso[A, B]) =
      new IsomorphicMultiplicativeAbGroup[A, B] { def B = b; def iso = ab }
  }


  // Boilerplate

  implicit def deriveEq[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Eq, G]): Eq[F] =
    TypeClass.deriveFromIso[Eq, F, G]

  implicit def deriveOrder[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Order, G]): Order[F] =
    TypeClass.deriveFromIso[Order, F, G]

  implicit def deriveSemigroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Semigroup, G]): Semigroup[F] =
    TypeClass.deriveFromIso[Semigroup, F, G]

  implicit def deriveMonoid[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Monoid, G]): Monoid[F] =
    TypeClass.deriveFromIso[Monoid, F, G]

  implicit def deriveGroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Group, G]): Monoid[F] =
    TypeClass.deriveFromIso[Group, F, G]

  implicit def deriveAbGroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[AbGroup, G]): AbGroup[F] =
    TypeClass.deriveFromIso[AbGroup, F, G]

  implicit def deriveAdditiveSemigroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[AdditiveSemigroup, G]): AdditiveSemigroup[F] =
    TypeClass.deriveFromIso[AdditiveSemigroup, F, G]

  implicit def deriveAdditiveMonoid[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[AdditiveMonoid, G]): AdditiveMonoid[F] =
    TypeClass.deriveFromIso[AdditiveMonoid, F, G]

  implicit def deriveAdditiveGroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[AdditiveGroup, G]): AdditiveGroup[F] =
    TypeClass.deriveFromIso[AdditiveGroup, F, G]

  implicit def deriveAdditiveAbGroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[AdditiveAbGroup, G]): AdditiveAbGroup[F] =
    TypeClass.deriveFromIso[AdditiveAbGroup, F, G]

  implicit def deriveMultiplicativeSemigroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[MultiplicativeSemigroup, G]): MultiplicativeSemigroup[F] =
    TypeClass.deriveFromIso[MultiplicativeSemigroup, F, G]

  implicit def deriveMultiplicativeMonoid[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[MultiplicativeMonoid, G]): MultiplicativeMonoid[F] =
    TypeClass.deriveFromIso[MultiplicativeMonoid, F, G]

  implicit def deriveMultiplicativeGroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[MultiplicativeGroup, G]): MultiplicativeGroup[F] =
    TypeClass.deriveFromIso[MultiplicativeGroup, F, G]

  implicit def deriveMultiplicativeAbGroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[MultiplicativeAbGroup, G]): MultiplicativeAbGroup[F] =
    TypeClass.deriveFromIso[MultiplicativeAbGroup, F, G]

}

// vim: expandtab:ts=2:sw=2
