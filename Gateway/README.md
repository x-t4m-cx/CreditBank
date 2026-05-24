# Микросервис Заявление

## Назначение

Сервис для управления кредитными заявлениями: создание заявления на основе прескоринга, выбор кредитного предложения.

## Функциональность

### 1. Создание заявки

**POST** `/statement`

- По API приходит `LoanStatementRequestDto`.
- Происходит прескоринг входных данных(валидация полей LoanStatementRequestDto)
- Отправляется **POST** запрос на `/deal/statement` в микросервис Сделка через `RestClient`.
- Ответ на API — список из 4 `LoanOfferDto`, отсортированных от "худшего" к "лучшему".

### 2. Выбор предложения

**POST** `/statement/offer`

- По API приходит `LoanOfferDto`.
- Отправляется **POST** запрос на `/deal/offer/select` в микросервис Сделка через `RestClient`.

## Технологии

- Java 21
- Spring Boot 3.5.11
- JUnit / Mockito
- Lombok
- Swagger (springdoc)