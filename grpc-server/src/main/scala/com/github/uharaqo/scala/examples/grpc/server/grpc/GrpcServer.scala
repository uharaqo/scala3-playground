package com.github.uharaqo.scala.examples.grpc.server.grpc

import io.grpc.ServerServiceDefinition
import cats.effect.*
import com.github.uharaqo.scala.examples.protos.*
import com.github.uharaqo.scala.examples.protos.hello.*
import fs2.grpc.syntax.all.*
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.{Metadata, ServerServiceDefinition}

class GrpcServer(service: Resource[IO, ServerServiceDefinition], port: Int = 50051) {

  def run(): IO[ExitCode] =
    service
      .use(s => newServer(s, port))
      .as(ExitCode.Success)

  private def newServer(service: ServerServiceDefinition, port: Int): IO[Nothing] =
    NettyServerBuilder
      .forPort(port)
      .addService(service)
      .resource[IO]
      .evalMap(server => IO(server.start()))
      .useForever
}
