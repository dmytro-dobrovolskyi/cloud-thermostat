package com.dmytrodobrovolskyi.couldthermostat.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Data
@Builder
public class Config {
    private String deviceName;
    private String measuringDeviceKey;
    private String batchcode;
    private double minTemperature;
    private double maxTemperature;

    @Builder.Default
    private boolean turnOffIfDisaster = true;

    private AdditionalData additionalData;

    @Builder.Default
    private boolean enabled = true;

    private SmartPlug heaterConfig;
    private SmartPlug coolerConfig;

    @RequiredArgsConstructor
    public enum SmartPlugType {
        HS100,
        TAPO_P100
    }

    @Data
    @Builder
    public static class AdditionalData {
        private String comment;

        /**
         * A measuringDeviceKey that this config's temperature should be synchronized with.
         */
        private String synchronizeWith;

        public Optional<String> getSynchronizeWith() {
            return Optional.ofNullable(synchronizeWith);
        }
    }

    @Data
    @Builder
    public static class SmartPlug {
        private SmartPlugType type;
        private String ipAddress;
        private String cloudUsername;
        private String cloudPassword;
        private String executablePath;
    }
}
