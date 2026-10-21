package com.foodordering.customer_service.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}