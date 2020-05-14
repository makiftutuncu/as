description          in ThisBuild := "As"
homepage             in ThisBuild := Some(url("https://akif.dev"))
startYear            in ThisBuild := Some(2020)
licenses             in ThisBuild := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organization         in ThisBuild := "dev.akif"
organizationName     in ThisBuild := "Mehmet Akif Tütüncü"
organizationHomepage in ThisBuild := Some(url("https://akif.dev"))
developers           in ThisBuild := List(Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
scmInfo              in ThisBuild := Some(ScmInfo(url("https://github.com/makiftutuncu/as"), "https://github.com/as/as.git"))

version       := "0.1.0"
scalaVersion  := "2.13.2"
javacOptions ++= Seq("-source", "11")

testFrameworks += new TestFramework("munit.Framework")

val eVersion          = "1.1.3-SNAPSHOT"
val mUnitVersion      = "0.7.5"
val scalaCheckVersion = "1.14.1"

libraryDependencies ++= Seq(
  "dev.akif"       %% "e-scala"          % eVersion,
  "org.scalameta"  %% "munit"            % mUnitVersion      % Test,
  "org.scalacheck" %% "scalacheck"       % scalaCheckVersion % Test,
  "org.scalameta"  %% "munit-scalacheck" % mUnitVersion      % Test
)
