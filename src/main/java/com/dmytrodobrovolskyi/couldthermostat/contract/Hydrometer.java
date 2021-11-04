package com.dmytrodobrovolskyi.couldthermostat.contract;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;

import java.math.BigDecimal;

public interface Hydrometer {
    BigDecimal gravity(Config config);
}
