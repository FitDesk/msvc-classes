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

    @KafkaListener(
            topics = "trainer-events",
            groupId = "msvc-classes-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeTrainerCreatedEvent(
            ConsumerRecord<String, TrainerCreatedEvent> record,
            Acknowledgment acknowledgment) {

        TrainerCreatedEvent event = record.value();
        log.info("Recibido evento TRAINER_CREATED: userId={}, eventId={}",
                event.userId(), event.eventId());

        try {
            if (trainerRepository.existsById(event.userId())) {
                log.warn("Trainer con userId={} ya existe. Evento duplicado ignorado.", event.userId());
                acknowledgment.acknowledge();
                return;
            }

            TrainerEntity trainer = TrainerEntity.builder()
                    .id(event.userId())
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

            // Confirmar procesamiento
            acknowledgment.acknowledge();

        } catch (
                Exception e) {
            log.error("Error procesando evento TRAINER_CREATED para userId={}", event.userId(), e);
            // No hacer acknowledge para que Kafka reintente
            // O enviar a DLQ (Dead Letter Queue)
            throw e;
        }
    }
}
