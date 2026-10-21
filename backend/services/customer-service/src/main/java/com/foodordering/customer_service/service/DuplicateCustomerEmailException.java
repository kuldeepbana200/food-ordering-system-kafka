package com.foodordering.customer_service.service;

public class DuplicateCustomerEmailException extends RuntimeException {

    public DuplicateCustomerEmailException(String email) {
        super("A customer with this email already exists: " + email);
    }
}