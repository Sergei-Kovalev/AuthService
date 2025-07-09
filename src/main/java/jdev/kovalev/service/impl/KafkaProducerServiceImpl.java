package jdev.kovalev.service.impl;

import jdev.kovalev.dto.kafka.EventForNotificationSrv;
import jdev.kovalev.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
    public static final String TOPIC_NAME = "confirmation-codes-from-authsrv-to-notification-srv";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, EventForNotificationSrv> kafkaTemplate;

    @Override
    public void sendConfirmationCode(EventForNotificationSrv eventForKafka) {
        CompletableFuture<SendResult<String, EventForNotificationSrv>> future = kafkaTemplate.send(TOPIC_NAME, eventForKafka);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Message sent successfully to: topic {}, partition {}, offset {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                logger.info("Event for kafka sent: {}",
                            result.getProducerRecord().value().toString());
            } else {
                logger.info("Message sent failed: {}", exception.getMessage());
            }
        });
    }
}
