package shapeless.contrib.scalaz

import scalaz.{Semigroup, Monoid, Equal, Order, Show, Ordering, Cord}

import shapeless._
import shapeless.contrib._

trait Empty {

  def emptyProduct = new Monoid[HNil] with Order[HNil] with Show[HNil] {
    def zero = HNil
    def append(f1: HNil, f2: => HNil) = HNil
    override def equal(a1: HNil, a2: HNil) = true
    def order(x: HNil, y: HNil) = Monoid[Ordering].zero
    override def shows(f: HNil) = "HNil"
  }

  def emptyCoproduct = new Monoid[CNil] with Order[CNil] with Show[CNil] {
    def zero = ???
    def append(f1: CNil, f2: => CNil) = f1
    def order(x: CNil, y: CNil) = Monoid[Ordering].zero
  }

}

// Products

trait ProductSemigroup[F, T <: HList]
  extends Semigroup[F :: T]
  with Product[Semigroup, F, T] {

  def append(f1: λ, f2: => λ) =
    F.append(f1.head, f2.head) :: T.append(f1.tail, f2.tail)

}

trait ProductMonoid[F, T <: HList]
  extends ProductSemigroup[F, T]
  with Monoid[F :: T]
  with Product[Monoid, F, T] {

  def zero = F.zero :: T.zero

}

trait ProductEqual[F, T <: HList]
  extends Equal[F :: T]
  with Product[Equal, F, T] {

  def equal(a1: λ, a2: λ) =
    F.equal(a1.head, a2.head) && T.equal(a1.tail, a2.tail)

}

trait ProductOrder[F, T <: HList]
  extends ProductEqual[F, T]
  with Order[F :: T]
  with Product[Order, F, T] {

  override def equal(a1: λ, a2: λ) =
    super[ProductEqual].equal(a1, a2)

  def order(x: λ, y: λ) =
    Semigroup[Ordering].append(F.order(x.head, y.head), T.order(x.tail, y.tail))

}

trait ProductShow[F, T <: HList]
  extends Show[F :: T]
  with Product[Show, F, T] {

  override def shows(f: λ) =
    F.shows(f.head) ++ " :: " ++ T.shows(f.tail)

  override def show(f: λ) =
    F.show(f.head) ++ Cord(" :: ") ++ T.show(f.tail)

}

// Coproducts

trait SumEqual[L, R <: Coproduct]
  extends Equal[L :+: R]
  with Sum[Equal, L, R] {

  def equal(a1: λ, a2: λ) = (a1, a2) match {
    case (Inl(l1), Inl(l2)) => L.equal(l1, l2)
    case (Inr(r1), Inr(r2)) => R.equal(r1, r2)
    case _ => false
  }

}

trait SumOrder[L, R <: Coproduct]
  extends SumEqual[L, R]
  with Order[L :+: R]
  with Sum[Order, L, R] {

  override def equal(a1: λ, a2: λ) =
    super[SumEqual].equal(a1, a2)

  def order(x: λ, y: λ) = (x, y) match {
    case (Inl(a), Inl(b)) => L.order(a, b)
    case (Inl(_), Inr(_)) => Ordering.LT
    case (Inr(_), Inl(_)) => Ordering.GT
    case (Inr(a), Inr(b)) => R.order(a, b)
  }

}

trait SumShow[L, R <: Coproduct]
  extends Show[L :+: R]
  with Sum[Show, L, R] {

  override def shows(f: λ) = f match {
    case Inl(l) => s"Inl(${L.shows(l)})"
    case Inr(r) => s"Inr(${R.shows(r)})"
  }

  override def show(f: λ) = f match {
    case Inl(l) => Cord("Inl(") ++ L.show(l) ++ Cord(")")
    case Inr(r) => Cord("Inr(") ++ R.show(r) ++ Cord(")")
  }

}

// Isos

trait IsomorphicSemigroup[A, B]
  extends Semigroup[A]
  with Isomorphic[Semigroup, A, B] {

  def append(f1: A, f2: => A) =
    from(B.append(to(f1), to(f2)))

}

trait IsomorphicMonoid[A, B]
  extends IsomorphicSemigroup[A, B]
  with Monoid[A]
  with Isomorphic[Monoid, A, B] {

  def zero = from(B.zero)

}

trait IsomorphicEqual[A, B]
  extends Equal[A]
  with Isomorphic[Equal, A, B] {

  override def equal(a1: A, a2: A) =
    B.equal(to(a1), to(a2))

}

trait IsomorphicOrder[A, B]
  extends IsomorphicEqual[A, B]
  with Order[A]
  with Isomorphic[Order, A, B] {

  override def equal(a1: A, a2: A) =
    super[IsomorphicEqual].equal(a1, a2)

  def order(x: A, y: A) =
    B.order(to(x), to(y))

}

trait IsomorphicShow[A, B]
  extends Show[A]
  with Isomorphic[Show, A, B] {

  override def shows(f: A) =
    B.shows(to(f))

  override def show(f: A) =
    B.show(to(f))

}

trait Instances {
  // Instances

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

  object EqualDerivedOrphans extends TypeClassCompanion[Equal] {
    object typeClass extends TypeClass[Equal] with Empty {
      def product[F, T <: HList](f: Equal[F], t: Equal[T]) =
        new ProductEqual[F, T] { def F = f; def T = t }
      def coproduct[L, R <: Coproduct](l: => Equal[L], r: => Equal[R]) =
        new SumEqual[L, R] { def L = l; def R = r }
      def project[A, B](b: => Equal[B], ab: A => B, ba: B => A) =
        new IsomorphicEqual[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object OrderDerivedOrphans extends TypeClassCompanion[Order] {
    object typeClass extends TypeClass[Order] with Empty {
      def product[F, T <: HList](f: Order[F], t: Order[T]) =
        new ProductOrder[F, T] { def F = f; def T = t }
      def coproduct[L, R <: Coproduct](l: => Order[L], r: => Order[R]) =
        new SumOrder[L, R] { def L = l; def R = r }
      def project[A, B](b: => Order[B], ab: A => B, ba: B => A) =
        new IsomorphicOrder[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  object ShowDerivedOrphans extends TypeClassCompanion[Show] {
    object typeClass extends TypeClass[Show] with Empty {
      def product[F, T <: HList](f: Show[F], t: Show[T]) =
        new ProductShow[F, T] { def F = f; def T = t }
      def coproduct[L, R <: Coproduct](l: => Show[L], r: => Show[R]) =
        new SumShow[L, R] { def L = l; def R = r }
      def project[A, B](b: => Show[B], ab: A => B, ba: B => A) =
        new IsomorphicShow[A, B] { def B = b; def to = ab; def from = ba }
    }
  }

  implicit def deriveSemigroup[T]
    (implicit orphan: Orphan[Semigroup, SemigroupDerivedOrphans.type, T]): Semigroup[T] = orphan.instance

  implicit def deriveMonoid[T]
    (implicit orphan: Orphan[Monoid, MonoidDerivedOrphans.type, T]): Monoid[T] = orphan.instance

  implicit def deriveEqual[T]
    (implicit orphan: Orphan[Equal, EqualDerivedOrphans.type, T]): Equal[T] = orphan.instance

  implicit def deriveOrder[T]
    (implicit orphan: Orphan[Order, OrderDerivedOrphans.type, T]): Order[T] = orphan.instance

  implicit def deriveShow[T]
    (implicit orphan: Orphan[Show, ShowDerivedOrphans.type, T]): Show[T] = orphan.instance
}
