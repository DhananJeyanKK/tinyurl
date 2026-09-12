package com.learn.tinyurl.security;

import com.learn.tinyurl.adapters.outbound.jpa.CustomerRepo;
import com.learn.tinyurl.adapters.outbound.persistence.CustomerEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class TinyDBUserDetailService implements UserDetailsService {

    private final CustomerRepo customerRepo;

    public TinyDBUserDetailService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        CustomerEntity customer = customerRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String roleName = "ROLE_" + customer.getRole().name();

        return new User(
                customer.getUsername(),
                customer.getPassword(), // Must be the hashed password stored in database
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}
