package com.learn.tinyurl.adapters.outbound.impl;

import com.learn.tinyurl.adapters.outbound.jpa.TinyUrlRepo;
import com.learn.tinyurl.adapters.outbound.persistence.TinyUrlEntity;
import com.learn.tinyurl.domain.model.TinyUrl;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@Component
public class TinyUrlRepoPortImpl {

    private final TinyUrlRepo tinyUrlRepo;

    public TinyUrlRepoPortImpl(TinyUrlRepo tinyUrlRepo) {
        this.tinyUrlRepo = tinyUrlRepo;
    }


    public void save(TinyUrl tinyUrl) {
        tinyUrlRepo.save(toEntity(tinyUrl));
    }


    public Optional<TinyUrl> findBy(String shortKey) {
        return tinyUrlRepo.findByShortKey(shortKey).map(this::toDomain);

    }

    private TinyUrlEntity toEntity(TinyUrl tinyUrl) {
        return new TinyUrlEntity(tinyUrl.url().toString(),tinyUrl.shortKey(),tinyUrl.expiredAt(),"100",1L,Instant.now(),Instant.now());
    }

    private TinyUrl toDomain(TinyUrlEntity entity) {
        return new TinyUrl(URI.create(entity.getUrl()),entity.getShortKey(), entity.getExpiredAt());
    }
}
