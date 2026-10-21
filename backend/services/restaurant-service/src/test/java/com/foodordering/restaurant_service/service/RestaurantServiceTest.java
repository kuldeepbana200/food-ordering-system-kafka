package com.foodordering.restaurant_service.service;

import com.foodordering.restaurant_service.dto.CreateRestaurantRequest;
import com.foodordering.restaurant_service.dto.RestaurantResponse;
import com.foodordering.restaurant_service.entity.Restaurant;
import com.foodordering.restaurant_service.repository.RestaurantRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant(
                "Spice Garden",
                "Indian cuisine",
                "Indore, Madhya Pradesh");
    }

    @Test
    void createShouldReturnSavedRestaurant() {
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenAnswer(invocation -> {
                    Restaurant saved = invocation.getArgument(0);

                    // Simulate values normally assigned by persistence.
                    return saved;
                });

        RestaurantResponse response = restaurantService.create(
                new CreateRestaurantRequest(
                        "Spice Garden",
                        "Indian cuisine",
                        "Indore, Madhya Pradesh"));

        assertEquals("Spice Garden", response.name());
        assertEquals("Indore, Madhya Pradesh", response.address());
        assertTrue(response.active());

        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void findByIdShouldThrowWhenRestaurantDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(restaurantRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                RestaurantNotFoundException.class,
                () -> restaurantService.findById(id));
    }

    @Test
    void deactivateShouldSetRestaurantInactive() {
        UUID id = UUID.randomUUID();

        when(restaurantRepository.findById(id))
                .thenReturn(Optional.of(restaurant));

        when(restaurantRepository.save(restaurant))
                .thenReturn(restaurant);

        RestaurantResponse response = restaurantService.deactivate(id);

        assertFalse(response.active());
        verify(restaurantRepository).save(restaurant);
    }
}