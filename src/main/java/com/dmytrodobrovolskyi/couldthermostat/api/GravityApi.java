package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.contract.Hydrometer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class GravityApi {
    private final Hydrometer hydrometer;

    @GetMapping("/gravity")
    public BigDecimal getTemperature() {
        return hydrometer.gravity();
    }
}
