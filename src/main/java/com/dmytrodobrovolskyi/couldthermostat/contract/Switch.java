package com.dmytrodobrovolskyi.couldthermostat.contract;

public interface Switch {
  boolean isOn();

  void turnOn();

  void turnOff();
}
