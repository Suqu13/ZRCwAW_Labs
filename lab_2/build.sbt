name := "lab_2"

version := "0.1"

scalaVersion := "2.13.6"

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

val http4sVersion = "0.23.5"
val pureConfigVersion = "0.17.0"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.12.80",
  "org.typelevel" %% "cats-effect" % "3.2.9" withSources() withJavadoc(),
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps"
)
