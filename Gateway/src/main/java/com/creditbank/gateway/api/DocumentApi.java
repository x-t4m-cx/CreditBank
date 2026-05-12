package com.creditbank.gateway.api;

import com.creditbank.gateway.dto.request.VerifySesCodeRequest;
import com.creditbank.gateway.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface DocumentApi {

    @Operation(
            summary = "Send documents to client",
            description = "Forwards a request to the Deal microservice to generate and send documents."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documents sent"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server or downstream error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> createDocuments(String statementId);

    @Operation(
            summary = "Prepare documents for signing",
            description = "Forwards a request to the Deal microservice to generate a signing code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signing code generated"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server or downstream error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> signDocuments(String statementId);

    @Operation(
            summary = "Verify SES code",
            description = "Forwards a request to the Deal microservice to verify the signing code."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SES code verified"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation or deserialization error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Statement not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Code does not match",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2026-03-28T13:33:47.1076646",
                                              "status": 422,
                                              "error": "Code not verify",
                                              "message": "SES code does not match",
                                              "errors": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server or downstream error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> verifySesCode(String statementId, VerifySesCodeRequest code);
}
