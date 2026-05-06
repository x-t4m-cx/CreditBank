package com.creditbank.deal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTheme {
    FINISH_REGISTRATION("Завершение регистрации"),
    CREATE_DOCUMENTS("Создание документов"),
    SEND_DOCUMENTS("Отправка документов"),
    SEND_SES("Отправка кода"),
    CREDIT_ISSUED("Кредит оформлен"),
    STATEMENT_DENIED("Заявка отклонена");

    private final String description;
}