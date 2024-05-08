Global / onChangedBuildSource := ReloadOnSourceChanges

val developer = Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev"))
val copyrightYear = 2020
val developerGithub = s"https://github.com/${developer.id}"

organization := "dev.akif"
name := "as"
description := s"${name.value}: No-macro, no-reflection, opinionated type refinement for Scala, powered by e"
scmInfo := Some(ScmInfo(url(s"$developerGithub/${name.value}"), s"$developerGithub/${name.value}.git"))
homepage := Some(developer.url)
startYear := Some(copyrightYear)
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organizationName := developer.name
organizationHomepage := Some(developer.url)
developers := List(developer)

// === Project Settings ===
version := "1.0.0-SNAPSHOT"
scalaVersion := "3.4.1"
javacOptions ++= Seq("-source", "21")

// === Project Dependencies ===
val e = "dev.akif" %% "e-scala" % "3.0.0"
val munit = "org.scalameta" %% "munit" % "1.0.0-RC1" % Test

libraryDependencies ++= Seq(e, munit)
