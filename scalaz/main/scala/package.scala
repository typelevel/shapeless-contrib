package shapeless.contrib

import _root_.scalaz._

import shapeless._
import shapeless.contrib._

package object scalaz {

  // Instances

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

  implicit def EqualI: TypeClass[Equal] = new TypeClass[Equal] with Empty {
    def product[F, T <: HList](f: Equal[F], t: Equal[T]) =
      new ProductEqual[F, T] { def F = f; def T = t }
    def derive[A, B](b: Equal[B], ab: Iso[A, B]) =
      new IsomorphicEqual[A, B] { def B = b; def iso = ab }
  }

  implicit def ShowI: TypeClass[Show] = new TypeClass[Show] with Empty {
    def product[F, T <: HList](f: Show[F], t: Show[T]) =
      new ProductShow[F, T] { def F = f; def T = t }
    def derive[A, B](b: Show[B], ab: Iso[A, B]) =
      new IsomorphicShow[A, B] { def B = b; def iso = ab }
  }

  implicit def OrderI: TypeClass[Order] = new TypeClass[Order] with Empty {
    def product[F, T <: HList](f: Order[F], t: Order[T]) =
      new ProductOrder[F, T] { def F = f; def T = t }
    def derive[A, B](b: Order[B], ab: Iso[A, B]) =
      new IsomorphicOrder[A, B] { def B = b; def iso = ab }
  }


  // Boilerplate

  implicit def deriveSemigroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Semigroup, G]): Semigroup[F] =
    TypeClass.deriveFromIso[Semigroup, F, G]

  implicit def deriveMonoid[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Monoid, G]): Monoid[F] =
    TypeClass.deriveFromIso[Monoid, F, G]

  implicit def deriveEqual[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Equal, G]): Equal[F] =
    TypeClass.deriveFromIso[Equal, F, G]

  implicit def deriveShow[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Show, G]): Show[F] =
    TypeClass.deriveFromIso[Show, F, G]

  implicit def deriveOrder[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Order, G]): Order[F] =
    TypeClass.deriveFromIso[Order, F, G]

}

// vim: expandtab:ts=2:sw=2
