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
    public boolean isOn(Config config) {
        var checkIsOn = Runtime.getRuntime().exec(resolveCommand(config, "check"));

        return IOUtils.toString(checkIsOn.getInputStream(), StandardCharsets.UTF_8).contains("ON");
    }

    @Override
    public boolean isOff(Config config) {
        return !isOn(config);
    }

    @Override
    @SneakyThrows
    public void turnOn(Config config) {
        Runtime.getRuntime().exec(resolveCommand(config, "on"));
    }

    @Override
    @SneakyThrows
    public void turnOff(Config config) {
        Runtime.getRuntime().exec(resolveCommand(config, "off"));
    }

    private String resolveCommand(Config config, String command) {
        var additionalData = config.getAdditionalData();

        return String.format(
                "%s -i %s %s",
                additionalData.getSmartPlugExecutablePath(),
                additionalData.getSmartPlugIpAddress(),
                command
        );
    }
}
