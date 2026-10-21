package com.foodordering.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRestaurantRequest(

        @NotBlank(message = "Restaurant name is required")
        @Size(max = 150, message = "Name cannot exceed 150 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @NotBlank(message = "Restaurant address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address

) {
}