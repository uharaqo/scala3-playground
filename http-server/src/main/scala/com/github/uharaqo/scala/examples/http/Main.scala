package com.github.uharaqo.scala.examples.http

import cats.effect.*
import com.github.uharaqo.scala.examples.http.server.http.{HttpRoutesFactory, HttpServer}
import org.http4s.HttpRoutes

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

//    val cqls    = Cqls(CqlSessionProvider())
//    val factory = EventSourcerFactory.cassandraBuilder(cqls)
//        val factory      = EventSourcerFactory.inMemoryBuilder()
//    val commandProxy = CommandProxy(Seq(ProjectEventSourcer(factory), TodoEventSourcer(factory)))
    val routes: HttpRoutes[IO] = HttpRoutesFactory()

    HttpServer.start(routes)
  }
}
