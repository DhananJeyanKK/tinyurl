package com.learn.tinyurl.ports.outbound;

import com.learn.tinyurl.domain.model.Customer;

public interface CustomerRepoPort {

    Customer save(Customer customer);
}
