package shapeless.contrib.spire

import spire.math._
import spire.algebra._

import shapeless._
import shapeless.contrib._

private trait Empty {

  def emptyProduct = new Order[HNil] with AbGroup[HNil] with AdditiveAbGroup[HNil] with MultiplicativeAbGroup[HNil] {
    override def eqv(x: HNil, y: HNil) = true
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

  override def eqv(x: λ, y: λ) =
    super[ProductEq].eqv(x, y)

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

// vim: expandtab:ts=2:sw=2
