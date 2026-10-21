package com.foodordering.restaurant_service.controller;

import com.foodordering.restaurant_service.dto.CreateRestaurantRequest;
import com.foodordering.restaurant_service.dto.RestaurantResponse;
import com.foodordering.restaurant_service.dto.UpdateRestaurantRequest;
import com.foodordering.restaurant_service.service.RestaurantService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(
            @Valid @RequestBody CreateRestaurantRequest request) {
        return restaurantService.create(request);
    }

    @GetMapping
    public Page<RestaurantResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        if (!sortBy.equals("name")
                && !sortBy.equals("createdAt")
                && !sortBy.equals("updatedAt")) {
            throw new IllegalArgumentException("Unsupported sort field");
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Direction must be 'asc' or 'desc'");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy));

        return restaurantService.findAll(activeOnly, pageable);
    }

    @GetMapping("/{id}")
    public RestaurantResponse findById(@PathVariable @NonNull UUID id) {
        return restaurantService.findById(id);
    }

    @DeleteMapping("/{id}")
    public RestaurantResponse deactivate(@PathVariable @NonNull UUID id) {
        return restaurantService.deactivate(id);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@PathVariable @NonNull UUID id,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        return restaurantService.update(id, request);
    }

}