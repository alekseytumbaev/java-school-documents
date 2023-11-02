## Описание

Фул стек приложение - справочник документов

## Структура

- `backend` - Бэкенд на java.
- `ui` - Фронтенд на react + redux.

## Подготовка

Установите:

- [node](https://nodejs.org) - front
- [openjdk](https://openjdk.java.net) 15 - java бэк
- [docker](https://docs.docker.com/engine/install/)
- [docker-compose](https://docs.docker.com/compose/install/)

## Запуск через докер

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

## Запуск фронта и бэка локально

Запуск базы данных и кафки:

```
POSTGRES_USER='documents' \
POSTGRES_PASSWORD='documents' \
POSTGRES_DB='documents' \
docker-compose up
```

Запуск фронта:

```
./gradlew ui:npm_run_start
```

Запуск бэка:
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

### Использование

Адрес страницы: http://localhost:9000/#/

После отправки документа на обработку, сообщение об этом появится в топике `documents-in`.
После этого отправьте в топик `documents-out` сообщение с таким же ключом, как у входящего, тело сообщения:

```json
{
  "documentId": 1, //id документа, который находится в поле payload входящего сообщения
  "statusCode": "ACCEPTED"//статус - результат обработки документа, может быть "ACCEPTED" или "REJECTED"
}
```

При передаче неправильных значений залогируется ошибка.
