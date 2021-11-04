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
    public boolean isOn(Config config) {
        return tapoPlugProvider.getPlug(config).getDeviceInfo().getDevice_on();
    }

    @Override
    public boolean isOff(Config config) {
        return !isOn(config);
    }

    @Override
    @SneakyThrows
    public void turnOn(Config config) {
        tapoPlugProvider.getPlug(config).turnOn();
    }

    @Override
    @SneakyThrows
    public void turnOff(Config config) {
        tapoPlugProvider.getPlug(config).turnOff();
    }

    private String resolveCommand(Config config, String command) {
        var additionalData = config.getAdditionalData();

        return String.format(
                "%s -i %s %s",
                additionalData.getSmartPlugExecutablePath(),
                additionalData.getSmartPlugIpAddress(),
                command
        );
    }


    @Component
    static class TapoPlugProvider {

        @Retryable(backoff = @Backoff(delay = 5000))
        @Cacheable(value = "tapo-p100-plug-cache", keyGenerator = "tapoCacheKeyGenerator")
        public PlugP100 getPlug(Config config) throws Exception {
            var plug = new PlugP100(config.getAdditionalData().getSmartPlugIpAddress(), config.getAdditionalData().getSmartPlugCloudUsername(), config.getAdditionalData().getSmartPlugCloudPassword());

            disableObjectMapperFailOnUnknownProperties(plug);

            plug.handshake();
            plug.login();

            return plug;
        }

        @Recover
        @CacheEvict(value = "tapo-p100-plug-cache", keyGenerator = "tapoCacheKeyGenerator")
        public PlugP100 recoverGetPlug(Throwable throwable, Config config) throws Exception {
            return getPlug(config);
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
