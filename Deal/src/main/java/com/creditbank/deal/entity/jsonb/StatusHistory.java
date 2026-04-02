package com.creditbank.deal.entity.jsonb;

import com.creditbank.deal.enums.ChangeType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    private String status;
    private LocalDateTime time;
    private ChangeType changeType;
}
