package com.learn.tinyurl.domain.service;

import com.learn.tinyurl.domain.model.Customer;
import com.learn.tinyurl.ports.inbound.CustomerPort;
import com.learn.tinyurl.ports.outbound.CustomerRepoPort;
import com.learn.tinyurl.util.DomainService;

@DomainService
public class CustomerService implements CustomerPort {

    private final CustomerRepoPort customerRepoPort;

    public CustomerService(CustomerRepoPort customerRepoPort) {
        this.customerRepoPort = customerRepoPort;
    }


    @Override
    public Customer register(Customer customer) {
        return customerRepoPort.save(customer);
    }
}
