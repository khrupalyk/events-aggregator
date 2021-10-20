package com.andrii.events.infrastructure.http4s

import cats.effect.{ConcurrentEffect, Timer}
import Http4sServer.Htp4sServerConfig
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.HttpRoutes

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait Http4sServer[F[_]] {
  def setup(routes: HttpRoutes[F], config: Htp4sServerConfig): F[Unit]
}

class Http4sServerImpl[F[_]: ConcurrentEffect: Timer] extends Http4sServer[F] {
  def setup(routes: HttpRoutes[F], config: Htp4sServerConfig): F[Unit] = {
    //should be separate ec for http server, ok for testing
    import scala.concurrent.ExecutionContext.global
    BlazeServerBuilder(global)
      .bindHttp(config.port, config.host)
      .withResponseHeaderTimeout(config.responseHeaderTimeout)
      .withIdleTimeout(config.idleTimeout)
      .withBanner(Seq.empty[String])
      .withHttpApp(routes.orNotFound)
      .withSelectorThreadFactory(new ThreadFactory {
        val ctr = new AtomicInteger(0)
        override def newThread(r: Runnable): Thread = {
          val res = new Thread(r, s"http4s-selector-server-pool-${ctr.getAndIncrement()}")
          res.setDaemon(true)
          res
        }
      })
      .serve
      .compile
      .drain
  }
}

object Http4sServer {

  case class Htp4sServerConfig(
    host: String = "0.0.0.0",
    port: Int = 9000,
    responseHeaderTimeout: FiniteDuration = 1.minutes,
    idleTimeout: FiniteDuration = 2.minutes,
    logHeaders: Boolean = false,
    logBody: Boolean = false)

}
