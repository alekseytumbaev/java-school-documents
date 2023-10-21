## Описание

Фул стек приложение - справочник документов

## Структура

- `backend` - Бэкенд на java.
- `ui` - Фронтенд на react + redux.

## Подготовка

Установите:

- docker
- docker-compose

## Запуск

## Докер
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
