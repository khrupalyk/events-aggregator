package com.andrii.events.services

import cats.Functor
import cats.effect.{Concurrent, Timer}
import cats.implicits._
import com.andrii.events.datasource.{DataProvider, Event}
import com.andrii.events.repositories.EventsRepo
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait EventsConsumer[F[_]] {
  def start: F[Unit]
}

object EventsConsumer {

  case class ConsumerConf(batchSize: Int, batchProcessingTimeout: FiniteDuration = 1.minute)

  class Fs2EventsConsumer[F[_]: Timer: Concurrent: Functor](
    dataProvider: DataProvider[F],
    eventsRepo: EventsRepo[F],
    conf: ConsumerConf) extends EventsConsumer[F] {

    private val log = LoggerFactory.getLogger(getClass)

    def start: F[Unit] = {
      dataProvider
        .stream
        .groupWithin(conf.batchSize, conf.batchProcessingTimeout)
        .map(_.toVector)
        .evalMap(processEvents)
        .compile
        .drain
    }

    private def processEvents(events: Seq[Event]) = {
      eventsRepo
        .save(events)
        .map(_ => log.info("Processed: " + events.length))
    }
  }

}