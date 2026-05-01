package com.creditbank.deal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ses code", description = "ses code for verify")
public class VerifySesCodeRequest {

    @NotNull(message = "code is required")
    @Pattern(regexp = "^\\d{4}$", message = "code must be a 4-digit number")
    @Schema(example = "1234")
    String code;
}
