package com.andrii.events.repositories

import cats.effect.Async
import com.andrii.events.datasource.Event
import com.andrii.events.domain.Average
import com.andrii.events.mongo.{MongoCollections, MongoDB}
import org.mongodb.scala.model.{Aggregates, Filters}
import org.mongodb.scala.model.Filters._

import java.sql
import java.util.Date

trait EventsRepo[F[_]] {
  def save(events: Seq[Event]): F[Boolean]
  def average(from: Date, to: Date, event: String): F[Average]
}

object EventsRepo {

  class MongoRepo[F[_]: Async](mongoCollections: MongoCollections) extends EventsRepo[F] with MongoDB {
    import mongoCollections._

    override def save(events: Seq[Event]): F[Boolean] = ef {
      eventCollection
        .insertMany(events)
        .map(_.wasAcknowledged())
    }

    override def average(from: Date, to: Date, event: String): F[Average] = ef {
      val aggregation = Seq(
        Aggregates.`match`(
          and(
            gte("timestamp", new sql.Date(from.getTime)),
            lte("timestamp", new sql.Date(to.getTime))
          )
        ),
        Aggregates.`match`(
          Filters.eq("eventType", event)
        ),
        Aggregates.group(
          id = "$eventType",
          bsonField(name = "value", key = "$avg", value = "$value"),
          bsonField(name = "count", key = "$sum", value = 1)
        )
      )

      eventCollection
        .aggregate[Average](aggregation)
        .collect()
        .map(_.headOption.getOrElse(Average.empty(event)))
    }
  }

}
