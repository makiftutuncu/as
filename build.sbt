Global / onChangedBuildSource := ReloadOnSourceChanges

val developer = Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev"))
val copyrightYear = 2020
val developerGithub = s"https://github.com/${developer.id}"

organization := "dev.akif"
name := "as"
description := "as is a no-macro, no-reflection, opinionated type refinement library for Scala 3"
scmInfo := Some(ScmInfo(url(s"$developerGithub/${name.value}"), s"$developerGithub/${name.value}.git"))
homepage := Some(developer.url)
startYear := Some(copyrightYear)
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organizationName := developer.name
organizationHomepage := Some(developer.url)
developers := List(developer)

// === Project Settings ===
scalaVersion := "3.4.1"
javacOptions ++= Seq("-source", "21")
ThisBuild / versionScheme := Some("semver-spec")

// === Project Dependencies ===
val e = "dev.akif" %% "e-scala" % "3.0.0"
val munit = "org.scalameta" %% "munit" % "1.0.0" % Test

libraryDependencies ++= Seq(e, munit)

// === Release Settings ===

import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

Compile / doc / scalacOptions ++= Seq("-snippet-compiler:compile")

Compile / packageBin / publishArtifact := true
Compile / packageSrc / publishArtifact := true
Compile / packageDoc / publishArtifact := true
Test / packageBin / publishArtifact := false
Test / packageSrc / publishArtifact := false
Test / packageDoc / publishArtifact := false
releasePublishArtifactsAction := publishSigned.value

val sonatypeUser = sys.env.getOrElse("SONATYPE_USER", "")
val sonatypePass = sys.env.getOrElse("SONATYPE_PASS", "")

ThisBuild / credentials ++= Seq(
    Credentials(
        "Sonatype Nexus Repository Manager",
        "oss.sonatype.org",
        sonatypeUser,
        sonatypePass
    )
)
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")

usePgpKeyHex("3D5A9AE9F71508A0D85E78DF877A4F41752BB3B5")

val checkPublishCredentials = ReleaseStep { state =>
    if (sonatypeUser.isEmpty || sonatypePass.isEmpty) {
        throw new Exception(
            "Sonatype credentials are missing! Make sure to provide SONATYPE_USER and SONATYPE_PASS environment variables."
        )
    }

    state
}

releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    checkPublishCredentials,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
)
