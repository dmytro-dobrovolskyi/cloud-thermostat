package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Hs100SmartSocket implements Switch {
  private static final String EXEC_HS100_SH = "bash ~/lib/hs100/hs100.sh";


  @Override
  @SneakyThrows
  public boolean isOn() {
    Process checkIsOn = Runtime.getRuntime().exec(EXEC_HS100_SH + "check");

    String result = IOUtils.toString(checkIsOn.getInputStream(), StandardCharsets.UTF_8);

    return false;
  }

  @Override
  public void turnOn() {

  }

  @Override
  public void turnOff() {

  }
}
