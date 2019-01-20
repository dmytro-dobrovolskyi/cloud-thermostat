package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import com.dmytrodobrovolskyi.couldthermostat.exception.CouldNotReadTemperatureException;
import org.springframework.stereotype.Component;
import tinyb.BluetoothDevice;
import tinyb.BluetoothManager;

@Component
public class TiltThermometer implements Thermometer {
  private static final String TILT_ADDRESS = "F0:11:BC:F4:2D:95";
  private static final String DATA_KEY = "76";
  private static final int TEMPERATURE_VALUE_INDEX = 19;

  @Override
  public double temperature() {
    BluetoothManager.getBluetoothManager().startDiscovery();

    return BluetoothManager.getBluetoothManager().getDevices()
        .stream()
        .filter(bluetoothDevice -> bluetoothDevice.getAddress().equals(TILT_ADDRESS))
        .peek(tilt -> tilt.setTrusted(true))
        .findFirst()
        .map(BluetoothDevice::getManufacturerData)
        .map(data -> data.get(Short.valueOf(DATA_KEY)))
        .map(tiltData -> tiltData[TEMPERATURE_VALUE_INDEX])
        .orElseThrow(CouldNotReadTemperatureException::new);
  }
}

