package jdev.kovalev.service;

import jdev.kovalev.dto.kafka.EventForNotificationSrv;

public interface KafkaProducerService {
    void sendConfirmationCode(EventForNotificationSrv eventForKafka);
}