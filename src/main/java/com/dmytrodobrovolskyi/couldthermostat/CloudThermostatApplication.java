package com.dmytrodobrovolskyi.couldthermostat;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import java.io.IOException;

public class CloudThermostatApplication {
  public static void main(String[] args) throws InterruptedException, BluetoothStateException {

    final Object inquiryCompletedEvent = new Object();
    DiscoveryListener listener = new DiscoveryListener() {

      public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
        try {
          System.out.println("     name " + btDevice.getFriendlyName(false));
        } catch (IOException cantGetDeviceName) {
          throw new RuntimeException(cantGetDeviceName);
        }
      }

      public void inquiryCompleted(int discType) {
        System.out.println("Device Inquiry completed!");
        synchronized (inquiryCompletedEvent) {
          inquiryCompletedEvent.notifyAll();
        }
      }

      public void serviceSearchCompleted(int transID, int respCode) {
      }

      public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
      }
    };

    synchronized (inquiryCompletedEvent) {
      boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
      if (started) {
        System.out.println("wait for device inquiry to complete...");
        inquiryCompletedEvent.wait();
      }
    }
  }
}
