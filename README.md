shapeless-contrib
=================

Interoperability libraries for Shapeless

[![Build Status](https://travis-ci.org/typelevel/shapeless-contrib.png?branch=master)](http://travis-ci.org/typelevel/shapeless-contrib)


Usage
-----

This library is currently available for Scala 2.10.

To use the latest version, include the following in your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "shapeless-scalacheck" % "0.1.2",
  "org.typelevel" %% "shapeless-spire" % "0.1.2",
  "org.typelevel" %% "shapeless-scalaz" % "0.1.2"
)
```

Some features are only available in the snapshot version (`0.2-SNAPSHOT`).


What does this library do?
--------------------------

`shapeless-contrib` aims to provide smooth interoperability between [Shapeless](https://github.com/milessabin/shapeless), [Scalaz](https://github.com/scalaz/scalaz) and [Spire](https://github.com/non/spire). At the moment, this means automatic derivation of type class instances for case classes. Stay tuned for further developments.


Examples
--------

### Scalaz + Shapeless = Profit

_Note:_ The 0.1.x series of this library depends on shapeless 1.2.x, whereas the newer 0.2.x series depends on shapeless 2.0.0.

The combination of these two libraries allows for some nifty utility functions related to `scalaz.Applicative`:

* lifting arbitrary functions (i.e. a generalized `liftA1`, `liftA2`, ...)
* sequencing and traversing an `HList` (just like sequencing and traversing a `List`)

```scala
import shapeless._
import shapeless.contrib.scalaz._

import scalaz.std.option._

// define a function with arbitrarily many parameters
def foo(x: Int, y: String, z: Float) = s"$x - $y - $z"

// lift it into `Option`
val lifted = Applicative[Option].liftA(foo _)

// resulting type: `(Option[Int], Option[String], Option[Float]) => Option[String]`


// define an `HList` consisting of `Option`s
val in = Option(1) :: Option("foo") :: HNil

val sequenced = sequence(in)

// resulting type: `Option[Int :: String :: HNil]`

// works for `Validation`, too:
import scalaz._
import scalaz.std.string._

val v1: Validation[String, Int] = Success(3)
val v2: Validation[String, Float] = Failure("foo")
sequence(v1 :: v2 :: HNil)

// resulting type: `Validation[String, Int :: Float :: HNil]`
```

Traversing works the same way, but you will also have to specify a `shapeless.Poly` which maps over the `HList` first.

In addition to that, it also provides a conversion between their lens types:

```scala
import shapeless._
import shapeless.Nat._
import shapeless.contrib.scalaz._

case class TwoElem(n: Int, x: String)
implicit def TwoIso = Iso.hlist(TwoElem.apply _, TwoElem.unapply _)

// Generate a `shapeless.Lens`
val sLens = Lens[TwoElem] >> _0

// Convert it to a `scalaz.Lens`
val zsLens = sLens.asScalaz

// The other way round:
import scalaz.Lens

val zLens = Lens.lensId[Int]
val szLens = zLens.asShapeless
```

### Derive type classes

Consider a simple case class with an addition operation:

```scala
case class Vector3(x: Int, y: Int, z: Int) {
  def +(other: Vector3) = Vector3(this.x + other.x, this.y + other.y, this.z + other.z)
}
```

If we want to use that in a generic setting, e.g. in an algorithm which requires a `Monoid`, we can define an instance for `spire.algebra.Monoid` like so:

```scala
implicit object Vector3Monoid extends Monoid[Vector3] {
  def id = Vector3(0, 0, 0)
  def op(x: Vector3, y: Vector3) = x + y
}
```

This will work nicely for that particular case. However, it requires repetition: addition on `Vector3` is just pointwise addition of its elements, and the null vector consists of three zeroes. We do not want to repeat that sort of code for all our case classes, and want to derive that automatically. Luckily, this library provides exactly that:

```scala
import spire.implicits._
import shapeless.contrib.spire._
import shapeless.Iso

// Define the `Vector3` case class without any operations
case class Vector3(x: Int, y: Int, z: Int)

// Invoke shapeless machinery
implicit val v3iso = Iso.hlist(Vector3.apply _, Vector3.unapply _)

// That's it! `Vector3` is an `AdditiveMonoid` now.
Vector3(1, 2, 3) + Vector3(-1, 3, 0)
```
