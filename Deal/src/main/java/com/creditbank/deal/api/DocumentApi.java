package com.creditbank.deal.api;

import com.creditbank.deal.dto.request.VerifySesCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface DocumentApi {
    @Operation(
            summary = "Generating a email message for send documents",
            description = "Update statement status, generate email message and async send to dossier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Updated statement and send email"),
            @ApiResponse(responseCode = "404", description = "Not found statement"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> sendDocuments(String statementId);

    @Operation(
            summary = "Generating a email message for sign documents",
            description = "Update statement with ses code, generate email message and async send to dossier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Updated statement and send email"),
            @ApiResponse(responseCode = "404", description = "Not found statement"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> signDocuments(String statementId);

    @Operation(
            summary = "Generating a email message success issue credit",
            description = "Update statement status, generate email message and async send to dossier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Updated statement and send email"),
            @ApiResponse(responseCode = "400", description = "not correct codes"),
            @ApiResponse(responseCode = "422", description = "codes do not match"),
            @ApiResponse(responseCode = "404", description = "Not found statement"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> verifySesCode(String statementId, VerifySesCodeRequest code);
}
