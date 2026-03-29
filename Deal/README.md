# Микросервис Сделка

## Назначение

Сервис для управления кредитными заявками: создание заявок, выбор кредитного предложения и расчет кредита на основе
скоринга.

## Функциональность

### 1. Создание заявки

**POST** `/deal/statement`

- По API приходит `LoanStatementRequestDto`.
- Создается сущность `Client` и сохраняется в базе данных.
- Создается `Statement` со связью на созданного `Client` и сохраняется в базе.
- Отправляется **POST** запрос на `/calculator/offers` в микросервис Калькулятор через `RestClient`.
- Каждому элементу списка `List<LoanOfferDto>` присваивается `statementId` созданной заявки.
- Ответ на API — список из 4 `LoanOfferDto`, отсортированных от "худшего" к "лучшему".

### 2. Выбор предложения

**POST** `/deal/offer/select`

- По API приходит `LoanOfferDto`.
- Из базы достается заявка (`Statement`) по `statementId` из `LoanOfferDto`.
- В заявке обновляется:
    - статус
    - история статусов (`List<StatementStatusHistoryDto>`)
    - принятое предложение (`appliedOffer`)
- Заявка сохраняется в базе.

### 3. Расчет кредита

**POST** `/deal/calculate/{statementId}`

- По API приходит объект `FinishRegistrationRequestDto` и параметр `statementId` (`String`).
- Из базы достается заявка (`Statement`) по `statementId`.
- `ScoringDataDto` насыщается информацией из `FinishRegistrationRequestDto` и `Client`, который хранится в `Statement`.
- Отправляется **POST** запрос на `/calculator/calc` в микросервис Калькулятор с телом `ScoringDataDto` через
  `RestClient`.
- На основе полученного из кредитного конвейера `CreditDto` создается сущность `Credit` и сохраняется в базе со статусом
  `CALCULATED`.
- В заявке обновляется статус и история статусов.
- Заявка сохраняется.

## Технологии

- Java 21
- Spring Boot 3.5.11
- PostgreSQL
- Spring Data JPA
- JUnit / Mockito
- Lombok
- Swagger (springdoc)