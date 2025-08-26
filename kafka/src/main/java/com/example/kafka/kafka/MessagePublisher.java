package com.example.kafka.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class MessagePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    public MessagePublisher(KafkaTemplate<String, String> kafkaTemplate,
                            @Value("${app.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }
    public CompletableFuture<Void> publish(String key, String value) {
        return kafkaTemplate.send(topic, key, value) // CompletableFuture<SendResult<String,String>>
                .thenAcceptAsync(result -> {              // :white_check_mark: devuelve CompletableFuture<Void>
                    var meta = result.getRecordMetadata();
                    System.out.printf("[PUBLISH OK] topic=%s partition=%d offset=%d key=%s value=%s%n",
                            meta.topic(), meta.partition(), meta.offset(), key, value);
                })
                .exceptionally(ex -> {                    // sigue siendo Void; devolver null está OK
                    System.err.printf("[PUBLISH ERROR] %s%n", ex.getMessage());
                    return null;
                });
    }
}