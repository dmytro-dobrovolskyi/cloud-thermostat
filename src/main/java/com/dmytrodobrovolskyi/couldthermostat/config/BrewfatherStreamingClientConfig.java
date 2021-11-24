package com.dmytrodobrovolskyi.couldthermostat.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class BrewfatherStreamingClientConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor(@Value("${thirdparty.brewfather.stream-id}") String streamId) {
        return requestTemplate -> requestTemplate.query("id", streamId);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
