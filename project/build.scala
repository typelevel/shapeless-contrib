import sbt._
import Keys._

import sbtrelease.ReleasePlugin._

object ShapelessContribBuild extends Build {

  val shapelessVersion = "1.2.4"


  lazy val standardSettings = Defaults.defaultSettings ++ releaseSettings ++ Seq(
    organization := "org.typelevel",

    scalaVersion := "2.10.0",
    crossScalaVersions := Seq("2.9.2", "2.10.0"),
    scalacOptions ++= Seq("-unchecked", "-deprecation"),

    libraryDependencies += "com.chuusai" %% "shapeless" % shapelessVersion cross CrossVersion.full,

    resolvers += Resolver.sonatypeRepo("releases"),

    // https://github.com/sbt/sbt/issues/603
    conflictWarning ~= { cw =>
      cw.copy(filter = (id: ModuleID) => true, group = (id: ModuleID) => id.organization + ":" + id.name, level = Level.Error, failOnConflict = true)
    },

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
    pomIncludeRepository := Function.const(false),
    pomExtra :=
      <url>http://typelevel.org/scalaz</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>http://www.opensource.org/licenses/mit-license.php</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
            <url>https://github.com/larsrh/shapeless-contrib</url>
            <connection>scm:git:git://github.com/larsrh/shapeless-contrib.git</connection>
            <developerConnection>scm:git:git@github.com:larsrh/shapeless-contrib.git</developerConnection>
        </scm>
        <developers>
          <developer>
            <id>larsrh</id>
            <name>Lars Hupel</name>
            <url>https://github.com/larsrh</url>
          </developer>
        </developers>
  )

  lazy val shapelessContrib = Project(
    id = "shapeless-contrib",
    base = file("."),
    settings = standardSettings ++ Seq(
      publishArtifact := false
    )
  )

}

// vim: expandtab:ts=2:sw=2
