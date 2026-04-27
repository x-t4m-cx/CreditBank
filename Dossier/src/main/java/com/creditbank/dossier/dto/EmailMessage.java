package com.creditbank.dossier.dto;

import com.creditbank.dossier.enums.EmailTheme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String address;
    private EmailTheme theme;
    private UUID statementId;
    private String text;
}
