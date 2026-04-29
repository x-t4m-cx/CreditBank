package com.creditbank.deal.controller;

import com.creditbank.deal.api.DocumentApi;
import com.creditbank.deal.service.DocumentService;
import com.creditbank.deal.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal/document")
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;
    private final StatementService statementService;

    @PostMapping("/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable String statementId) {
        documentService.sendSendDocumentRequest(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }

    @PostMapping("/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String statementId) {
        documentService.sendSignDocumentRequest(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }

    @PostMapping("/{statementId}/code")
    public ResponseEntity<Void> verifySesCode(
            @PathVariable String statementId,
            @RequestBody String code) {

        if (code == null || code.isBlank() || code.length() != 4) {
            return ResponseEntity.status(400).build();
        }
        if (!statementService.verifyCode(UUID.fromString(statementId), code)) {
            return ResponseEntity.status(400).build();
        }

        documentService.sendCreditIssueCredit(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }
}
