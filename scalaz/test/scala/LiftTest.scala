package shapeless.contrib.scalaz

import org.scalacheck.Arbitrary._

import org.specs2.scalaz.{ScalazMatchers, Spec}

import scalaz._

class LiftTest extends Spec with ScalazMatchers {

  import scalaz.std.option._
  import scalaz.std.string._
  import scalaz.syntax.applicative._

  def foo(x: Int, y: String, z: Float) = s"$x - $y - $z"
  val lifted = lift((foo _).pure[Option])

  // check for type
  val _: (Option[Int], Option[String], Option[Float]) => Option[String] = lifted

  "lifting a ternary operation" ! prop { (x: Option[Int], y: Option[String], z: Option[Float]) =>
    val r1 = lifted(x, y, z)
    val r2 = Apply[Option].ap3(x, y, z)((foo _).pure[Option])
    r1 must equal(r2)
  }

}

// vim: expandtab:ts=2:sw=2
