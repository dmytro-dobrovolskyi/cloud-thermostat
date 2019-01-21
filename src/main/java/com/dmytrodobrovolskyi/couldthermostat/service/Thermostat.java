package com.dmytrodobrovolskyi.couldthermostat.service;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.exception.NotFoundException;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Thermostat {
  private final ConfigRepository configRepository;
  private final Thermometer thermometer;
  private final Switch powerSwitch;

  public void regulateTemperature() {
    Config config = configRepository.getConfig().orElseThrow(() -> new NotFoundException("No config found to Regulate Temperature"));
    double currentTemperature = thermometer.temperature();

    log.info("Current temperature is {}", currentTemperature);

    if (isTooCool(config, currentTemperature) && powerSwitch.isOff()) {
      powerSwitch.turnOn();
      log.info("It's too cool but no worries it's gonna be all right");
    } else if (isTooHot(config, currentTemperature) && powerSwitch.isOff()) {
      powerSwitch.turnOn();
      log.info("It's too hot but no worries it's gonna be all right");
    } else {
      log.info("Looks like you're all set!");
    }
  }

  private boolean isTooCool(Config config, double currentTemperature) {
    return config.isHeating() && currentTemperature < config.getTemperature();
  }

  private boolean isTooHot(Config config, double currentTemperature) {
    return config.isCooling() && currentTemperature > config.getTemperature();
  }
}
