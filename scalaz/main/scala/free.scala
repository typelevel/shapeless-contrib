package shapeless.contrib.scalaz

import shapeless.Lazy

import scalaz._

sealed trait FreeInstances0 {

  implicit def freeEqual[F[_], A](implicit A0: Equal[A], FF0: Lazy[Equal[F[Free[F, A]]]], F0: Functor[F]): Equal[Free[F, A]] =
    new FreeEqual[F, A] {
      def FF = FF0.value
      def F = F0
      def A = A0
    }
}

trait FreeInstances extends FreeInstances0 {

  implicit def freeOrder[F[_], A](implicit A0: Order[A], FF0: Lazy[Order[F[Free[F, A]]]], F0: Functor[F]): Order[Free[F, A]] =
    new FreeOrder[F, A] {
      def FF = FF0.value
      def F = F0
      def A = A0
    }

  implicit def freeShow[F[_], A](implicit A: Show[A], FF: Lazy[Show[F[Free[F, A]]]], F0: Functor[F]): Show[Free[F, A]] =
    Show.shows{_.resume match {
      case \/-(a) => "Return(" + A.shows(a) + ")"
      case -\/(a) => "Suspend(" + FF.value.shows(a) + ")"
    }}

}

private sealed trait FreeEqual[F[_], A] extends Equal[Free[F, A]] {
  def FF: Equal[F[Free[F, A]]]
  def A: Equal[A]
  implicit def F: Functor[F]

  override final def equal(a: Free[F, A], b: Free[F, A]) =
    (a.resume, b.resume) match {
      case (-\/(a), -\/(b)) => FF.equal(a, b)
      case (\/-(a), \/-(b)) => A.equal(a, b)
      case _ => false
    }
}

private sealed trait FreeOrder[F[_], A] extends Order[Free[F, A]] with FreeEqual[F, A] {
  def FF: Order[F[Free[F, A]]]
  def A: Order[A]
  implicit def F: Functor[F]

  def order(a: Free[F, A], b: Free[F, A]) =
    (a.resume, b.resume) match {
      case (-\/(a), -\/(b)) => FF.order(a, b)
      case (\/-(a), \/-(b)) => A.order(a, b)
      case (\/-(_), -\/(_)) => Ordering.GT
      case (-\/(_), \/-(_)) => Ordering.LT
    }
}
