package com.foodordering.restaurant_service.service;

import com.foodordering.restaurant_service.dto.CreateRestaurantRequest;
import com.foodordering.restaurant_service.dto.RestaurantResponse;
import com.foodordering.restaurant_service.dto.UpdateRestaurantRequest;
import com.foodordering.restaurant_service.entity.Restaurant;
import com.foodordering.restaurant_service.repository.RestaurantRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // Create a Restaurant
    public RestaurantResponse create(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant(
                request.name().trim(),
                request.description(),
                request.address().trim());

        return toResponse(restaurantRepository.save(restaurant));
    }

    // Find all the Restaurants
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> findAll(
            boolean activeOnly,
            Pageable pageable) {

        Page<Restaurant> restaurants = activeOnly
                ? restaurantRepository.findByActiveTrue(pageable)
                : restaurantRepository.findAll(pageable);

        return restaurants.map(this::toResponse);
    }

    // Find Restaurant by ID
    @Transactional(readOnly = true)
    public RestaurantResponse findById(@NonNull UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        return toResponse(restaurant);
    }

    // Deactivate a Restaurant by ID ( Won't delete it from the database )
    public RestaurantResponse deactivate(@NonNull UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        restaurant.setActive(false);

        return toResponse(restaurantRepository.save(restaurant));
    }

    // Update a Restaurant
    public RestaurantResponse update(@NonNull UUID id, UpdateRestaurantRequest request) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        restaurant.setAddress(request.address());
        restaurant.setDescription(request.description());
        restaurant.setName(request.name());

        return toResponse(restaurantRepository.save(restaurant));
    }


    private RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getAddress(),
                restaurant.isActive(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt());
    }
}