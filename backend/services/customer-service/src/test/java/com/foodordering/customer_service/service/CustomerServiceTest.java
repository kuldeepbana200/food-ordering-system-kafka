package com.foodordering.customer_service.service;

import com.foodordering.customer_service.dto.CreateCustomerRequest;
import com.foodordering.customer_service.dto.CustomerResponse;
import com.foodordering.customer_service.entity.Customer;
import com.foodordering.customer_service.repository.CustomerRepository;

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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createShouldNormalizeEmailAndSaveCustomer() {
        when(customerRepository.existsByEmail("aarav@example.com"))
                .thenReturn(false);

        when(customerRepository.saveAndFlush(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateCustomerRequest request = new CreateCustomerRequest(
                "Aarav",
                "Sharma",
                " Aarav@Example.com ",
                "+919876543210"
        );

        CustomerResponse response = customerService.create(request);

        assertEquals("Aarav", response.firstName());
        assertEquals("Sharma", response.lastName());
        assertEquals("aarav@example.com", response.email());
        assertEquals("+919876543210", response.phone());
        assertTrue(response.active());

        verify(customerRepository).saveAndFlush(any(Customer.class));
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        when(customerRepository.existsByEmail("aarav@example.com"))
                .thenReturn(true);

        CreateCustomerRequest request = new CreateCustomerRequest(
                "Aarav",
                "Sharma",
                "aarav@example.com",
                "9876543210"
        );

        assertThrows(
                DuplicateCustomerEmailException.class,
                () -> customerService.create(request)
        );

        verify(customerRepository, never())
                .saveAndFlush(any(Customer.class));
    }

    @Test
    void findByIdShouldThrowWhenCustomerDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(customerRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findById(id)
        );
    }

    @Test
    void createShouldRejectInvalidPhoneNumber() {
        when(customerRepository.existsByEmail("aarav@example.com"))
                .thenReturn(false);

        CreateCustomerRequest request = new CreateCustomerRequest(
                "Aarav",
                "Sharma",
                "aarav@example.com",
                "12345"
        );

        assertThrows(
                InvalidPhoneNumberException.class,
                () -> customerService.create(request)
        );

        verify(customerRepository, never())
                .saveAndFlush(any(Customer.class));
    }
}