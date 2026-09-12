package com.learn.tinyurl.adapters.inbound;

import com.learn.tinyurl.api.CustomerApi;
import com.learn.tinyurl.domain.model.Customer;
import com.learn.tinyurl.model.CustomerResponse;
import com.learn.tinyurl.model.RegisterCustomerRequest;
import com.learn.tinyurl.ports.inbound.CustomerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController implements CustomerApi {

  private final CustomerPort customerPort;

    public CustomerController(CustomerPort customerPort) {
        this.customerPort = customerPort;
    }


    @Override
    public ResponseEntity<CustomerResponse> registerCustomer(RegisterCustomerRequest registerCustomerRequest) {

        Customer customer = customerPort.register(new Customer(registerCustomerRequest.getEmail(),
                registerCustomerRequest.getUsername(), registerCustomerRequest.getPassword(),
                registerCustomerRequest.getRole().getValue()));

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setEmail(customer.email());
        customerResponse.setUsername(customer.username());
        customerResponse.setRole(CustomerResponse.RoleEnum.fromValue(customer.role()));

        return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
    }

}
