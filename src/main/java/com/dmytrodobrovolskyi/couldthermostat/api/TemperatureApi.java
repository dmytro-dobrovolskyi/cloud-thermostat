package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TemperatureApi {
    private final Thermometer thermometer;
    private final ConfigService configService;

    @GetMapping("/{deviceKey}/temperature")
    public double getTemperature(@PathVariable String deviceKey) {
        return thermometer.temperature(configService.getAllByDeviceKey().get(deviceKey));
    }
}
