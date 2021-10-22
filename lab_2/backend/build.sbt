name := "lab_2"

version := "0.1"

scalaVersion := "2.13.6"

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

val http4sVersion = "0.23.5"
val pureConfigVersion = "0.17.0"
val circeVersion = "0.14.1"
val amazonSdkVersion = "2.17.57"

libraryDependencies ++= Seq(

  //  aws-sdk
  "software.amazon.awssdk" % "s3" % amazonSdkVersion,
  "software.amazon.awssdk" % "ec2" % amazonSdkVersion,
  "software.amazon.awssdk" % "comprehend" % "2.17.57",
  "software.amazon.awssdk" % "translate" % amazonSdkVersion,
  "software.amazon.awssdk" % "polly" % amazonSdkVersion,

  //  cats-effect
  "org.typelevel" %% "cats-effect" % "3.2.9" withSources() withJavadoc(),

  //http4s
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,

  // pureconfig
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,

  //circe
  "io.circe" %% "circe-generic" % circeVersion
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps"
)
