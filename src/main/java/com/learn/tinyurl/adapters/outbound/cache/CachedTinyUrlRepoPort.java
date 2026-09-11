package com.learn.tinyurl.adapters.outbound.cache;

import com.learn.tinyurl.adapters.outbound.impl.TinyUrlRepoPortImpl;
import com.learn.tinyurl.domain.model.TinyUrl;
import com.learn.tinyurl.ports.outbound.TinyUrlRepoPort;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Primary
public class CachedTinyUrlRepoPort implements TinyUrlRepoPort {

    private static final String CACHE_PREFIX = "tinyurl:";
    private static final Duration TTL = Duration.ofHours(24);

    private final TinyUrlRepoPortImpl delegate;
    private final RedisTemplate<String, TinyUrl> redisTemplate;

    public CachedTinyUrlRepoPort(TinyUrlRepoPortImpl delegate,
                                 RedisTemplate<String, TinyUrl> redisTemplate) {
        this.delegate = delegate;
        this.redisTemplate = redisTemplate;
    }


    public void save(TinyUrl tinyUrl) {
        delegate.save(tinyUrl);
    }


    public Optional<TinyUrl> findBy(String shortCode) {
        String key = CACHE_PREFIX + shortCode;

        TinyUrl cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<TinyUrl> fromDb = delegate.findBy(shortCode);
        fromDb.ifPresent(tinyUrl -> redisTemplate.opsForValue().set(key, tinyUrl, TTL));
        return fromDb;
    }

}