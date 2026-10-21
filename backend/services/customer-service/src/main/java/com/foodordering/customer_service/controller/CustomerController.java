package com.foodordering.customer_service.controller;

import com.foodordering.customer_service.dto.CreateCustomerRequest;
import com.foodordering.customer_service.dto.CustomerResponse;
import com.foodordering.customer_service.service.CustomerService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Create a Customer
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(
            @Valid @RequestBody CreateCustomerRequest request) {
        return customerService.create(request);
    }

    // Find a user By id
    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable UUID id) {
        return customerService.findById(id);
    }

    // Get all the active users
    @GetMapping
    public List<CustomerResponse> findActive() {
        return customerService.findActive();
    }
}