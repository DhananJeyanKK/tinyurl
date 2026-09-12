package com.learn.tinyurl.ports.inbound;

import com.learn.tinyurl.domain.model.Customer;

public interface CustomerPort {

    Customer register(Customer customer);

}
