package shapeless.contrib.scalaz

import org.specs2.scalaz.Spec

import scalaz._
import scalaz.scalacheck.ScalazArbitrary._

import shapeless._

class SequenceTest extends Spec {

  import scalaz.std.option._
  import scalaz.std.string._
  import scalaz.syntax.apply._

  "sequencing Option" ! prop { (x: Option[Int], y: Option[String], z: Option[Float]) =>
    sequence(x :: y :: z :: HNil) must_== ((x |@| y |@| z) { _ :: _ :: _ :: HNil })
  }

  "sequencing Validation" ! prop { (x: Validation[String, Int], y: Validation[String, String], z: Validation[String, Float]) =>
    sequence(x :: y :: z :: HNil) must_== ((x |@| y |@| z) { _ :: _ :: _ :: HNil })
  }

}

// vim: expandtab:ts=2:sw=2
