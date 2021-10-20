package com.andrii.events.routes

import cats.data.{NonEmptyList, Validated}
import cats.effect.ConcurrentEffect
import cats.implicits._
import com.andrii.events.domain.ServiceError
import com.andrii.events.services.EventsService
import org.http4s.{HttpRoutes, ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl

import java.time.{LocalDateTime, ZoneOffset}
import scala.util.Try
import io.circe.syntax._
import org.http4s.circe.jsonEncoder
import org.slf4j.LoggerFactory

import java.util.Date

class EventsRoutes[F[_]: ConcurrentEffect](service: EventsService[F]) extends Http4sDsl[F] {

  private val log = LoggerFactory.getLogger(getClass)

  val routes = HttpRoutes.of[F] {
    case GET -> Root / eventType / "average" :? FromParam(from) +& ToParam(to) =>
      val start = System.currentTimeMillis()
      service.average(from, to, eventType)
        .flatMap {
          case Right(data) =>
            val end = System.currentTimeMillis()
            //naive measurement, prometheus could be used to measure time
            log.info(s"Time spent: ${(end - start).toDouble} ms, processed count ${data.processedCount} for ${data.`type`}")
            Ok(data.asJson)
          case Left(err) =>
            BadRequest(ServiceError(err.getMessage).asJson)
        }
  }

  implicit val yearQueryParamDecoder: QueryParamDecoder[Date] = value => {
    Validated.fromTry(Try(Date.from(LocalDateTime.parse(value.value).toInstant(ZoneOffset.UTC))))
      .leftMap(err => NonEmptyList.of(ParseFailure("Invalid date", err.toString)))
  }

  object FromParam extends QueryParamDecoderMatcher[Date]("from")
  object ToParam extends QueryParamDecoderMatcher[Date]("to")
}
