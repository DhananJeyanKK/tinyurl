package com.learn.tinyurl.util;

import org.springframework.stereotype.Component;// Infrastructure/Common package

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component// Tells Spring to auto-scan this
public @interface DomainService {
}