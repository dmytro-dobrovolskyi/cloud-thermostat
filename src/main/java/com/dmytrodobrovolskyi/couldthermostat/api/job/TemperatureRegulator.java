package com.dmytrodobrovolskyi.couldthermostat.api.job;

import com.dmytrodobrovolskyi.couldthermostat.service.Thermostat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemperatureRegulator {
  private final Thermostat thermostat;

  @Scheduled(fixedDelayString = "${application.thermostat.delayInMilliseconds}")
  public void regulateTemperature() {
    log.info("Job to regualte temperature has started");

    thermostat.regulateTemperature();

    log.info("Job to regualte temperature has finished");
  }
}
