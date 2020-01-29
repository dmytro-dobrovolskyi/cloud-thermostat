package com.dmytrodobrovolskyi.couldthermostat.service;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.exception.NotFoundException;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class Thermostat {
  private final ConfigRepository configRepository;
  private final Thermometer thermometer;
  private final Switch powerSwitch;

  public void regulateTemperature() {
    try {
      doRegulateTemperature();
    } catch (Throwable ex) {
      log.error("Disastrous situation!");
      Optional<Config> config = configRepository.getConfig();

      if (!config.isPresent() || config.get().isTurnOffIfDisaster()) {
        log.error("Turning OFF");
        powerSwitch.turnOff();
      } else {
        log.error("Keeping isOn={} according to the Config given: {}", powerSwitch.isOn(), config.get());
      }
    }
  }

  private void doRegulateTemperature() {
    Config config = configRepository.getConfig().orElseThrow(() -> new NotFoundException("No config found to Regulate Temperature"));
    double currentTemperature = thermometer.temperature();

    log.info("Current temperature is {}", currentTemperature);

    if (isHeatingRequired(config, currentTemperature)) {
      powerSwitch.turnOn();
      log.info("It's too cool but no worries it's gonna be all right. Turning on the heater!");
    } else if (isCoolingRequired(config, currentTemperature)) {
      powerSwitch.turnOn();
      log.info("It's too hot but no worries it's gonna be all right. Turning on the cooler!");
    } else if (shouldTurnOff(config, currentTemperature)) {
      powerSwitch.turnOff();
      log.info("Looks like we did the job well. Turning off the heater/cooler");
    } else {
      log.info("Looks like you're all set!");
    }
  }

  private boolean isCoolingRequired(Config config, double currentTemperature) {
    return config.isCooling() && isTooHot(config, currentTemperature) && powerSwitch.isOff();
  }

  private boolean isHeatingRequired(Config config, double currentTemperature) {
    return config.isHeating() && isTooCool(config, currentTemperature) && powerSwitch.isOff();
  }

  private boolean shouldTurnOff(Config config, double currentTemperature) {
    return powerSwitch.isOn() && (isHeatedEnough(config, currentTemperature) || isCooledEnough(config, currentTemperature));
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
}
