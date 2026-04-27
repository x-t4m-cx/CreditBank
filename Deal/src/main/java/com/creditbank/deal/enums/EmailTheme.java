package com.creditbank.deal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTheme {
    FINISH_REGISTRATION,
    CREATE_DOCUMENTS,
    SEND_DOCUMENTS,
    SEND_SES,
    CREDIT_ISSUED,
    STATEMENT_DENIED
}