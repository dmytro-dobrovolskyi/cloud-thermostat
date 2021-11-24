package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component("HS100")
public class Hs100SmartSocket implements Switch {

    @Override
    @SneakyThrows
    public boolean isOn(Config.SmartPlug smartPlugConfig) {
        var checkIsOn = Runtime.getRuntime().exec(resolveCommand(smartPlugConfig, "check"));

        return IOUtils.toString(checkIsOn.getInputStream(), StandardCharsets.UTF_8).contains("ON");
    }

    @Override
    public boolean isOff(Config.SmartPlug smartPlugConfig) {
        return !isOn(smartPlugConfig);
    }

    @Override
    @SneakyThrows
    public void turnOn(Config.SmartPlug smartPlugConfig) {
        Runtime.getRuntime().exec(resolveCommand(smartPlugConfig, "on"));
    }

    @Override
    @SneakyThrows
    public void turnOff(Config.SmartPlug smartPlugConfig) {
        Runtime.getRuntime().exec(resolveCommand(smartPlugConfig, "off"));
    }

    private String resolveCommand(Config.SmartPlug smartPlugConfig, String command) {
        return String.format(
                "%s -i %s %s",
                smartPlugConfig.getExecutablePath(),
                smartPlugConfig.getIpAddress(),
                command
        );
    }
}
