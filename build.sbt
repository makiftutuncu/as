// === Build Settings ===
val developer = Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev"))
val copyrightYear = 2021
val developerGithub = s"https://github.com/${developer.id}"

organization         := "dev.akif"
name                 := "as"
description          := s"${name.value}: No-macro, no-reflection, opinionated type refinement for Scala, powered by e"
scmInfo              := Some(ScmInfo(url(s"$developerGithub/${name.value}"), s"$developerGithub/${name.value}.git"))
homepage             := Some(developer.url)
startYear            := Some(copyrightYear)
licenses             := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organizationName     := developer.name
organizationHomepage := Some(developer.url)
developers           := List(developer)

// === Project Settings ===
val scala2 = "2.13.5"
val scala3 = "3.0.0"

version             := "2.0.0"
scalaVersion        := scala3
crossScalaVersions  := Seq(scala2, scala3)

// === Project Dependencies ===
val e     = ("dev.akif" %% "e-scala" % "2.0.0").cross(CrossVersion.for3Use2_13)
val munit = "org.scalameta" %% "munit" % "0.7.26" % Test

libraryDependencies ++= Seq(e, munit)
