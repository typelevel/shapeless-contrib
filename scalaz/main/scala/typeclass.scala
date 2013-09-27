package shapeless.contrib.scalaz

import scalaz._

import shapeless._
import shapeless.contrib._

trait Instances {

  implicit def deriveSemigroup[T] = macro TypeClass.derive_impl[Semigroup, T]

  implicit def deriveMonoid[T] = macro TypeClass.derive_impl[Monoid, T]

  implicit def deriveEqual[T] = macro TypeClass.derive_impl[Equal, T]

  implicit def deriveShow[T] = macro TypeClass.derive_impl[Show, T]

  implicit def deriveOrder[T] = macro TypeClass.derive_impl[Order, T]

}

// vim: expandtab:ts=2:sw=2
