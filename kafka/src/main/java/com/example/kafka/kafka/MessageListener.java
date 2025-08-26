package com.example.kafka.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    @KafkaListener(topics = "${app.topic}", groupId = "demo-consumers")
    public void onMessage(ConsumerRecord<String, String> record) {
        System.out.printf(
                "[CONSUME] topic=%s partition=%d offset=%d key=%s value=%s%n",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value()
        );
    }
}