package com.creditbank.deal.api;

import com.creditbank.deal.dto.response.StatementDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface AdminApi {

    @Operation(
            summary = "Get statement by ID",
            description = "Retrieves a specific statement by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statement successfully retrieved",
                    content = @Content(schema = @Schema(implementation = StatementDto.class))),
            @ApiResponse(responseCode = "404", description = "Statement not found with the given ID",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    ResponseEntity<StatementDto> getStatementById(
            @Parameter(description = "Unique identifier of the statement", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String statementId
    );

    @Operation(
            summary = "Get all statements with pagination",
            description = "Retrieves a paginated list of all statements sorted by creation date (descending by default)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statements successfully retrieved",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "Page number (0-indexed)", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "Number of records per page", in = ParameterIn.QUERY, example = "20"),
            @Parameter(name = "sort", description = "Sort criteria: field, direction (e.g., creationDate,desc)",
                    in = ParameterIn.QUERY, example = "creationDate,desc")
    })
    ResponseEntity<Page<StatementDto>> getAllStatements(
            @ParameterObject
            @PageableDefault(
                    size = 20,
                    sort = "creationDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    );
}