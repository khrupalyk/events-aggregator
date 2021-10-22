### Metrics

Application uses data from resource folder: ./datasource/src/main/resources/data/samples.log

Kinesis stream returns always 0 records (don't know why, tried different ShardIteratorType). See `KinesisDataProvider`


#### Prerequisites
- docker
- sbt

For better performance, time series collection should be created in mongo before running the app:
````js
db.createCollection(
    "metrics", 
    { 
        "timeseries" : { 
            "timeField": "timestamp", 
            "metaField": "eventType", 
            "granularity": "seconds" //for test puproses it's ok
        }
    })
````

Configiration:
```
server {
  host = "localhost"
  port = 9000
}

mongo {
  host = "localhost"
  port = 27017
  database = test_db
  events-collection = "metrics"
  user = "fake"
  password = "fake"
}

event-consumer {
  # how many metrics will be inserted by one request
  batch-size = 1000
}

# should be true if you want to enrich DB with the test data
produce-test-data = false
```


#### How to run

- setup `produce-test-data` flag in `application.conf` if you want to enrich DB with test data
- docker-compose up -d
- sbt events-service/run

#### How to test

```
curl -XGET http://localhost:9000/mars/average?from=2018-08-01T19:26:01&to=2019-09-12T19:26:01
```

#### TODO
- Integration tests (setup env to run tests)
- Error handling (handle data stream fails, errors from db, error protocol, etc.)
- More advanced consumer configuration
- Metrics (Prometheus)
- Logging
