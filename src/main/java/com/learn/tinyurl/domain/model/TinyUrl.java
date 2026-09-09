package com.learn.tinyurl.domain.model;

import java.net.URI;
import java.time.Instant;


public record TinyUrl(URI url,
                      String shortKey,
                      Instant expiredAt) {
}
