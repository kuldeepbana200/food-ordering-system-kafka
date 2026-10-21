package com.foodordering.restaurant_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.restaurant_service.dto.CreateRestaurantRequest;
import com.foodordering.restaurant_service.dto.RestaurantResponse;
import com.foodordering.restaurant_service.service.RestaurantNotFoundException;
import com.foodordering.restaurant_service.service.RestaurantService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.List;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@Import(com.foodordering.restaurant_service.exception.GlobalExceptionHandler.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void createShouldReturn201() throws Exception {
        UUID id = UUID.randomUUID();

        RestaurantResponse response = new RestaurantResponse(
                id,
                "Spice Garden",
                "Indian cuisine",
                "Indore, Madhya Pradesh",
                true,
                Instant.now(),
                Instant.now());

        when(restaurantService.create(any(CreateRestaurantRequest.class)))
                .thenReturn(response);

        CreateRestaurantRequest request = new CreateRestaurantRequest(
                "Spice Garden",
                "Indian cuisine",
                "Indore, Madhya Pradesh");

        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Spice Garden"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createShouldReturn400ForBlankName() throws Exception {
        String invalidJson = """
                {
                    "name": " ",
                    "description": "Indian cuisine",
                    "address": "Indore, Madhya Pradesh"
                }
                """;

        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"));
    }

    @Test
    void findByIdShouldReturn404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();

        when(restaurantService.findById(id))
                .thenThrow(new RestaurantNotFoundException(id));

        mockMvc.perform(get("/api/restaurants/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Restaurant Not Found"));
    }

    @Test
    void findAllShouldReturnPaginatedResponse() throws Exception {
        when(restaurantService.findAll(
                org.mockito.ArgumentMatchers.eq(false),
                any(org.springframework.data.domain.Pageable.class))).thenReturn(new PageImpl<>(
                        List.of(new RestaurantResponse(
                                UUID.randomUUID(),
                                "Spice Garden",
                                "Indian cuisine",
                                "Indore",
                                true,
                                Instant.now(),
                                Instant.now())),
                        PageRequest.of(0, 10),
                        1));

        mockMvc.perform(get("/api/restaurants")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("Spice Garden"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void findAllShouldRejectInvalidPageSize() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Request Parameter"));
    }

    @Test
    void findAllShouldRejectUnsupportedSortField() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                .param("sortBy", "password"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Request Parameter"));
    }

    @Test
    void createShouldRejectMalformedJson() throws Exception {
        mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Malformed Request"));
    }
}