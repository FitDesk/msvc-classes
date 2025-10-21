package com.classes.listeners;

import com.classes.entities.TrainerEntity;
import com.classes.enums.TrainerStatus;
import com.classes.events.TrainerCreatedEvent;
import com.classes.repositories.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerEventHandler {
    private final TrainerRepository trainerRepository;

    @KafkaListener(topics = "trainer-created-event-topic")
    @Transactional
    public void consumeTrainerCreatedEvent(
            ConsumerRecord<String, TrainerCreatedEvent> record,
            Acknowledgment acknowledgment) {

        TrainerCreatedEvent event = record.value();
        log.info("Recibido evento TRAINER_CREATED: userId={}, eventId={}",
                event.id(), event.eventId());

        try {
            if (trainerRepository.existsById(event.id())) {
                log.warn("Trainer con userId={} ya existe. Evento duplicado ignorado.", event.id());
                acknowledgment.acknowledge();
                return;
            }

            TrainerEntity trainer = TrainerEntity.builder()
                    .id(event.id())
                    .firstName(event.firstName())
                    .lastName(event.lastName())
                    .dni(event.dni())
                    .email(event.email())
                    .phone(event.phone())
                    .hireDate(LocalDate.now())
                    .status(TrainerStatus.ACTIVO)
                    .yearsOfExperience(0)
                    .build();

            trainerRepository.save(trainer);
            log.info("Trainer creado exitosamente con ID: {}", trainer.getId());

            acknowledgment.acknowledge();

        } catch (
                Exception e) {
            log.error("Error procesando evento TRAINER_CREATED para userId={}", event.id(), e);

            throw e;
        }
    }
}
