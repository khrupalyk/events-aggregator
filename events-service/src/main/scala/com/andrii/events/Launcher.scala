package com.andrii.events

import cats.effect.{ExitCode, IO, IOApp}
import com.andrii.events.datasource.DataProvider
import com.andrii.events.config.AppConfig
import com.andrii.events.infrastructure.http4s.{Http4sServer, Http4sServerImpl}
import com.andrii.events.mongo.MongoCollections
import com.andrii.events.repositories.EventsRepo
import com.andrii.events.routes.EventsRoutes
import com.andrii.events.services.{EventsConsumer, EventsService}
import org.slf4j.LoggerFactory

object Launcher extends IOApp {

  private val log = LoggerFactory.getLogger(getClass)

  override def run(args: List[String]): IO[ExitCode] = {

    val config = AppConfig.loadUnsafe

    val repo: EventsRepo[IO] = new EventsRepo.MongoRepo(MongoCollections(config.mongo))
    val service = new EventsService.EventsServiceImpl[IO](repo)
    val dataProvider: DataProvider[IO] = new DataProvider.MockedData
    val eventsConsumer: EventsConsumer[IO] = new EventsConsumer.Fs2EventsConsumer(dataProvider, repo, config.eventConsumer)

    val routes = new EventsRoutes[IO](service)
    val httpServer: Http4sServer[IO] = new Http4sServerImpl
    val server = httpServer.setup(routes.routes, config.server)

    if(config.produceTestData) {
      log.info("Data consumer is on. Will use mocked data stream instead of kinesis.")
      (eventsConsumer.start.start *> server)
        .as(ExitCode.Success)
    } else {
      log.info("Data consumer is off.")
      server.as(ExitCode.Success)
    }
  }
}
