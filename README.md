## Description

Document processing application. Documents are created and uploaded via a web
interface and processed in a third-party application that interacts with Kafka.

## Structure

- `backend` - java backend
- `ui` - react + redux frontend

## Prerequisites

- [node](https://nodejs.org)
- [openjdk](https://openjdk.java.net)
- [docker](https://docs.docker.com/engine/install/)
- [docker-compose](https://docs.docker.com/compose/install/)

## Running using docker

```
./gradlew backend:bootJar
```

```
./gradlew ui:npm_run_build
```

```
SPRING_DATASOURCE_URL='jdbc:postgresql://db:5432/documents' \
SPRING_DATASOURCE_USERNAME='documents' \
SPRING_DATASOURCE_PASSWORD='documents' \
SPRING_KAFKA_BOOTSTRAP_SERVERS='kafka:9092' \
POSTGRES_USER='documents' \
POSTGRES_PASSWORD='documents' \
POSTGRES_DB='documents' \
docker-compose up
```

## Running frontend and backend locally

Runnig kafka and database

```
POSTGRES_USER='documents' \
POSTGRES_PASSWORD='documents' \
POSTGRES_DB='documents' \
docker-compose up
```

Running frontend:

```
./gradlew ui:npm_run_start
```

Running backend:

```
./gradlew backend:bootJar
```

```
java \
-DSPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/documents \
-DSPRING_DATASOURCE_USERNAME=documents \
-DSPRING_DATASOURCE_PASSWORD=documents \
-DSPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:19092 \
-jar backend/build/libs/backend-1.0-SNAPSHOT.jar
```

### Usage

Frontend url: http://localhost:9000/#/

After sending a document for processing, a message about it will appear in the `documents-in` topic.
After that, send a message to the `documents-out` topic with the same key as the incoming one, the body of the message should be as following:

```json
{
  "documentId": 1, //id of the document that is in the "payload" field of the incoming message
  "statusCode": "ACCEPTED" //status - the result of document processing, can be "ACCEPTED" or "REJECTED"
}
```

If incorrect values ​​are passed, an error is logged.
