package shapeless.contrib.scalaz

import org.specs2.scalaz.Spec

import scalaz._
import scalaz.scalacheck.ScalazArbitrary._

import shapeless._

class TraverseTest extends Spec {
  import scalaz.std.option._
  import scalaz.std.string._
  import scalaz.syntax.apply._
  import scalaz.syntax.std.option._

  def optToValidation[T](opt: Option[T]): Validation[String, T] = opt.toSuccess("Nothing there!")

  object headOption extends Poly1 {
    implicit def caseSet[T] = at[Set[T]](_.headOption)
  }

  object optionToValidation extends Poly1 {
    implicit def caseOption[T] = at[Option[T]](optToValidation)
  }

  "traversing Set with Set => Option" ! prop { (x: Set[Int], y: Set[String], z: Set[Float]) =>
    traverse(x :: y :: z :: HNil)(headOption) must_== ((x.headOption |@| y.headOption |@| z.headOption) { _ :: _ :: _ :: HNil })
  }

  "traversing Option with Option => Validation" ! prop {(x: Option[Int], y: Option[String], z: Option[Float]) =>
    traverse(x :: y :: z :: HNil)(optionToValidation) must_==
      ((optToValidation(x) |@| optToValidation(y) |@| optToValidation(z)) { _ :: _ :: _ :: HNil })
  }
}
