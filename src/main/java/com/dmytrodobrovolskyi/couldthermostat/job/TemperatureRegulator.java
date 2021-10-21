package com.dmytrodobrovolskyi.couldthermostat.job;

import com.dmytrodobrovolskyi.couldthermostat.contract.Hydrometer;
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
  private final Hydrometer hydrometer;

  @Scheduled(fixedDelayString = "${application.thermostat.delayInMilliseconds}")
  public void regulateTemperature() {
    log.info("Job to regulate the temperature has started");

    thermostat.regulateTemperature();

    log.info("Job to regulate the temperature has finished");
  }
}
