package com.classes.dtos.Trainer;
import com.classes.enums.ContractType;
import com.classes.enums.DayAvailability;
import com.classes.enums.Gender;
import com.classes.enums.TrainerStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class TrainerResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String dni;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;
    private Gender gender;
    private String phone;
    private String email;
    private String address;
    private String profileImageUrl;
    private String specialties;
    private int yearsOfExperience;
    private List<String> certifications;
    private Set<DayAvailability> availability;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate hireDate;
    private TrainerStatus status;
    private ContractType contractType;
    private BigDecimal salaryPerClass;
    private String bankInfo;
    private String notes;
}
