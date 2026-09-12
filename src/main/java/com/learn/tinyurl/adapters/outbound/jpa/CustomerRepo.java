package com.learn.tinyurl.adapters.outbound.jpa;

import com.learn.tinyurl.adapters.outbound.persistence.CustomerEntity;
import com.learn.tinyurl.model.RegisterCustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepo extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByUsername(String username);
}
