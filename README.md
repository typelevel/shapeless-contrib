shapeless-contrib
=================

Interoperability libraries for Shapeless

[![Build Status](https://travis-ci.org/typelevel/shapeless-contrib.png?branch=master)](http://travis-ci.org/typelevel/shapeless-contrib)


Usage
-----

This library is currently available for Scala 2.10, 2.11, and 2.12.

To use the latest version, include the following in your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "org.typelevel" %% "shapeless-scalacheck" % "0.6.1",
  "org.typelevel" %% "shapeless-spire" % "0.6.1",
  "org.typelevel" %% "shapeless-scalaz" % "0.6.1"
)
```


What does this library do?
--------------------------

`shapeless-contrib` aims to provide smooth interoperability between [Shapeless](https://github.com/milessabin/shapeless), and [Spire](https://github.com/non/spire). The most prominent feature is automatic derivation of type class instances for case classes, but there is more. If you've got a cool idea for more stuff, please open an issue.


Examples
--------

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

// Define the `Vector3` case class without any operations
case class Vector3(x: Int, y: Int, z: Int)

// That's it! `Vector3` is an `AdditiveMonoid` now.
Vector3(1, 2, 3) + Vector3(-1, 3, 0)
```
