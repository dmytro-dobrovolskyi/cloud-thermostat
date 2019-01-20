package com.dmytrodobrovolskyi.couldthermostat.contract.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.Thermometer;
import tinyb.BluetoothManager;

import java.util.stream.Collectors;


public class TiltThermometer implements Thermometer {
  private static final String TILT_ADDRESS = "F0-11-BC-F4-2D-95";

  @Override
  public double temperature() {
    BluetoothManager.getBluetoothManager().startDiscovery();

   return BluetoothManager.getBluetoothManager().getDevices()
        .stream()
        .filter(bluetoothDevice -> bluetoothDevice.getAlias().equals(TILT_ADDRESS))
        .collect(Collectors.toList())
        .get(0)
        .getManufacturerData()
        .get(Short.valueOf("76"))
        [19];
  }
}

