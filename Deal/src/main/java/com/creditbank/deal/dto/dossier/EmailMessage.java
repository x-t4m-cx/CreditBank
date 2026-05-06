package com.creditbank.deal.dto.dossier;

import com.creditbank.deal.enums.EmailTheme;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
    private String text;
}