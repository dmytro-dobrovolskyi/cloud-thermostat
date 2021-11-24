package com.dmytrodobrovolskyi.couldthermostat.dto;

import lombok.Value;

@Value
public class SwitchDto {
    State heater;
    State cooler;
    
    public enum State {
        ON,
        OFF,
        NOT_CONNECTED;

        public static State of(boolean power) {
            return power ? ON : OFF;
        }
    }
}
