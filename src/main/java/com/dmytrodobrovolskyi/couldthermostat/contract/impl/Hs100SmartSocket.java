package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Hs100SmartSocket implements Switch {
  private static final String EXEC_HS100_SH = "/home/pi/lib/hs100/hs100.sh ";


  @Override
  @SneakyThrows
  public boolean isOn() {
    Process checkIsOn = Runtime.getRuntime().exec(EXEC_HS100_SH + "check");

    return IOUtils.toString(checkIsOn.getInputStream(), StandardCharsets.UTF_8).contains("ON");
  }

  @Override
  public boolean isOff() {
    return !isOn();
  }

  @Override
  @SneakyThrows
  public void turnOn() {
    Runtime.getRuntime().exec(EXEC_HS100_SH + "on");
  }

  @Override
  @SneakyThrows
  public void turnOff() {
    Runtime.getRuntime().exec(EXEC_HS100_SH + "off");
  }
}
