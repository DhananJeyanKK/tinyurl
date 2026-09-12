package com.learn.tinyurl.adapters.outbound.impl;

import com.learn.tinyurl.adapters.outbound.jpa.CustomerRepo;
import com.learn.tinyurl.adapters.outbound.persistence.CustomerEntity;
import com.learn.tinyurl.domain.model.Customer;
import com.learn.tinyurl.ports.outbound.CustomerRepoPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomerRepoPortImpl implements CustomerRepoPort {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public CustomerRepoPortImpl(CustomerRepo customerRepo, PasswordEncoder passwordEncoder) {
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Customer save(Customer customer) {
        return toDomain(customerRepo.save(toEntity(customer)));
    }

    public Customer toDomain(CustomerEntity customerEntity){
        return new Customer(customerEntity.getEmail(),
                customerEntity.getUsername(), null, customerEntity.getRole().getValue());

    }

    public CustomerEntity toEntity(Customer customer){
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setEmail(customer.email());
        customerEntity.setUsername(customer.username());
        customerEntity.setPassword(passwordEncoder.encode(customer.password()));
        customerEntity.setRole(CustomerEntity.Role.valueOf(customer.role()));
        return customerEntity;
    }
}
