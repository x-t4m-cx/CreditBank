package com.creditbank.deal.api;

import org.springframework.http.ResponseEntity;

public interface DocumentApi {

    ResponseEntity<Void> sendDocuments(String statementId);
    ResponseEntity<Void> signDocuments(String statementId);
    ResponseEntity<Void> verifySesCode(String statementId);
}
