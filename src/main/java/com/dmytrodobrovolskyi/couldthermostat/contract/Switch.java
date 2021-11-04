package com.dmytrodobrovolskyi.couldthermostat.contract;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;

public interface Switch {
    boolean isOn(Config config);

    boolean isOff(Config config);

    void turnOn(Config config);

    void turnOff(Config config);
}
