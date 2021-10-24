package com.dmytrodobrovolskyi.couldthermostat.model;

import lombok.Data;

@Data
public class Config {
    private String tiltAddress;
    private boolean isHeating;
    private double minTemperature;
    private double maxTemperature;
    private boolean turnOffIfDisaster = true;

    public boolean isCooling() {
        return !isHeating;
    }
}
