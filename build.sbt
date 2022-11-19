import Version._

scalaVersion := "3.2.1"
organization := "com.github.uharaqo"
name         := "scala-examples"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
)

val baseSettings =
  Seq(
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
    ),
    scalaVersion             := "3.2.1",
    Test / parallelExecution := true,
    run / fork               := true,
  )

lazy val proto =
  (project in file("proto"))
    .settings(baseSettings)
    .settings(name := "proto")
    .enablePlugins(Fs2Grpc)

lazy val httpServer =
  (project in file("http-server"))
    .settings(baseSettings)
    .settings(
      name := "http-server",
      libraryDependencies ++=
        commonDeps ++ fs2Deps ++ http4sDeps ++ grpcDeps ++ doobieDeps
    )
    .dependsOn(proto)

lazy val grpcServer =
  (project in file("grpc-server"))
    .settings(baseSettings)
    .settings(
      name := "grpc-server",
      libraryDependencies ++=
        commonDeps ++ fs2Deps ++ grpcDeps
    )
    .dependsOn(proto)

val root =
  (project in file("."))
    .aggregate(proto, httpServer, grpcServer)
