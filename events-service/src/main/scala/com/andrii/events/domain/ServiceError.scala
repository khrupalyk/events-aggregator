package com.andrii.events.domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class ServiceError(msg: String)

object ServiceError {
  implicit val encoder: Encoder[ServiceError] = deriveEncoder
}
