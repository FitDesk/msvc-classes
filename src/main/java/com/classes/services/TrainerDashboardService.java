package com.classes.services;

import com.classes.dtos.Dashboard.TrainerDashboardDTO;

import java.util.UUID;

public interface TrainerDashboardService {

    TrainerDashboardDTO getDashboardForTrainer(UUID trainerId);
}
