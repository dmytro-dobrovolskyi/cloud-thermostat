package com.dmytrodobrovolskyi.couldthermostat.contract;

public interface Switch {
    boolean isOn();

    boolean isOff();

    void turnOn();

    void turnOff();
}
