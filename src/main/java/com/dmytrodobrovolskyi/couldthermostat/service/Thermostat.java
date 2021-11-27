package com.dmytrodobrovolskyi.couldthermostat.service;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
                .map(config -> CompletableFuture
                        .runAsync(() -> doRegulateTemperature(config))
                        .exceptionally(ex -> handleError(ex, config))
                )
                .forEach(CompletableFuture::join);
    }

    private void doRegulateTemperature(Config config) {
        double currentTemperature = thermometer.temperature(config);
        var cooler = findSwitchFor(config.getCoolerConfig());
        var heater = findSwitchFor(config.getHeaterConfig());

        log.info("Current temperature is {}", currentTemperature);

        if (cooler.isPresent()) {
            if (cooler.get().isOff(config.getCoolerConfig()) && isTooHot(config, currentTemperature)) {
                cooler.get().turnOn(config.getCoolerConfig());

                log.info("It's too hot but no worries it's gonna be all right. Turning on the cooler!");
            } else if (isCooledEnough(config, currentTemperature)) {
                cooler.get().turnOff(config.getCoolerConfig());

                log.info("Looks like we did the job well. Turning off the cooler");
            } else {
                log.info("Continuing with the current cooling mode");
            }
        }
        if (heater.isPresent() && syncDeviceIsCoolEnough(config)) {
            if (heater.get().isOff(config.getHeaterConfig()) && isTooCool(config, currentTemperature)) {
                heater.get().turnOn(config.getHeaterConfig());

                log.info("It's too cool but no worries it's gonna be all right. Turning on the heater!");
            } else if (isHeatedEnough(config, currentTemperature)) {
                heater.get().turnOff(config.getHeaterConfig());

                log.info("Looks like we did the job well. Turning off the heater");
            } else {
                log.info("Continuing with the current heating mode");
            }
        }
    }

    private boolean syncDeviceIsCoolEnough(Config config) {
        return Optional.ofNullable(config.getAdditionalData())
                .flatMap(Config.AdditionalData::getSynchronizeWith)
                .flatMap(configService::getConfigByDeviceKey)
                .map(syncDeviceConfig -> thermometer.temperature(syncDeviceConfig) < syncDeviceConfig.getMaxTemperature())
                .orElse(true);
    }

    private Optional<Switch> findSwitchFor(Config.SmartPlug smartPlug) {
        return Optional.ofNullable(smartPlug)
                .map(Config.SmartPlug::getType)
                .map(Enum::name)
                .map(switchByType::get);
    }

    private boolean isCooledEnough(Config config, double currentTemperature) {
        return currentTemperature <= config.getMinTemperature();
    }

    private boolean isHeatedEnough(Config config, double currentTemperature) {
        return currentTemperature >= config.getMaxTemperature();
    }

    private boolean isTooCool(Config config, double currentTemperature) {
        return currentTemperature < config.getMinTemperature();
    }

    private boolean isTooHot(Config config, double currentTemperature) {
        return currentTemperature > config.getMaxTemperature();
    }

    private Void handleError(Throwable ex, Config config) {
        log.error("Failed to regulate temperature", ex);

        var smartPlugConfigs = Stream.of(config.getCoolerConfig(), config.getHeaterConfig());
        if (config.isTurnOffIfDisaster()) {
            log.error("Turning OFF");

            smartPlugConfigs.forEach(smartPlugConfig -> findSwitchFor(smartPlugConfig)
                    .ifPresent(powerSwitch -> powerSwitch.turnOff(smartPlugConfig))
            );
        } else {
            smartPlugConfigs.forEach(smartPlugConfig -> findSwitchFor(smartPlugConfig)
                    .ifPresent(powerSwitch -> log.error(
                            "Keeping isOn={} according to the Config given: {}",
                            powerSwitch.isOn(smartPlugConfig),
                            config)
                    )
            );
        }
        return null;
    }
}
