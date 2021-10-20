package com.andrii.events.datasource

import cats.effect.{Sync, Timer}

import java.util.Date
//import scala.concurrent.duration.DurationInt
import scala.io.Source

trait DataProvider[F[_]] {
  def stream: fs2.Stream[F, Event]
}

object DataProvider {

  class MockedData[F[_]: Timer: Sync] extends DataProvider[F] {

    private val path = "data/samples.log"

    override def stream: fs2.Stream[F, Event] = {

      val data = Source.fromResource(path).getLines

//      fs2.Stream.awakeEvery[F](1.millis) zipRight
        fs2.Stream
          .fromIterator[F].apply(data, chunkSize = 1)
          .map { s =>
            val csv = s.split(",")
            val timestamp = new Date(csv(0).toLong * 1000)
            Event(csv(1), csv(2).toFloat, timestamp)
          }
    }
  }
}
