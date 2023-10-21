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
- [docker-compose]()

## Запуск

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
POSTGRES_USER='documents' \
POSTGRES_PASSWORD='documents' \
POSTGRES_DB='documents' \
docker-compose up
```

### Адрес страницы
```
http://localhost:3006/#/
```
