package com.example.kafka.infra;

import com.example.kafka.api.MessageRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.profiles.active=test",
        "app.topic=demo.messages"
})
@EmbeddedKafka(partitions = 1, topics = { "demo.messages" })
@DirtiesContext
class PublisherListenerTest {

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void testPublishIsConsumedByListener() throws Exception {

        messagePublisher.publish("k1", "v1").get();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, String>(consumerProps);
        try (var consumer = consumerFactory.createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "demo.messages");
            var record = KafkaTestUtils.getSingleRecord(consumer, "demo.messages", Duration.ofSeconds(5));
            assertThat(record.key()).isEqualTo("k1");
            assertThat(record.value()).isEqualTo("v1");
        }
    }
}