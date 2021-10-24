package com.dmytrodobrovolskyi.couldthermostat.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableRetry
@EnableCaching
@EnableScheduling
@EnableFeignClients(basePackages = "com.dmytrodobrovolskyi.couldthermostat.thirdparty")
public class CloudThermostatApplicationConfig {

}
