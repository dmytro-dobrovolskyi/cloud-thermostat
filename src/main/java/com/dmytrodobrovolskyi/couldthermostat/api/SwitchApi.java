package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("cloud-thermostat/api/power-switch")
@RequiredArgsConstructor
public class SwitchApi {
  private final Switch powerSwitch;

  @GetMapping("/is-on")
  public boolean isOn() {
    return powerSwitch.isOn();
  }

  @PostMapping("/off")
  public void off() {
    powerSwitch.turnOff();
  }

  @PostMapping("/on")
  public void on() {
    powerSwitch.turnOn();
  }
}
