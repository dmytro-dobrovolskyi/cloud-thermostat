package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Hydrometer;
import com.dmytrodobrovolskyi.couldthermostat.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class GravityApi {
    private final Hydrometer hydrometer;
    private final ConfigService configService;

    @GetMapping("/{deviceKey}/gravity")
    public BigDecimal getTemperature(@PathVariable String deviceKey) {
        return hydrometer.gravity(configService.getAllGroupingByDeviceKey().get(deviceKey));
    }
}
