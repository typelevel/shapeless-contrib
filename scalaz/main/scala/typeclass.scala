package shapeless.contrib.scalaz

import scalaz._

import shapeless._
import shapeless.contrib._

private trait Empty {

  def emptyProduct = new Monoid[HNil] with Order[HNil] with Show[HNil] {
    def zero = HNil
    def append(f1: HNil, f2: => HNil) = HNil
    override def equal(a1: HNil, a2: HNil) = true
    def order(x: HNil, y: HNil) = Monoid[Ordering].zero
    override def shows(f: HNil) = "HNil"
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

private trait ProductOrder[F, T <: HList]
  extends ProductEqual[F, T]
  with Order[F :: T]
  with Product[Order, F, T] {

  override def equal(a1: λ, a2: λ) =
    super[ProductEqual].equal(a1, a2)

  def order(x: λ, y: λ) =
    Semigroup[Ordering].append(F.order(x.head, y.head), T.order(x.tail, y.tail))

}

private trait ProductShow[F, T <: HList]
  extends Show[F :: T]
  with Product[Show, F, T] {

  override def shows(f: λ) =
    F.shows(f.head) ++ " :: " ++ T.shows(f.tail)

  override def show(f: λ) =
    F.show(f.head) ++ Cord(" :: ") ++ T.show(f.tail)

}


// Isos

private trait IsomorphicSemigroup[A, B]
  extends Semigroup[A]
  with Isomorphic[Semigroup, A, B] {

  def append(f1: A, f2: => A) =
    from(B.append(to(f1), to(f2)))

}

private trait IsomorphicMonoid[A, B]
  extends IsomorphicSemigroup[A, B]
  with Monoid[A]
  with Isomorphic[Monoid, A, B] {

  def zero = from(B.zero)

}

private trait IsomorphicEqual[A, B]
  extends Equal[A]
  with Isomorphic[Equal, A, B] {

  override def equal(a1: A, a2: A) =
    B.equal(to(a1), to(a2))

}

private trait IsomorphicOrder[A, B]
  extends IsomorphicEqual[A, B]
  with Order[A]
  with Isomorphic[Order, A, B] {

  override def equal(a1: A, a2: A) =
    super[IsomorphicEqual].equal(a1, a2)

  def order(x: A, y: A) =
    B.order(to(x), to(y))

}

private trait IsomorphicShow[A, B]
  extends Show[A]
  with Isomorphic[Show, A, B] {

  override def shows(f: A) =
    B.shows(to(f))

  override def show(f: A) =
    B.show(to(f))

}

trait Instances {

  // Instances

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

  implicit def EqualI: ProductTypeClass[Equal] = new ProductTypeClass[Equal] with Empty {
    def product[F, T <: HList](f: Equal[F], t: Equal[T]) =
      new ProductEqual[F, T] { def F = f; def T = t }
    def project[A, B](b: => Equal[B], ab: A => B, ba: B => A) =
      new IsomorphicEqual[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def ShowI: ProductTypeClass[Show] = new ProductTypeClass[Show] with Empty {
    def product[F, T <: HList](f: Show[F], t: Show[T]) =
      new ProductShow[F, T] { def F = f; def T = t }
    def project[A, B](b: => Show[B], ab: A => B, ba: B => A) =
      new IsomorphicShow[A, B] { def B = b; def to = ab; def from = ba }
  }

  implicit def OrderI: ProductTypeClass[Order] = new ProductTypeClass[Order] with Empty {
    def product[F, T <: HList](f: Order[F], t: Order[T]) =
      new ProductOrder[F, T] { def F = f; def T = t }
    def project[A, B](b: => Order[B], ab: A => B, ba: B => A) =
      new IsomorphicOrder[A, B] { def B = b; def to = ab; def from = ba }
  }


  // Boilerplate

  implicit def deriveSemigroup[T] = macro TypeClass.derive_impl[Semigroup, T]

  implicit def deriveMonoid[T] = macro TypeClass.derive_impl[Monoid, T]

  implicit def deriveEqual[T] = macro TypeClass.derive_impl[Equal, T]

  implicit def deriveShow[T] = macro TypeClass.derive_impl[Show, T]

  implicit def deriveOrder[T] = macro TypeClass.derive_impl[Order, T]

}

// vim: expandtab:ts=2:sw=2
