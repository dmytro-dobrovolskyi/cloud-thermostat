package com.dmytrodobrovolskyi.couldthermostat.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class BrewersFriendConfig {

    @Bean
    public RequestInterceptor requestInterceptor(@Value("${thirdparty.brewers-friend.api-key}") String apiKey) {
        return requestTemplate -> requestTemplate.header("X-API-KEY", apiKey);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
