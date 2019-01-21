package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SwitchApi {
  private final Switch powerSwitch;

  @GetMapping("/power-switch/is-on")
  public boolean isOn() {
    return powerSwitch.isOn();
  }

  @PostMapping("/power-switch/off")
  public void off() {
    powerSwitch.turnOff();
  }

  @PostMapping("/power-switch/on")
  public void on() {
    powerSwitch.turnOn();
  }
}
