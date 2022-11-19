package com.github.uharaqo.scala.examples.http.server.http

import cats.effect.*
import cats.implicits.*
import com.github.uharaqo.scala.examples.http.io.grpc.GrpcClient
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

case class SimpleHttpResponse(
  message: String,
  status: Status = Status.Ok,
  headers: Map[String, String] = Map.empty,
) {

  def toResponse(): Response[IO] =
    Response(
      status = status,
      headers = Headers(headers.map((k, v) => Header.Raw(k.ci, v)).toList),
      entity = EntityEncoder.stringEncoder.toEntity(message)
    )
}