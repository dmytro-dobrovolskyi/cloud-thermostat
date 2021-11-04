package com.dmytrodobrovolskyi.couldthermostat.contract;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;

public interface Thermometer {
    double temperature(Config config);
}
