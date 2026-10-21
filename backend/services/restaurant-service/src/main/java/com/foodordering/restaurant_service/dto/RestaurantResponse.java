package com.foodordering.restaurant_service.dto;

import java.time.Instant;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String description,
        String address,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
