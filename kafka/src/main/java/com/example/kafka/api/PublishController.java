package com.example.kafka.api;

import com.example.kafka.kafka.MessagePublisher;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class PublishController {

    private final MessagePublisher messagePublisher;

    public PublishController(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @PostMapping
    public ResponseEntity<String> publish(@Valid @RequestBody MessageRequest request) {
        messagePublisher.publish(request.key(), request.value());
        return ResponseEntity.accepted().body("queued");
    }
}