package com.dmytrodobrovolskyi.couldthermostat.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class Config {
    private String measuringDeviceKey;
    private String batchcode;
    private boolean isHeating;
    private double minTemperature;
    private double maxTemperature;
    private boolean turnOffIfDisaster = true;
    private SmartPlugType smartPlugType;
    private AdditionalData additionalData;
    private boolean enabled;

    public boolean isCooling() {
        return !isHeating;
    }
    
    @RequiredArgsConstructor
    public enum SmartPlugType {
        HS100,
        TAPO_P100
    }
    
    @Data
    public static class AdditionalData {
        private String smartPlugIpAddress;
        private String smartPlugCloudUsername;
        private String smartPlugCloudPassword;
        private String smartPlugExecutablePath;
        private String comment;
    }
}
