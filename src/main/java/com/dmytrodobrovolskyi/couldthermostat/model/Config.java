package com.dmytrodobrovolskyi.couldthermostat.model;

import lombok.Data;

@Data
public class Config {
  private boolean isHeating;
  private double temperature;
  private boolean turnOffIfDisaster = true;

  public boolean isCooling() {
    return !isHeating;
  }
}
