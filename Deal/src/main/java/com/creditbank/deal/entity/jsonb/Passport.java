package com.creditbank.deal.entity.jsonb;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Passport {
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
