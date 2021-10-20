package com.andrii.events.services

import cats.implicits._
import cats._
import cats.data.{EitherT, Validated}
import com.andrii.events.domain.Average
import com.andrii.events.repositories.EventsRepo

import java.util.Date

trait EventsService[F[_]] {
  def average(from: Date, to: Date, event: String): F[Throwable Either Average]
}

object EventsService {

  class EventsServiceImpl[F[_]: Monad](eventsRepo: EventsRepo[F]) extends EventsService[F] {
    def average(from: Date, to: Date, event: String): F[Throwable Either Average] = {
      (validateRange(from, to) *> EitherT.right(eventsRepo.average(from, to, event))).value
    }

    private def validateRange(from: Date, to: Date): EitherT[F, Throwable, Unit] = EitherT.fromEither {
      for {
        _ <- Either.cond(to.getTime >= from.getTime, (), new RuntimeException("from > to"))
        _ <- Either.cond(to.getTime - from.getTime >= 60 * 1000, (), new RuntimeException("range < 1 min"))
      } yield ()
    }
  }
//
//
////  val client = new MongoClient("0.0.0.0")
////  println(client.listDatabaseNames().first())
//  val mongo = MongoClient("mongodb://fake:fake@localhost")
////  println(mongo.get)
//
//  val database = mongo.getDatabase("test_db")
//
//  val data = new CreateCollectionOptions()
////  com.mongodb.b
//
//  import org.bson.BSONObject
//  import org.bson.BasicBSONDecoder
//
//  import java.text.SimpleDateFormat
//  val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//
//  //      "2021-10-18T20:19:06.232Z".asJson
//  val str = sdf.format(new Date(LocalDateTime.now().atOffset(ZoneOffset.UTC).toInstant.toEpochMilli))
//
//  import java.text.SimpleDateFormat
//
//  val simpleformat = new SimpleDateFormat("'Z'")
//  simpleformat.setTimeZone(TimeZone.getTimeZone("GMT"))
//  val strTimeZone = simpleformat.format(new Date())
//  println(str)
//  println(strTimeZone)
//  val params =
//    s"""
//      |{
//      | "timestamp": ISODate("${str}"),
//      |
//      |"create" : {
//      | "timeseries": {
//      |   "timeField": "timestamp",
//      |
//      |          "metaField": "metadata",
//      |
//      |          "granularity": "hours"
//      | }}
//      |}
//      |""".stripMargin
//  val decoder = new BasicBSONDecoder
////  val bsonObject = decoder.readObject()
////  val bsonObject = decoder.readObject(new ByteArrayInputStream(params.getBytes))
//
//
//  println(params)
//  println(Document.parse(params))
//  val ops = data.storageEngineOptions(Document.parse(params))
//  println(params)
////  database.
////  database.
//  database
//    val collection = database.getCollection[Document]("test")
////  collection.insertOne(Document.parse(params)).headOption().onComplete(println)
//
//  collection.find().map(println)
//    .collect()
//    .head()
//
//
////  ("weather", ops)
////    .head().onComplete(println)
////  println(mongo.listDatabaseNames().headOption().map {s =>
////    println(s)
////  }.onComplete(println))
//
//  Thread.sleep(2000)

//  mongo.

}
