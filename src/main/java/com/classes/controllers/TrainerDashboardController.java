package com.classes.controllers;

import com.classes.dtos.Dashboard.TrainerDashboardDTO;
import com.classes.services.AuthorizationService;
import com.classes.services.TrainerDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/dashboard/trainer")
@RequiredArgsConstructor
@Slf4j
public class TrainerDashboardController {
    
    private final TrainerDashboardService dashboardService;
    private final AuthorizationService authorizationService;
    
    @Operation(summary = "Obtener dashboard del trainer autenticado con métricas")
    @GetMapping
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<TrainerDashboardDTO> getTrainerDashboard(Authentication authentication) {
        UUID trainerId = authorizationService.getUserId(authentication);
        log.info("Trainer {} consultando su dashboard", trainerId);
        
        TrainerDashboardDTO dashboard = dashboardService.getDashboardForTrainer(trainerId);
        return ResponseEntity.ok(dashboard);
    }
}
