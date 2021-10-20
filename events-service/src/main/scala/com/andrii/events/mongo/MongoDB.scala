package com.andrii.events.mongo

import cats.effect.Async
import com.mongodb.client.model.BsonField
import org.bson.Document
import org.mongodb.scala.SingleObservable

trait MongoDB {
  final def ef[F[_]: Async, T](observable: SingleObservable[T]): F[T] = {
    Async[F].async[T] { f =>
      observable.subscribe(result =>
        f(Right(result)),
        err => f(Left(err)))
    }
  }

  final def bsonField(name: String, key: String, value: Any) = new BsonField(name, new Document(key, value))

}
