package com.foodordering.restaurant_service.repository;

import com.foodordering.restaurant_service.entity.Restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RestaurantRepositoryTest {

        @Autowired
        private RestaurantRepository restaurantRepository;

        @Test
        void shouldSaveAndFindRestaurant() {
                Restaurant restaurant = new Restaurant(
                                "Test Restaurant",
                                "Test description",
                                "Indore, Madhya Pradesh");

                Restaurant saved = restaurantRepository.saveAndFlush(restaurant);

                assertNotNull(saved.getId());
                assertNotNull(saved.getCreatedAt());
                assertNotNull(saved.getUpdatedAt());

                Restaurant found = restaurantRepository.findById(saved.getId())
                                .orElseThrow();

                assertEquals("Test Restaurant", found.getName());
                assertEquals("Indore, Madhya Pradesh", found.getAddress());
                assertTrue(found.isActive());
        }

        @Test
        void findByActiveTrueShouldExcludeInactiveRestaurants() {
                Restaurant active = new Restaurant(
                                "Active Restaurant",
                                "Available",
                                "Indore");

                Restaurant inactive = new Restaurant(
                                "Inactive Restaurant",
                                "Unavailable",
                                "Bhopal");

                inactive.setActive(false);

                restaurantRepository.saveAllAndFlush(List.of(active, inactive));

                List<Restaurant> results = restaurantRepository
                                .findByActiveTrue(org.springframework.data.domain.PageRequest.of(0, 10))
                                .getContent();

                assertTrue(results.stream()
                                .anyMatch(r -> r.getName().equals("Active Restaurant")));

                assertFalse(results.stream()
                                .anyMatch(r -> r.getName().equals("Inactive Restaurant")));
        }
}