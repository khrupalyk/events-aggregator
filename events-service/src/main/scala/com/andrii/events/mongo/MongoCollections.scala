package com.andrii.events.mongo

import com.andrii.events.datasource.Event
import com.andrii.events.mongo.codecs.CodecRegistry
import com.andrii.events.config.AppConfig.MongoConfig
import org.mongodb.scala.{MongoClient, MongoCollection}

trait MongoCollections {
  def eventCollection: MongoCollection[Event]
}

object MongoCollections {
  def apply(config: MongoConfig): MongoCollections = {

    val mongo = MongoClient(s"mongodb://${config.user}:${config.password}@${config.host}:${config.port}")
    val database = mongo.getDatabase(config.database)

    new MongoCollections with CodecRegistry {
      override val eventCollection: MongoCollection[Event] =
        database.getCollection[Event](config.eventsCollection).withCodecRegistry(codecRegistry)
    }
  }
}
