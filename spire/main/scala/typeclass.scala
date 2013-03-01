package shapeless.contrib.spire

import spire.math._
import spire.algebra._

import shapeless._
import shapeless.contrib._

trait TypeClasses {

  private trait Empty {

    def emptyProduct = new Order[HNil] with AbGroup[HNil] with AdditiveAbGroup[HNil] with MultiplicativeAbGroup[HNil] {
      def eqv(x: HNil, y: HNil) = true
      override def neqv(x: HNil, y: HNil) = false
      def compare(x: HNil, y: HNil) = 0
      def op(x: HNil, y: HNil) = HNil
      def id = HNil
      def inverse(a: HNil) = HNil
      def plus(x: HNil, y: HNil) = HNil
      def zero = HNil
      def negate(x: HNil) = HNil
      override def minus(x: HNil, y: HNil) = HNil
      override def additive: AbGroup[HNil] = this
      def times(x: HNil, y: HNil) = HNil
      def one = HNil
      override def reciprocal(x: HNil) = HNil
      def div(x: HNil, y: HNil) = HNil
      override def multiplicative: AbGroup[HNil] = this
    }

  }


  // Products

  private trait ProductEq[F, T <: HList]
    extends Eq[F :: T]
    with Product[Eq, F, T] {

    def eqv(x: λ, y: λ) =
      F.eqv(x.head, y.head) && T.eqv(x.tail, y.tail)

    override def neqv(x: λ, y: λ) =
      F.neqv(x.head, y.head) || T.neqv(x.tail, y.tail)

  }

  private trait ProductOrder[F, T <: HList]
    extends ProductEq[F, T]
    with Order[F :: T]
    with Product[Order, F, T] {

    def compare(x: λ, y: λ) = {
      val headOrder = F.compare(x.head, y.head)
      if (headOrder < 0)
        headOrder
      else
        T.compare(x.tail, y.tail)
    }

  }

  private trait ProductSemigroup[F, T <: HList]
    extends Semigroup[F :: T]
    with Product[Semigroup, F, T] {

    def op(x: λ, y: λ) =
      F.op(x.head, y.head) :: T.op(x.tail, y.tail)

  }

  private trait ProductMonoid[F, T <: HList]
    extends ProductSemigroup[F, T]
    with Monoid[F :: T]
    with Product[Monoid, F, T] {

    def id = F.id :: T.id

  }

  private trait ProductGroup[F, T <: HList]
    extends ProductMonoid[F, T]
    with Group[F :: T]
    with Product[Group, F, T] {

    def inverse(a: λ) =
      F.inverse(a.head) :: T.inverse(a.tail)

  }

  private trait ProductAbGroup[F, T <: HList]
    extends ProductGroup[F, T]
    with AbGroup[F :: T]

  private trait ProductAdditiveSemigroup[F, T <: HList]
    extends AdditiveSemigroup[F :: T]
    with Product[AdditiveSemigroup, F, T] {

    def plus(x: λ, y: λ) =
      F.plus(x.head, y.head) :: T.plus(x.tail, y.tail)

  }

  private trait ProductAdditiveMonoid[F, T <: HList]
    extends ProductAdditiveSemigroup[F, T]
    with AdditiveMonoid[F :: T]
    with Product[AdditiveMonoid, F, T] {

    def zero = F.zero :: T.zero

  }

  private trait ProductAdditiveGroup[F, T <: HList]
    extends ProductAdditiveMonoid[F, T]
    with AdditiveGroup[F :: T]
    with Product[AdditiveGroup, F, T] {

    def negate(a: λ) =
      F.negate(a.head) :: T.negate(a.tail)

    override def minus(x: λ, y: λ) =
      F.minus(x.head, y.head) :: T.minus(x.tail, y.tail)

  }

  private trait ProductAdditiveAbGroup[F, T <: HList]
    extends ProductAdditiveGroup[F, T]
    with AdditiveAbGroup[F :: T]

  private trait ProductMultiplicativeSemigroup[F, T <: HList]
    extends MultiplicativeSemigroup[F :: T]
    with Product[MultiplicativeSemigroup, F, T] {

    def times(x: λ, y: λ) =
      F.times(x.head, y.head) :: T.times(x.tail, y.tail)

  }

