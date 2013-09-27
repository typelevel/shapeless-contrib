package shapeless.contrib

import _root_.spire.math._
import _root_.spire.algebra._

import shapeless._

package object spire {

  implicit def deriveEq[T] = macro TypeClass.derive_impl[Eq, T]

  implicit def deriveOrder[T] = macro TypeClass.derive_impl[Order, T]

  implicit def deriveSemigroup[T] =
    macro TypeClass.derive_impl[Semigroup, T]

  implicit def deriveMonoid[T] = macro TypeClass.derive_impl[Monoid, T]

  implicit def deriveGroup[T] = macro TypeClass.derive_impl[Group, T]

  implicit def deriveAbGroup[T] = macro TypeClass.derive_impl[AbGroup, T]

  implicit def deriveAdditiveSemigroup[T] =
    macro TypeClass.derive_impl[AdditiveSemigroup, T]

  implicit def deriveAdditiveMonoid[T] =
    macro TypeClass.derive_impl[AdditiveMonoid, T]

  implicit def deriveAdditiveGroup[T] =
    macro TypeClass.derive_impl[AdditiveGroup, T]

  implicit def deriveAdditiveAbGroup[T] =
    macro TypeClass.derive_impl[AdditiveAbGroup, T]

  implicit def deriveMultiplicativeSemigroup[T] =
    macro TypeClass.derive_impl[MultiplicativeSemigroup, T]

  implicit def deriveMultiplicativeMonoid[T] =
    macro TypeClass.derive_impl[MultiplicativeMonoid, T]

  implicit def deriveMultiplicativeGroup[T] =
    macro TypeClass.derive_impl[MultiplicativeGroup, T]

  implicit def deriveMultiplicativeAbGroup[T] =
    macro TypeClass.derive_impl[MultiplicativeAbGroup, T]
}

// vim: expandtab:ts=2:sw=2
