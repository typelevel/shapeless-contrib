package shapeless.contrib.scalaz

import scalaz._

sealed trait FreeInstances0 {

  implicit def freeEqual[F[+_], A](implicit A0: Equal[A], F0: shapeless.Lazy[Equal[F[Free[F, A]]]]): Equal[Free[F, A]] =
    new FreeEqual[F, A] {
      def F = F0.value
      def A = A0
    }
}

trait FreeInstances extends FreeInstances0 {

  implicit def freeOrder[F[+_], A](implicit A0: Order[A], F0: shapeless.Lazy[Order[F[Free[F, A]]]]): Order[Free[F, A]] =
    new Order[Free[F, A]] with FreeEqual[F, A] {
      def F = F0.value
      def A = A0

      def order(a: Free[F, A], b: Free[F, A]) =
        (a.resume, b.resume) match {
          case (-\/(a), -\/(b)) => F.order(a, b)
          case (\/-(a), \/-(b)) => A.order(a, b)
          case (\/-(_), -\/(_)) => Ordering.GT
          case (-\/(_), \/-(_)) => Ordering.LT
        }
    }

  implicit def freeShow[F[+_], A](implicit A: Show[A], F: shapeless.Lazy[Show[F[Free[F, A]]]]): Show[Free[F, A]] =
    Show.shows{_.resume match {
      case \/-(a) => "Return(" + A.shows(a) + ")"
      case -\/(a) => "Suspend(" + F.value.shows(a) + ")"
    }}

}

private sealed trait FreeEqual[F[+_], A] extends Equal[Free[F, A]]{
  def F: Equal[F[Free[F, A]]]
  def A: Equal[A]

  override final def equal(a: Free[F, A], b: Free[F, A]) =
    (a.resume, b.resume) match {
      case (-\/(a), -\/(b)) => F.equal(a, b)
      case (\/-(a), \/-(b)) => A.equal(a, b)
      case _ => false
    }
}

