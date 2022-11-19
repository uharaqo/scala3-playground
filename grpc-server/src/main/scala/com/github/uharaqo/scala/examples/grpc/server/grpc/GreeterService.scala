package com.github.uharaqo.scala.examples.grpc.server.grpc

import cats.effect.IO
import com.github.uharaqo.scala.examples.protos.hello.{GreeterFs2Grpc, HelloReply, HelloRequest}
import io.grpc.Metadata

class GreeterService extends GreeterFs2Grpc[IO, Metadata] {
  override def sayHello(request: HelloRequest, ctx: Metadata): IO[HelloReply] =
    IO(HelloReply("Request name is: " + request.name))
}
