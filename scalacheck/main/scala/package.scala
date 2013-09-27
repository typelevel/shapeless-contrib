package shapeless.contrib

import shapeless._

import scalaz._
import scalaz.syntax.apply._
import scalaz.scalacheck.ScalaCheckBinding._

import org.scalacheck.{Gen, Arbitrary}

package object scalacheck {

  implicit def deriveArbitrary[T] = macro TypeClass.derive_impl[Arbitrary, T]

}

// vim: expandtab:ts=2:sw=2
