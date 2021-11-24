package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.adeptues.p100.PlugP100;
import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

@Component("TAPO_P100")
@RequiredArgsConstructor
public class TapoP100SmartSocket implements Switch {
    private final TapoPlugProvider tapoPlugProvider;

    @Override
    @SneakyThrows
    public boolean isOn(Config.SmartPlug smartPlugConfig) {
        return tapoPlugProvider.getPlug(smartPlugConfig).getDeviceInfo().getDevice_on();
    }

    @Override
    public boolean isOff(Config.SmartPlug smartPlugConfig) {
        return !isOn(smartPlugConfig);
    }

    @Override
    @SneakyThrows
    public void turnOn(Config.SmartPlug smartPlugConfig) {
        tapoPlugProvider.getPlug(smartPlugConfig).turnOn();
    }

    @Override
    @SneakyThrows
    public void turnOff(Config.SmartPlug smartPlugConfig) {
        tapoPlugProvider.getPlug(smartPlugConfig).turnOff();
    }

    @Component
    static class TapoPlugProvider {

        @Retryable(backoff = @Backoff(delay = 5000))
        @Cacheable(value = "tapo-p100-plug-cache", key = "#smartPlugConfig.ipAddress")
        public PlugP100 getPlug(Config.SmartPlug smartPlugConfig) throws Exception {
            var plug = new PlugP100(smartPlugConfig.getIpAddress(), smartPlugConfig.getCloudUsername(), smartPlugConfig.getCloudPassword());

            disableObjectMapperFailOnUnknownProperties(plug);

            plug.handshake();
            plug.login();

            return plug;
        }

        @Recover
        @CacheEvict(value = "tapo-p100-plug-cache", key = "#smartPlugConfig.ipAddress")
        public PlugP100 recoverGetPlug(Throwable throwable, Config.SmartPlug smartPlugConfig) throws Exception {
            return getPlug(smartPlugConfig);
        }

        /**
         * A dirty fix due to library bug.
         */
        private void disableObjectMapperFailOnUnknownProperties(PlugP100 plug) {
            var objectMapperField = ReflectionUtils.findField(PlugP100.class, "objectMapper", ObjectMapper.class);
            Objects.requireNonNull(objectMapperField, "PlugP100 library version inconsistency. Please ensure the correct version");

            objectMapperField.setAccessible(true);
            var objectMapper = (ObjectMapper) ReflectionUtils.getField(objectMapperField, plug);

            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
    }
}
