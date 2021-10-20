package com.andrii.events.config

import AppConfig.MongoConfig
import com.andrii.events.infrastructure.http4s.Http4sServer.Htp4sServerConfig
import com.andrii.events.services.EventsConsumer.ConsumerConf
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class AppConfig(
  server: Htp4sServerConfig,
  mongo: MongoConfig,
  eventConsumer: ConsumerConf,
  produceTestData: Boolean = false)

object AppConfig {
  case class MongoConfig(
    host: String = "localhost",
    port: Int = 27017,
    database: String = "test",
    eventsCollection: String = "test",
    user: String = "fake",
    password: String = "fake")

  def loadUnsafe = ConfigSource.default.loadOrThrow[AppConfig]
}

