package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.exception.CouldNotReadTemperatureException;
import lombok.SneakyThrows;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import tinyb.BluetoothAdapter;
import tinyb.BluetoothDevice;
import tinyb.BluetoothManager;
import tinyb.TransportType;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class TiltThermometer implements Thermometer {
  private static final String TILT_ADDRESS = "F0:11:BC:F4:2D:95";
  private static final String DATA_KEY = "76";
  private static final int TEMPERATURE_VALUE_INDEX = 19;

  @Override
  @SneakyThrows
  @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3500))
  public double temperature() {
    BluetoothAdapter bluetoothAdapter = BluetoothManager.getBluetoothManager()
        .getAdapters()
        .stream()
        .findFirst()
        .orElseThrow(CouldNotReadTemperatureException::new);

    bluetoothAdapter.setDiscoveryFilter(Collections.emptyList(), 0, 0, TransportType.LE);
    bluetoothAdapter.startDiscovery();

    Thread.sleep(TimeUnit.SECONDS.toMillis(5));

    byte temperature = bluetoothAdapter.getDevices()
        .stream()
        .filter(bluetoothDevice -> bluetoothDevice.getAddress().equals(TILT_ADDRESS))
        .peek(tilt -> tilt.setTrusted(true))
        .findFirst()
        .map(BluetoothDevice::getManufacturerData)
        .map(data -> data.get(Short.valueOf(DATA_KEY)))
        .map(tiltData -> tiltData[TEMPERATURE_VALUE_INDEX])
        .orElseThrow(CouldNotReadTemperatureException::new);

    new Thread(bluetoothAdapter::stopDiscovery).start();

    return temperature;
  }
}
