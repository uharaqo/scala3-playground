import sbt._

object Version {
  val http4sVersion = "1.0.0-M36"
  val circeVersion  = "0.14.3"
  val fs2Version    = "3.3.0"
  val doobieVersion = "1.0.0-RC2"
  val quillVersion = "4.6.0"

  lazy val commonDeps =
    Seq(
      // util
      "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
      "com.typesafe"            % "config"             % "1.4.2",

      // logging
      "org.slf4j"                   % "slf4j-api"       % "2.0.3",
      "ch.qos.logback"              % "logback-classic" % "1.4.4",
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.5",
    )

  lazy val fs2Deps =
    Seq(
      "org.typelevel" %% "cats-core"            % "2.8.0" withSources () withJavadoc (),
      "org.typelevel" %% "cats-effect"          % "3.3.14" withSources () withJavadoc (),
      "co.fs2"        %% "fs2-io"               % fs2Version,
      "co.fs2"        %% "fs2-reactive-streams" % fs2Version,
    )

  lazy val http4sDeps =
    Seq(
      // http4s
      "org.http4s" %% "http4s-core"         % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe"        % http4sVersion,
      // serialization
      "io.circe" %% "circe-core"    % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser"  % circeVersion,
    )

  lazy val grpcDeps =
    Seq(
      "io.grpc" % "grpc-netty-shaded" % scalapb.compiler.Version.grpcJavaVersion
    )

  lazy val doobieDeps =
    Seq(
      // doobie
      "org.tpolecat" %% "doobie-core"   % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
//      "org.tpolecat" %% "doobie-quill"  % doobieVersion, // not supported for Scala 3
      // quill
      "io.getquill" %% "quill-jdbc" % quillVersion,
      "io.getquill" %% "quill-doobie" % quillVersion,
      // postgres
      "org.postgresql" % "postgresql"      % "42.5.0",
      "org.tpolecat"  %% "doobie-postgres" % doobieVersion,
    )
}
