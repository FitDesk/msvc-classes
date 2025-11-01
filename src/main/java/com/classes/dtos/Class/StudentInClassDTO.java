package com.classes.dtos.Class;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentInClassDTO {
    private UUID memberId;
    private UUID reservationId;
    private String name;
    private String email;
    private String avatarInitials;
    private String profileImageUrl;
    private String status;
    private String membershipType;

    private String attendanceStatus;

    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime checkInTime;

    private double attendancePercentage;
    private int totalClasses;

    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDateTime lastAccess;
}
