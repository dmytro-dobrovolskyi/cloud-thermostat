package com.dmytrodobrovolskyi.couldthermostat.service;

import com.dmytrodobrovolskyi.couldthermostat.contract.Switch;
import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThermostatTest {

    @Mock
    private ConfigService configService;

    @Mock
    private Thermometer thermometer;

    @Mock
    private Switch cooler;

    @Mock
    private Switch heater;

    private Thermostat thermostat;

    @BeforeEach
    void setUp() {
        thermostat = new Thermostat(configService, thermometer, Map.of(
                Config.SmartPlugType.HS100.name(), cooler,
                Config.SmartPlugType.TAPO_P100.name(), heater
        ));


    }

    @Test
    void regulateTemperatureShouldCool() {
        when(cooler.isOff(any())).thenReturn(true);
        doAnswer(invocationOnMock -> when(cooler.isOff(invocationOnMock.getArgument(0, Config.SmartPlug.class))).thenReturn(false))
                .when(cooler)
                .turnOn(any());

        var blackTiltConfig = Config.builder()
                .measuringDeviceKey("Black-Tilt-Id")
                .minTemperature(77)
                .maxTemperature(79)
                .coolerConfig(Config.SmartPlug.builder()
                        .executablePath("/black-tilt-smart-plug")
                        .type(Config.SmartPlugType.HS100)
                        .build()
                )
                .heaterConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.TAPO_P100)
                        .build()
                )
                .build();
        var orangeTiltConfig = Config.builder()
                .measuringDeviceKey("Orange-Tilt-Id")
                .minTemperature(69)
                .maxTemperature(71)
                .coolerConfig(Config.SmartPlug.builder()
                        .executablePath("/orange-tilt-smart-plug")
                        .type(Config.SmartPlugType.HS100)
                        .build()
                )
                .heaterConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.TAPO_P100)
                        .build()
                )
                .build();

        when(configService.getAllConfigs()).thenReturn(List.of(blackTiltConfig, orangeTiltConfig));
        when(thermometer.temperature(blackTiltConfig))
                .thenReturn(79d)
                .thenReturn(80d);
        when(thermometer.temperature(orangeTiltConfig)).thenReturn(72d);

        thermostat.regulateTemperature();

        verify(cooler).turnOn(orangeTiltConfig.getCoolerConfig());
        verify(cooler).turnOn(any());

        thermostat.regulateTemperature();

        verify(cooler).turnOn(blackTiltConfig.getCoolerConfig());
        verify(cooler, times(2)).turnOn(any());

        thermostat.regulateTemperature();
        
        verify(cooler, times(2)).turnOn(any());
        verify(cooler, never()).turnOff(any());
    }

    @Test
    void regulateTemperatureShouldHeat() {
        when(heater.isOff(any())).thenReturn(true);
        doAnswer(invocationOnMock -> when(heater.isOff(invocationOnMock.getArgument(0, Config.SmartPlug.class))).thenReturn(false))
                .when(heater)
                .turnOn(any());

        var blackTiltConfig = Config.builder()
                .measuringDeviceKey("Black-Tilt-Id")
                .minTemperature(55)
                .maxTemperature(65)
                .coolerConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.HS100)
                        .build()
                )
                .heaterConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.TAPO_P100)
                        .cloudUsername("Black")
                        .build()
                )
                .build();
        var orangeTiltConfig = Config.builder()
                .measuringDeviceKey("Orange-Tilt-Id")
                .minTemperature(74)
                .maxTemperature(74)
                .coolerConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.HS100)
                        .build()
                )
                .heaterConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.TAPO_P100)
                        .cloudUsername("Orange")
                        .build()
                )
                .build();

        when(configService.getAllConfigs()).thenReturn(List.of(blackTiltConfig, orangeTiltConfig));
        when(thermometer.temperature(blackTiltConfig))
                .thenReturn(55d)
                .thenReturn(54d);
        when(thermometer.temperature(orangeTiltConfig)).thenReturn(40d);

        thermostat.regulateTemperature();

        verify(heater).turnOn(orangeTiltConfig.getHeaterConfig());
        verify(heater).turnOn(any());

        thermostat.regulateTemperature();

        verify(heater).turnOn(blackTiltConfig.getHeaterConfig());
        verify(heater, times(2)).turnOn(any());

        thermostat.regulateTemperature();

        verify(heater, times(2)).turnOn(any());
        verify(heater, never()).turnOff(any());
    }

    @Test
    void heatingShouldSyncWhenConfigured() {
        when(heater.isOff(any())).thenReturn(true);

        var blackTiltConfig = Config.builder()
                .measuringDeviceKey("Black-Tilt-Id")
                .minTemperature(67)
                .maxTemperature(69)
                .coolerConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.HS100)
                        .build()
                )
                .heaterConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.TAPO_P100)
                        .cloudUsername("Black")
                        .build()
                )
                .additionalData(Config.AdditionalData.builder()
                        .synchronizeWith("Orange-Tilt-Id")
                        .build()
                )
                .build();
        var orangeTiltConfig = Config.builder()
                .measuringDeviceKey("Orange-Tilt-Id")
                .minTemperature(67)
                .maxTemperature(70)
                .coolerConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.HS100)
                        .build()
                )
                .heaterConfig(Config.SmartPlug.builder()
                        .type(Config.SmartPlugType.TAPO_P100)
                        .cloudUsername("Orange")
                        .build()
                )
                .additionalData(Config.AdditionalData.builder()
                        .synchronizeWith("Black-Tilt-Id")
                        .build()
                )
                .build();

        when(configService.getAllConfigs()).thenReturn(List.of(blackTiltConfig, orangeTiltConfig));
        when(configService.getConfigByDeviceKey(blackTiltConfig.getMeasuringDeviceKey())).thenReturn(Optional.of(blackTiltConfig));
        when(configService.getConfigByDeviceKey(orangeTiltConfig.getMeasuringDeviceKey())).thenReturn(Optional.of(orangeTiltConfig));
        
        when(thermometer.temperature(blackTiltConfig)).thenReturn(69d);
        when(thermometer.temperature(orangeTiltConfig)).thenReturn(66d);

        thermostat.regulateTemperature();

        verify(heater, never()).turnOn(orangeTiltConfig.getHeaterConfig());
        verify(heater, never()).turnOn(any());
        
        verify(thermometer, times(2)).temperature(blackTiltConfig);
    }
}