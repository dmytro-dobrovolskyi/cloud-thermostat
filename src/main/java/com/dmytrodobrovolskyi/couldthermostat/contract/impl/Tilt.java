package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Hydrometer;
import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.exception.CouldNotReadGravityException;
import com.dmytrodobrovolskyi.couldthermostat.exception.CouldNotReadTemperatureException;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import tinyb.BluetoothAdapter;
import tinyb.BluetoothDevice;
import tinyb.BluetoothManager;
import tinyb.TransportType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Tilt implements Thermometer, Hydrometer {
    private static final short DATA_KEY = 76;

    private static final int TEMPERATURE_VALUE_INDEX = 19;

    private static final int GRAVITY_VALUE_INDEX = 21;
    private static final BigDecimal MINIMAL_READING = new BigDecimal("0.001");
    private static final BigDecimal MIN_GRAVITY = new BigDecimal("0.896");
    private static final Map<Byte, BigDecimal> GRAVITY_IDENTIFIER_TO_HUMAN_READABLE_GRAVITY = Stream
            .iterate(GravityIdentifierToHumanReadableGravity.firstReadings(),
                    GravityIdentifierToHumanReadableGravity::isNotLast,
                    GravityIdentifierToHumanReadableGravity::nextReadings
            )
            .collect(Collectors.toMap(
                    GravityIdentifierToHumanReadableGravity::getTiltGravityIdentifier,
                    GravityIdentifierToHumanReadableGravity::getHumanReadableGravity
            ));


    private final ConfigRepository configRepository;

    @Override
    @SneakyThrows
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 7000))
    public double temperature() {
        var manufacturerData = getManufacturerData();

        return temperature(manufacturerData);
    }

    @Override
    @SneakyThrows
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 7000))
    public BigDecimal gravity() {
        var manufacturerData = getManufacturerData();

        return gravity(manufacturerData);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 7000))
    public Map<Short, byte[]> getManufacturerData() {
        BluetoothAdapter bluetoothAdapter = BluetoothManager.getBluetoothManager()
                .getAdapters()
                .stream()
                .findFirst()
                .orElseThrow(CouldNotReadTemperatureException::new);

        bluetoothAdapter.setDiscoveryFilter(Collections.emptyList(), 0, 0, TransportType.LE);
        bluetoothAdapter.startDiscovery();


        var manufacturerData = bluetoothAdapter.getDevices()
                .stream()
                .filter(bluetoothDevice -> bluetoothDevice.getAddress().equals(
                        configRepository.getConfig()
                                .map(Config::getTiltAddress)
                                .orElseThrow(IllegalStateException::new))
                )
                .peek(tilt -> tilt.setTrusted(true))
                .findFirst()
                .map(BluetoothDevice::getManufacturerData)
                .orElseThrow(CouldNotReadTemperatureException::new);

        CompletableFuture.runAsync(bluetoothAdapter::stopDiscovery);

        return manufacturerData;
    }

    public double temperature(Map<Short, byte[]> manufacturerData) {
        return Optional.ofNullable(manufacturerData)
                .map(data -> data.get(DATA_KEY))
                .map(tiltData -> tiltData[TEMPERATURE_VALUE_INDEX])
                .orElseThrow(CouldNotReadTemperatureException::new);
    }

    public BigDecimal gravity(Map<Short, byte[]> manufacturerData) {
        return Optional.ofNullable(manufacturerData.get(DATA_KEY))
                .map(tiltData -> tiltData[GRAVITY_VALUE_INDEX])
                .map(GRAVITY_IDENTIFIER_TO_HUMAN_READABLE_GRAVITY::get)
                .orElseThrow(CouldNotReadGravityException::new);
    }

    @Value
    private static class GravityIdentifierToHumanReadableGravity {
        int tiltGravityIdentifier;
        BigDecimal humanReadableGravity;

        static GravityIdentifierToHumanReadableGravity firstReadings() {
            return new GravityIdentifierToHumanReadableGravity(Byte.MIN_VALUE, MIN_GRAVITY);
        }

        GravityIdentifierToHumanReadableGravity nextReadings() {
            return new GravityIdentifierToHumanReadableGravity(
                    tiltGravityIdentifier + 1,
                    humanReadableGravity.add(MINIMAL_READING)
            );
        }

        public boolean isNotLast() {
            return this.tiltGravityIdentifier <= Byte.MAX_VALUE;
        }

        public byte getTiltGravityIdentifier() {
            return (byte) tiltGravityIdentifier;
        }
    }
}
