name := "lab_2"

version := "0.1"

scalaVersion := "2.13.6"

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

val http4sVersion = "0.23.5"
val pureConfigVersion = "0.17.0"
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "s3" % "2.17.57",
  "software.amazon.awssdk" % "ec2" % "2.17.57",
  "software.amazon.awssdk" % "comprehend" % "2.17.57",
  "org.typelevel" %% "cats-effect" % "3.2.9" withSources() withJavadoc(),
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps"
)
