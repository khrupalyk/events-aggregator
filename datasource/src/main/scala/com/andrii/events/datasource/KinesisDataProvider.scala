package com.andrii.events.datasource

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClient
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetShardIteratorRequest}

import java.util.Date
import scala.collection.JavaConverters._

class KinesisDataProvider[F[_]] extends DataProvider[F] {

  val streamName = "candidate-home-assignments-kinesis-stream"
  val keyId = "..."
  val secret = "..."
  val region = "us-east-1"

  val kinesisClient = AmazonKinesisAsyncClient.asyncBuilder()
    .withRegion(region)
    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(keyId, secret)))
    .build()

  val shards = kinesisClient.describeStreamAsync(streamName).get().getStreamDescription.getShards.asScala

  println("Shards: " + shards)

  val shardid = shards.map(_.getShardId).head
  val sn = shards.map(_.getSequenceNumberRange).head.getStartingSequenceNumber
  val shardReq = new GetShardIteratorRequest()
    .withStreamName(streamName)
    .withShardId(shardid)
    .withTimestamp(new Date(0))
    .withShardIteratorType("AT_TIMESTAMP")
  val iterator = kinesisClient.getShardIteratorAsync(shardReq).get()

  println("Iterator: " + iterator)

  val getReq = new GetRecordsRequest()
    .withShardIterator(iterator.getShardIterator)

  println(kinesisClient.getRecords(getReq).getRecords.size())

  override def stream: fs2.Stream[F, Event] = fs2.Stream.empty
}
