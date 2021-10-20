package com.andrii.events.mongo.codecs

import com.andrii.events.datasource.Event
import com.andrii.events.domain.Average
import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromRegistries}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext, configuration}
import org.bson.{BsonReader, BsonWriter}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY

trait CodecRegistry {

  private val eventCodec = new Codec[Event] {
    override def encode(writer: BsonWriter, value: Event, encoderContext: EncoderContext): Unit = {
      writer.writeStartDocument()

      writer.writeString("eventType", value.eventType)
      writer.writeDouble("value", value.value)
      writer.writeDateTime("timestamp", value.timestamp.getTime)

      writer.writeEndDocument()
    }

    override def getEncoderClass: Class[Event] = classOf[Event]

    override def decode(reader: BsonReader, decoderContext: DecoderContext): Event = ???
  }

  private val averageCodec = new Codec[Average] {
    override def encode(writer: BsonWriter, value: Average, encoderContext: EncoderContext): Unit = ???

    override def getEncoderClass: Class[Average] = classOf[Average]

    override def decode(reader: BsonReader, decoderContext: DecoderContext): Average = {
      reader.readStartDocument()

      val id = reader.readString("_id")
      val value = reader.readDouble("value")
      val count = reader.readInt32("count")

      reader.readEndDocument()

      Average(id, value, count)
    }
  }

  val codecRegistry: configuration.CodecRegistry =
    fromRegistries(fromCodecs(eventCodec, averageCodec), DEFAULT_CODEC_REGISTRY)

}
