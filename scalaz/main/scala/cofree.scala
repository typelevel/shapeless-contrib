package shapeless.contrib.scalaz

import scalaz._

sealed trait CofreeInstances0 {

  implicit final def cofreeEqual[F[+_], A](implicit A0: Equal[A], F0: shapeless.Lazy[Equal[F[Cofree[F, A]]]]): Equal[Cofree[F, A]] =
    new CofreeEqual[F, A] {
      def F = F0.value
      def A = A0
    }
}

trait CofreeInstances extends CofreeInstances0 {

  implicit final def cofreeOrder[F[+_], A](implicit A0: Order[A], F0: shapeless.Lazy[Order[F[Cofree[F, A]]]]): Order[Cofree[F, A]] =
    new Order[Cofree[F, A]] with CofreeEqual[F, A] {
      def F = F0.value
      def A = A0

      def order(a: Cofree[F, A], b: Cofree[F, A]) = A.order(a.head, b.head) match {
        case Ordering.EQ => F.order(a.tail, b.tail)
        case o => o
      }
    }

  implicit final def cofreeShow[F[+_], A](implicit A: Show[A], F: shapeless.Lazy[Show[F[Cofree[F, A]]]]): Show[Cofree[F, A]] =
    Show.shows{ a =>
      "Cofree(" + A.shows(a.head) + "," + F.value.shows(a.tail) + ")"
    }

}

private sealed trait CofreeEqual[F[+_], A] extends Equal[Cofree[F, A]]{
  def F: Equal[F[Cofree[F, A]]]
  def A: Equal[A]

  override final def equal(a: Cofree[F, A], b: Cofree[F, A]) =
    A.equal(a.head, b.head) && F.equal(a.tail, b.tail)
}

