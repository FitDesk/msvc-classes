package com.classes.controllers;


import com.classes.dtos.Dashboard.MemberDashboardDTO;
import com.classes.services.AuthorizationService;
import com.classes.services.Impl.DashboardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardServiceImpl dashboardService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Obtener dashboard del miembro logueado")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/member")
    public ResponseEntity<MemberDashboardDTO> getMemberDashboard(Authentication authentication) {
        UUID memberId = authorizationService.getUserId(authentication);
        log.info("📊 Usuario {} consultando su dashboard", memberId);

        MemberDashboardDTO dashboard = dashboardService.getDashboardForMember(memberId);
        return ResponseEntity.ok(dashboard);
    }
}
