package jdev.kovalev.service.impl;

import jdev.kovalev.dto.kafka.EventForNotificationSrv;
import jdev.kovalev.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@EmbeddedKafka(
        partitions = 1,
        topics = KafkaProducerServiceImpl.TOPIC_NAME,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
class KafkaProducerServiceImplTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaTemplate<String, EventForNotificationSrv> kafkaTemplate;

    @Autowired
    private ConsumerFactory<String, EventForNotificationSrv> consumerFactory;

    private KafkaProducerService service;
    private EventForNotificationSrv event;
    private Consumer<String, EventForNotificationSrv> consumer;

    @BeforeEach
    void setUp() throws Exception {
        service = new KafkaProducerServiceImpl(kafkaTemplate);
        event = EventForNotificationSrv.builder()
                .email("test@example.com")
                .confirmationCode("123456")
                .build();
        consumer = consumerFactory.createConsumer();
    }

    @Test
    void sendConfirmationCode_ShouldSendToRealKafka() throws Exception {
        consumer.subscribe(Collections.singleton(KafkaProducerServiceImpl.TOPIC_NAME));

        embeddedKafka.consumeFromAllEmbeddedTopics(consumer);
        Thread.sleep(1000);

        service.sendConfirmationCode(event);

        Thread.sleep(2000);

        ConsumerRecords<String, EventForNotificationSrv> records = consumer.poll(Duration.ofSeconds(3));

        assertThat(records).isNotEmpty();

        ConsumerRecord<String, EventForNotificationSrv> record = records.iterator().next();

        assertThat(record.value().email()).isEqualTo(event.email());
        assertThat(record.value().confirmationCode()).isEqualTo(event.confirmationCode());

        consumer.close();
    }
}