package com.learn.tinyurl.util;

import com.learn.tinyurl.domain.model.TinyUrl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, TinyUrl> tinyUrlRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TinyUrl> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<TinyUrl>(TinyUrl.class));
        template.afterPropertiesSet();
        return template;
    }
}
