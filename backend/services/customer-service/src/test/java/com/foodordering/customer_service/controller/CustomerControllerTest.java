package com.foodordering.customer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodordering.customer_service.dto.CreateCustomerRequest;
import com.foodordering.customer_service.dto.CustomerResponse;
import com.foodordering.customer_service.exception.GlobalExceptionHandler;
import com.foodordering.customer_service.service.CustomerNotFoundException;
import com.foodordering.customer_service.service.CustomerService;
import com.foodordering.customer_service.service.DuplicateCustomerEmailException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void createShouldReturn201() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Aarav",
                "Sharma",
                "aarav@example.com",
                "9876543210"
        );

        Instant now = Instant.now();

        CustomerResponse response = new CustomerResponse(
                UUID.randomUUID(),
                "Aarav",
                "Sharma",
                "aarav@example.com",
                "9876543210",
                true,
                now,
                now
        );

        when(customerService.create(any(CreateCustomerRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("aarav@example.com"));
    }

    @Test
    void createShouldReturn409ForDuplicateEmail() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Aarav",
                "Sharma",
                "aarav@example.com",
                "9876543210"
        );

        when(customerService.create(any(CreateCustomerRequest.class)))
                .thenThrow(new DuplicateCustomerEmailException(
                        "Email already exists"
                ));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void findByIdShouldReturn404WhenCustomerDoesNotExist()
            throws Exception {
        UUID id = UUID.randomUUID();

        when(customerService.findById(id))
                .thenThrow(new CustomerNotFoundException(id));

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400ForInvalidRequest() throws Exception {
        String invalidRequest = """
                {
                  "firstName": "",
                  "lastName": "Sharma",
                  "email": "not-an-email",
                  "phone": "12345"
                }
                """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }
}