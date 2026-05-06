package com.creditbank.deal.controller;

import com.creditbank.deal.api.DocumentApi;
import com.creditbank.deal.dto.request.VerifySesCodeRequest;
import com.creditbank.deal.service.DocumentService;
import com.creditbank.deal.service.StatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal/document")
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    @PostMapping("/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable String statementId) {
        documentService.sendDocuments(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }

    @PostMapping("/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String statementId) {
        documentService.signDocuments(UUID.fromString(statementId));
        return ResponseEntity.status(202).build();
    }

    @PostMapping("/{statementId}/code")
    public ResponseEntity<Void> verifySesCode(
            @PathVariable String statementId,
            @Valid @RequestBody VerifySesCodeRequest code) {
        documentService.verifySesCode(UUID.fromString(statementId), code);
        return ResponseEntity.status(202).build();
    }
}
