package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.dto.SwitchDto;
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

    @GetMapping("/{deviceKey}/power-switch/state")
    public SwitchDto getSwitchState(@PathVariable String deviceKey) {
        var config = configService.getAllGroupingByDeviceKey().get(deviceKey);
        var coolerState = SwitchDto.State.NOT_CONNECTED;
        var heaterState = SwitchDto.State.NOT_CONNECTED;
        
        if (config.getCoolerConfig() != null) {
            var cooler = switchByType.get(config.getCoolerConfig().getType().name());
            coolerState = SwitchDto.State.of(cooler.isOn(config.getCoolerConfig()));
        }
        if (config.getHeaterConfig() != null) {
            var heater = switchByType.get(config.getHeaterConfig().getType().name());
            heaterState = SwitchDto.State.of(heater.isOn(config.getHeaterConfig()));
        }

        return new SwitchDto(heaterState, coolerState); 
    }

    @PostMapping("/{deviceKey}/cooler/off")
    public void coolerOff(@PathVariable String deviceKey) {
        var config = configService.getAllGroupingByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getCoolerConfig().getType().name());

        powerSwitch.turnOff(config.getCoolerConfig());
    }

    @PostMapping("/{deviceKey}/heater/off")
    public void heaterOff(@PathVariable String deviceKey) {
        var config = configService.getAllGroupingByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getHeaterConfig().getType().name());

        powerSwitch.turnOff(config.getHeaterConfig());
    }

    @PostMapping("/{deviceKey}/cooler/on")
    public void coolerOn(@PathVariable String deviceKey) {
        var config = configService.getAllGroupingByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getCoolerConfig().getType().name());

        powerSwitch.turnOn(config.getCoolerConfig());
    }

    @PostMapping("/{deviceKey}/heater/on")
    public void heaterOn(@PathVariable String deviceKey) {
        var config = configService.getAllGroupingByDeviceKey().get(deviceKey);
        var powerSwitch = switchByType.get(config.getHeaterConfig().getType().name());

        powerSwitch.turnOn(config.getHeaterConfig());
    }
}
