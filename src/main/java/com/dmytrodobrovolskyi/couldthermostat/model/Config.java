package com.dmytrodobrovolskyi.couldthermostat.model;

import lombok.Data;

@Data
public class Config {
  private boolean isHeating;
  private double temperature;

  public boolean isCooling() {
    return !isHeating;
  }
}
