name := "lab_1"

version := "0.1"

scalaVersion := "2.13.6"

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.12.80",
  "org.typelevel" %% "cats-effect" % "3.2.9" withSources() withJavadoc(),
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps"
)
