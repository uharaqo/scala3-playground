package com.github.uharaqo.scala.examples.http.io.grpc

import cats.effect.std.Dispatcher
import cats.effect.{IO, Resource}
import com.github.uharaqo.scala.examples.protos.hello.{GreeterFs2Grpc, HelloReply, HelloRequest}
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import fs2.grpc.syntax.all.*
import io.grpc.{ManagedChannel, Metadata}

object GrpcClient {

  private val stub: Resource[IO, GreeterFs2Grpc[IO, Metadata]] =
    GreeterFs2Grpc.stubResource(
      NettyChannelBuilder
        .forAddress("127.0.0.1", 50051)
        .usePlaintext()
        .build()
    )

  def call(request: HelloRequest): IO[HelloReply] =
    for {
      res <- stub.use(_.sayHello(request, Metadata()))
    } yield res
}