  private trait ProductMultiplicativeMonoid[F, T <: HList]
    extends ProductMultiplicativeSemigroup[F, T]
    with MultiplicativeMonoid[F :: T]
    with Product[MultiplicativeMonoid, F, T] {

    def one = F.one :: T.one

  }

  private trait ProductMultiplicativeGroup[F, T <: HList]
    extends ProductMultiplicativeMonoid[F, T]
    with MultiplicativeGroup[F :: T]
    with Product[MultiplicativeGroup, F, T] {

    override def reciprocal(a: λ) =
      F.reciprocal(a.head) :: T.reciprocal(a.tail)

    def div(x: λ, y: λ) =
      F.div(x.head, y.head) :: T.div(x.tail, y.tail)

  }

  private trait ProductMultiplicativeAbGroup[F, T <: HList]
    extends ProductMultiplicativeGroup[F, T]
    with MultiplicativeAbGroup[F :: T]


  // Isos

  private trait IsomorphicSemigroup[A, B]
    extends Semigroup[A]
    with Isomorphic[Semigroup, A, B] {

    def op(x: A, y: A) =
      iso.from(B.op(iso.to(x), iso.to(y)))

  }

  private trait IsomorphicMonoid[A, B]
    extends IsomorphicSemigroup[A, B]
    with Monoid[A]
    with Isomorphic[Monoid, A, B] {

    def id = iso.from(B.id)

  }

  private trait IsomorphicGroup[A, B]
    extends IsomorphicMonoid[A, B]
    with Group[A]
    with Isomorphic[Group, A, B] {

    def inverse(a: A) =
      iso.from(B.inverse(iso.to(a)))

  }

  private trait IsomorphicAbGroup[A, B]
    extends IsomorphicGroup[A, B]
    with AbGroup[A]

  private trait IsomorphicAdditiveSemigroup[A, B]
    extends AdditiveSemigroup[A]
    with Isomorphic[AdditiveSemigroup, A, B] {

    def plus(x: A, y: A) =
      iso.from(B.plus(iso.to(x), iso.to(y)))

  }

  private trait IsomorphicAdditiveMonoid[A, B]
    extends IsomorphicAdditiveSemigroup[A, B]
    with AdditiveMonoid[A]
    with Isomorphic[AdditiveMonoid, A, B] {

    def zero = iso.from(B.zero)

  }

  private trait IsomorphicAdditiveGroup[A, B]
    extends IsomorphicAdditiveMonoid[A, B]
    with AdditiveGroup[A]
    with Isomorphic[AdditiveGroup, A, B] {

    def negate(a: A) =
      iso.from(B.negate(iso.to(a)))

    override def minus(x: A, y: A) =
      iso.from(B.minus(iso.to(x), iso.to(y)))

  }

  private trait IsomorphicAdditiveAbGroup[A, B]
    extends IsomorphicAdditiveGroup[A, B]
    with AdditiveAbGroup[A]

  private trait IsomorphicMultiplicativeSemigroup[A, B]
    extends MultiplicativeSemigroup[A]
    with Isomorphic[MultiplicativeSemigroup, A, B] {

    def times(x: A, y: A) =
      iso.from(B.times(iso.to(x), iso.to(y)))

  }

  private trait IsomorphicMultiplicativeMonoid[A, B]
    extends IsomorphicMultiplicativeSemigroup[A, B]
    with MultiplicativeMonoid[A]
    with Isomorphic[MultiplicativeMonoid, A, B] {

    def one = iso.from(B.one)

  }

  private trait IsomorphicMultiplicativeGroup[A, B]
    extends IsomorphicMultiplicativeMonoid[A, B]
    with MultiplicativeGroup[A]
    with Isomorphic[MultiplicativeGroup, A, B] {

    override def reciprocal(a: A) =
      iso.from(B.reciprocal(iso.to(a)))

    def div(x: A, y: A) =
      iso.from(B.div(iso.to(x), iso.to(y)))

  }

  private trait IsomorphicMultiplicativeAbGroup[A, B]
    extends IsomorphicMultiplicativeGroup[A, B]
    with MultiplicativeAbGroup[A]


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

object typeClasses extends TypeClasses

// vim: expandtab:ts=2:sw=2
