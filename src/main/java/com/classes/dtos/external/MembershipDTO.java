package com.classes.dtos.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipDTO {
    @JsonProperty("planName")
    private String type;

    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer daysRemaining;
    private boolean isActive;
    private boolean isExpired;
}
