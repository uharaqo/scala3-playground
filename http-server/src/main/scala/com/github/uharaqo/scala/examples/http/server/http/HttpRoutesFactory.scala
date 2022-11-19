package com.github.uharaqo.scala.examples.http.server.http

import cats.effect.*
import cats.implicits.*
import com.github.uharaqo.scala.examples.http.io.grpc.GrpcClient
import com.github.uharaqo.scala.examples.http.io.db.PostgresTester
import com.github.uharaqo.scala.examples.protos.hello.HelloRequest
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{DecodingFailure, Json}
import org.http4s.*
import org.http4s.Header.Raw
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.*
import org.http4s.syntax.all.*
import org.typelevel.ci.*

import scala.concurrent.Future
import org.http4s.server.middleware.RequestId

object HttpRoutesFactory {

  import org.http4s.circe.CirceEntityDecoder.*

  case class Hello(name: String)

  def apply(): HttpRoutes[IO] =
    RequestId.httpRoutes(
      Router(
        "/api/" -> hello()
      )
    )

  private def hello(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ GET -> Root / "hello" / name =>
        val params = req.params

        val reqId      = req.headers.get(ci"X-Request-ID").fold("empty")(_.head.value)
        val headers    = Map("foo" -> "bar")
        val message =
          s"""requestId: $reqId
             |path param: $name
             |query param: ${params("foo")}
             |""".stripMargin

        IO.pure(SimpleHttpResponse(message, headers = headers).toResponse())

      case req @ POST -> Root / "grpc" =>
        for {
          hello <- req.as[Hello]
          reply <- GrpcClient.call(HelloRequest(hello.name))
        } yield SimpleHttpResponse(s"Hello ${reply.message}").toResponse()

      case req @ POST -> Root / "postgres" =>
        for {
          result <- PostgresTester().insertEventsInTransaction()
        } yield SimpleHttpResponse(s"Applied ${result}").toResponse()
    }
}
