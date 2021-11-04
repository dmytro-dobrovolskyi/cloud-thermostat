package com.dmytrodobrovolskyi.couldthermostat.service;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class Thermostat {
    private final ConfigService configService;
    private final Thermometer thermometer;
    private final Map<String, Switch> switchByType;

    public void regulateTemperature() {
        configService.getAllConfigs()
                .stream()
                .filter(Config::isEnabled)
                .forEach(config -> CompletableFuture
                        .runAsync(() -> doRegulateTemperature(config))
                        .exceptionally(ex -> handleError(ex, config))
                );
    }

    private void doRegulateTemperature(Config config) {
        double currentTemperature = thermometer.temperature(config);
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        log.info("Current temperature is {}", currentTemperature);

        if (isHeatingRequired(config, currentTemperature)) {
            powerSwitch.turnOn(config);
            log.info("It's too cool but no worries it's gonna be all right. Turning on the heater!");
        } else if (isCoolingRequired(config, currentTemperature)) {
            powerSwitch.turnOn(config);
            log.info("It's too hot but no worries it's gonna be all right. Turning on the cooler!");
        } else if (shouldTurnOff(config, currentTemperature)) {
            powerSwitch.turnOff(config);
            log.info("Looks like we did the job well. Turning off the heater/cooler");
        } else {
            log.info("Looks like you're all set!");
        }
    }

    private boolean isCoolingRequired(Config config, double currentTemperature) {
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        return config.isCooling() && isTooHot(config, currentTemperature) && powerSwitch.isOff(config);
    }

    private boolean isHeatingRequired(Config config, double currentTemperature) {
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        return config.isHeating() && isTooCool(config, currentTemperature) && powerSwitch.isOff(config);
    }

    private boolean shouldTurnOff(Config config, double currentTemperature) {
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        return powerSwitch.isOn(config) && (isHeatedEnough(config, currentTemperature) || isCooledEnough(config, currentTemperature));
    }

    private boolean isCooledEnough(Config config, double currentTemperature) {
        return config.isCooling() && currentTemperature <= config.getMinTemperature();
    }

    private boolean isHeatedEnough(Config config, double currentTemperature) {
        return config.isHeating() && currentTemperature >= config.getMaxTemperature();
    }

    private boolean isTooCool(Config config, double currentTemperature) {
        return currentTemperature < config.getMinTemperature();
    }

    private boolean isTooHot(Config config, double currentTemperature) {
        return currentTemperature > config.getMaxTemperature();
    }

    private Void handleError(Throwable ex, Config config) {
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        if (config.isTurnOffIfDisaster()) {
            log.error("Turning OFF");
            powerSwitch.turnOff(config);
        } else {
            log.error("Keeping isOn={} according to the Config given: {}", powerSwitch.isOn(config), config);
        }
        return null;
    }
}
