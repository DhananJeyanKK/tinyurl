package com.learn.tinyurl.ports.outbound;

import com.learn.tinyurl.domain.model.TinyUrl;

import java.util.Optional;

/*
This port helps to reach outside from domain service
 */
public interface TinyUrlRepoPort {

    void save(TinyUrl tinyUrl);

    Optional<TinyUrl> findBy(String shortKey);
}
