package com.github.uharaqo.scala.examples.grpc

import cats.effect.*
import com.github.uharaqo.scala.examples.protos.*
import com.github.uharaqo.scala.examples.protos.hello.*
import com.github.uharaqo.scala.examples.grpc.server.grpc.*
import fs2.grpc.syntax.all.*
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.{Metadata, ServerServiceDefinition}

object Main extends IOApp {

  val helloService: Resource[IO, ServerServiceDefinition] =
    GreeterFs2Grpc.bindServiceResource[IO](GreeterService())

  override def run(args: List[String]): IO[ExitCode] =
    GrpcServer(helloService, 50051).run()
}
