package com.learn.tinyurl.ports.inbound;

import com.learn.tinyurl.domain.model.TinyUrl;


import java.net.URI;
import java.time.Instant;

/*
<p>TinyUrlPort - This port is helping to reach the domain service. Outside -> Inside</p>
 */
public interface TinyUrlPort {

    record CreateShortUrlCommand(URI url,
                                 String alias,
                                 Instant expiredAt){}


    TinyUrl createTinyUrl(CreateShortUrlCommand command);

    TinyUrl resolveTinyUrl(String shortKey);
}
