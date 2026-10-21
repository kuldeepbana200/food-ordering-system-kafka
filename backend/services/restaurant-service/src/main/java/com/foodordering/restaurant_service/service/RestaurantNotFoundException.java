package com.foodordering.restaurant_service.service;

import java.util.UUID;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(UUID id) {
        super("Restaurant not found with id: " + id);
    }
}