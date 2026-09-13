package com.learn.tinyurl.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEvent {


    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent){
        System.out.println("Successfully logged in");
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent){
        System.out.println("Failed login");
    }


}
