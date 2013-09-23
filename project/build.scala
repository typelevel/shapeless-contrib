import sbt._
import Keys._

import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.Utilities._

import com.typesafe.sbt.pgp.PgpKeys._

object ShapelessContribBuild extends Build {

  val shapelessVersion = "1.2.4"
  val scalazVersion = "7.0.0"
  val scalacheckVersion = "1.10.0"


  lazy val publishSignedArtifacts = ReleaseStep(
    action = st => {
      val extracted = st.extract
      val ref = extracted.get(thisProjectRef)
      extracted.runAggregated(publishSigned in Global in ref, st)
    },
    check = st => {
      // getPublishTo fails if no publish repository is set up.
      val ex = st.extract
      val ref = ex.get(thisProjectRef)
      Classpaths.getPublishTo(ex.get(publishTo in Global in ref))
      st
    },
    enableCrossBuild = true
  )

  lazy val standardSettings = Defaults.defaultSettings ++ releaseSettings ++ Seq(
    organization := "org.typelevel",

    licenses := Seq("MIT" → url("http://www.opensource.org/licenses/mit-license.php")),
    homepage := Some(url("http://typelevel.org/")),

    scalaVersion := "2.10.2",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),

    libraryDependencies += "com.chuusai" %% "shapeless" % shapelessVersion,

    dependencyOverrides +=
      "org.scala-lang" % "scala-library" % scalaVersion.value,

    resolvers += Resolver.sonatypeRepo("releases"),

    conflictManager := ConflictManager.strict,

    sourceDirectory <<= baseDirectory(identity),

    publishTo <<= (version).apply { v =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("Snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("Releases" at nexus + "service/local/staging/deploy/maven2")
    },
    credentials += Credentials(
      Option(System.getProperty("build.publish.credentials")) map (new File(_)) getOrElse (Path.userHome / ".ivy2" / ".credentials")
    ),

    // adapted from sbt-release defaults
    // * does not perform `pushChanges`
    // * performs `publish-signed` instead of `publish`
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishSignedArtifacts,
      setNextVersion,
      commitNextVersion
    ),

    pomIncludeRepository := Function.const(false),
    pomExtra := (
      <scm>
        <url>https://github.com/typelevel/shapeless-contrib</url>
        <connection>scm:git:git://github.com/typelevel/shapeless-contrib.git</connection>
      </scm>
      <developers>
        <developer>
          <id>larsrh</id>
          <name>Lars Hupel</name>
          <url>https://github.com/larsrh</url>
        </developer>
      </developers>
    )
  )

  lazy val shapelessContrib = Project(
    id = "shapeless-contrib",
    base = file("."),
    settings = standardSettings ++ Seq(
      publishArtifact := false
    ),
    aggregate = Seq(common, scalacheck, scalaz, spire)
  )

  lazy val common = Project(
    id = "common",
    base = file("common"),
    settings = standardSettings ++ Seq(
      name := "shapeless-contrib-common"
    )
  )

  lazy val scalacheck = Project(
    id = "scalacheck",
    base = file("scalacheck"),
    settings = standardSettings ++ Seq(
      name := "shapeless-scalacheck",
      libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % scalazVersion,
        "org.scalacheck" %% "scalacheck" % scalacheckVersion,
        "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion
      )
    )
  )

  lazy val scalaz = Project(
    id = "scalaz",
    base = file("scalaz"),
    dependencies = Seq(common, scalacheck % "test"),
    settings = standardSettings ++ Seq(
      name := "shapeless-scalaz",
      libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % scalazVersion,

        "org.specs2" %% "specs2" % "1.12.3" % "test",
        "org.scalacheck" %% "scalacheck" % scalacheckVersion % "test",
        "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test",
        "org.typelevel" %% "scalaz-specs2" % "0.1.4" % "test"
      )
    )
  )

  lazy val spire = Project(
    id = "spire",
    base = file("spire"),
    dependencies = Seq(common, scalacheck % "test"),
    settings = standardSettings ++ Seq(
      name := "shapeless-spire",
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "org.scalacheck" %% "scalacheck" % scalacheckVersion % "test",
        "org.spire-math" %% "spire" % "0.4.0-M3",
        "org.spire-math" %% "spire-scalacheck-binding" % "0.4.0-M3" % "test"
      )
    )
  )

}

// vim: expandtab:ts=2:sw=2
