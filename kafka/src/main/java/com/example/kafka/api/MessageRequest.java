package com.example.kafka.api;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest (@NotBlank String key, @NotBlank String value) {}