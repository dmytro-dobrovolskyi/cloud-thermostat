package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SwitchApi {
    private final Map<String, Switch> switchByType;
    private final ConfigService configService;

    @GetMapping("/{deviceKey}/power-switch/is-on/")
    public boolean isOn(@PathVariable String deviceKey) {
        var config = configService.getAllByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        return powerSwitch.isOn(config);
    }

    @PostMapping("/{deviceKey}/power-switch/off")
    public void off(@PathVariable String deviceKey) {
        var config = configService.getAllByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        powerSwitch.turnOff(config);
    }

    @PostMapping("/{deviceKey}/power-switch/on")
    public void on(@PathVariable String deviceKey) {
        var config = configService.getAllByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getSmartPlugType().name());

        powerSwitch.turnOn(config);
    }
}
