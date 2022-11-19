package com.github.uharaqo.scala.examples.http.server.http

import cats.effect.{ExitCode, IO}
import com.comcast.ip4s.*
import com.typesafe.scalalogging.LazyLogging
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.*

object HttpServer extends LazyLogging {

  private val errorHandler: PartialFunction[Throwable, IO[Response[IO]]] = { case t: Throwable =>
    logger.error("Unexpected Error", t)

    IO.pure(
      Response(
        Status.InternalServerError,
        entity = EntityEncoder.stringEncoder.toEntity("Unexpected Error")
      )
    )
  }

  def start(routes: HttpRoutes[IO], port: Port = Port.fromInt(8080).get): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port)
      .withErrorHandler(errorHandler)
      .withHttpApp(routes.orNotFound) // TODO: replace orNotFound with a custom handler
//      .withHttpApp(Kleisli(a => routes.run(a).getOrElse(Status.NotFound("hello not found"))))
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
