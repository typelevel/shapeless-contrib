package shapeless.contrib.scalaz

import scalaz._

import shapeless._
import shapeless.contrib._

trait TypeClasses {

  private trait Empty {
    def emptyProduct = new Monoid[HNil] with Equal[HNil] {
      def zero = HNil
      def append(f1: HNil, f2: => HNil) = HNil
      def equal(a1: HNil, a2: HNil) = true
    }
  }


  // Products

  private trait ProductSemigroup[F, T <: HList]
    extends Semigroup[F :: T]
    with Product[Semigroup, F, T] {

    def append(f1: λ, f2: => λ) =
      F.append(f1.head, f2.head) :: T.append(f1.tail, f2.tail)

  }

  private trait ProductMonoid[F, T <: HList]
    extends ProductSemigroup[F, T]
    with Monoid[F :: T]
    with Product[Monoid, F, T] {

    def zero = F.zero :: T.zero

  }

  private trait ProductEqual[F, T <: HList]
    extends Equal[F :: T]
    with Product[Equal, F, T] {

    def equal(a1: λ, a2: λ) =
      F.equal(a1.head, a2.head) && T.equal(a1.tail, a2.tail)

  }


  // Isos

  private trait IsomorphicSemigroup[A, B]
    extends Semigroup[A]
    with Isomorphic[Semigroup, A, B] {

    def append(f1: A, f2: => A): A =
      iso.from(B.append(iso.to(f1), iso.to(f2)))

  }

  private trait IsomorphicMonoid[A, B]
    extends IsomorphicSemigroup[A, B]
    with Monoid[A]
    with Isomorphic[Monoid, A, B] {

    def zero: A = iso.from(B.zero)

  }

  private trait IsomorphicEqual[A, B]
    extends Equal[A]
    with Isomorphic[Equal, A, B] {

    def equal(a1: A, a2: A): Boolean =
      B.equal(iso.to(a1), iso.to(a2))

  }


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


  // Boilerplate

  implicit def deriveSemigroup[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Semigroup, G]): Semigroup[F] =
    TypeClass.deriveFromIso[Semigroup, F, G]

  implicit def deriveMonoid[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Monoid, G]): Monoid[F] =
    TypeClass.deriveFromIso[Monoid, F, G]

  implicit def deriveEqual[F, G <: HList](implicit iso: Iso[F, G], hlistInst: TypeClass.HListInstance[Equal, G]): Equal[F] =
    TypeClass.deriveFromIso[Equal, F, G]

}

object typeClasses extends TypeClasses

// vim: expandtab:ts=2:sw=2
