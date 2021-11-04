package com.dmytrodobrovolskyi.couldthermostat.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public KeyGenerator tapoCacheKeyGenerator() {
        return (target, method, params) -> "tapo-p100-plug-cache-key";
    }
}
