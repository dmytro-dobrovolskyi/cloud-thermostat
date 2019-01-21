package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("cloud-thermostat/api/temperature")
@RequiredArgsConstructor
public class TemperatureApi {
  private final Thermometer thermometer;

  @GetMapping
  public double getTemperature() {
    return thermometer.temperature();
  }
}
