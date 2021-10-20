package com.andrii.events.domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class Average(`type`: String, value: Double, processedCount: Long)

object Average {
  implicit val encoder: Encoder[Average] = deriveEncoder

  def empty(t: String): Average = Average(t, 0.0, 0)
}
