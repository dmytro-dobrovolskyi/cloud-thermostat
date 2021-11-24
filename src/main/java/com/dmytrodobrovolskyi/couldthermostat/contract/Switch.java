package com.dmytrodobrovolskyi.couldthermostat.contract;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;

public interface Switch {
    boolean isOn(Config.SmartPlug smartPlugConfig);

    boolean isOff(Config.SmartPlug smartPlugConfig);

    void turnOn(Config.SmartPlug smartPlugConfig);

    void turnOff(Config.SmartPlug smartPlugConfig);
}
