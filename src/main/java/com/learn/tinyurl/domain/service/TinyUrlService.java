package com.learn.tinyurl.domain.service;

import com.learn.tinyurl.domain.model.TinyUrl;
import com.learn.tinyurl.ports.inbound.TinyUrlPort;
import com.learn.tinyurl.ports.outbound.TinyUrlRepoPort;
import com.learn.tinyurl.util.DomainService;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * Domain Service for Tiny URL
 *
 */
@DomainService
public class TinyUrlService implements TinyUrlPort {

    private final TinyUrlRepoPort tinyUrlRepoPort;

    public TinyUrlService(TinyUrlRepoPort tinyUrlRepoPort) {
        this.tinyUrlRepoPort = tinyUrlRepoPort;
    }

    @Override
    public TinyUrl createTinyUrl(CreateShortUrlCommand command) {

        String shortKey = StringUtils.isBlank(command.alias()) ? generateShortKey() : command.alias();

        TinyUrl tinyUrl  = new TinyUrl(command.url(),
                shortKey, command.expiredAt());

        tinyUrlRepoPort.save(tinyUrl);

        return tinyUrl;
    }

    //AI
    private String generateShortKey() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public TinyUrl resolveTinyUrl(String shortKey) {

        Optional<TinyUrl> tinyUrl = tinyUrlRepoPort.findBy(shortKey);

        return tinyUrl.orElse(null);
    }
}
