package com.foodordering.customer_service.service;

import com.foodordering.customer_service.dto.CreateCustomerRequest;
import com.foodordering.customer_service.dto.CustomerResponse;
import com.foodordering.customer_service.entity.Customer;
import com.foodordering.customer_service.repository.CustomerRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Create a customer
    public CustomerResponse create(CreateCustomerRequest request) {

        // Normalize email and store it in a consistent format
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check if the customer already exists
        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateCustomerEmailException(email);
        }

        // Validate and normalize the phone number
        String phone = normalizePhone(request.phone());

        Customer customer = new Customer(
                request.firstName().trim(),
                request.lastName().trim(),
                email,
                phone
        );

        // Flush immediately so database constraint violations can be caught here
        try {
            return toResponse(customerRepository.saveAndFlush(customer));
        } catch (DataIntegrityViolationException exception) {
            // Translate a database integrity error into an application exception
            // Ideally, only duplicate-email constraint violations should be translated this way
            throw new DuplicateCustomerEmailException(email);
        }
    }

    // Find customer by ID
    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return toResponse(customer);
    }

    // Find only active customers
    @Transactional(readOnly = true)
    public List<CustomerResponse> findActive() {
        return customerRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Normalize and validate an Indian mobile number

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String normalized = phone.trim().replaceAll("[\\s()-]", "");

        // Accept either a 10-digit Indian mobile number
        // or the same number with the +91 country code.
        if (normalized.matches("^[6-9]\\d{9}$")) {
            return normalized;
        }

        if (normalized.matches("^\\+91[6-9]\\d{9}$")) {
            return normalized;
        }

        throw new InvalidPhoneNumberException(
                "Phone number must be a valid Indian mobile number, "
                        + "with or without the +91 country code."
        );
    }


    // Convert Customer entity into response DTO
    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.isActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}