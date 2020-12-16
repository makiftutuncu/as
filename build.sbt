description          in ThisBuild := "As"
homepage             in ThisBuild := Some(url("https://akif.dev"))
startYear            in ThisBuild := Some(2020)
licenses             in ThisBuild := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
organization         in ThisBuild := "dev.akif"
organizationName     in ThisBuild := "Mehmet Akif Tütüncü"
organizationHomepage in ThisBuild := Some(url("https://akif.dev"))
developers           in ThisBuild := List(Developer("makiftutuncu", "Mehmet Akif Tütüncü", "m.akif.tutuncu@gmail.com", url("https://akif.dev")))
scmInfo              in ThisBuild := Some(ScmInfo(url("https://github.com/makiftutuncu/as"), "https://github.com/as/as.git"))

version       := "1.0.0"
scalaVersion  := "2.13.2"
javacOptions ++= Seq("-source", "11")

libraryDependencies ++= Seq(
  "dev.akif" %% "e-scala" % "2.0.0"
)
