package com.dmytrodobrovolskyi.couldthermostat;

import tinyb.BluetoothManager;

/**
 * To debug:
 * sudo java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=y -jar target/cloud-thermostat-0.1.jar
 */
public class CloudThermostatApplication {
  public static void main(String[] args) {
    BluetoothManager.getBluetoothManager();
  }
}
