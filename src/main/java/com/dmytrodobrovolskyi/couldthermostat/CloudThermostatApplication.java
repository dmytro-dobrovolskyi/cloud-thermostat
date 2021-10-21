package com.dmytrodobrovolskyi.couldthermostat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * To debug:
 * 
 * sudo java -Djava.library.path=/usr/lib/arm-linux-gnueabihf:/home/pi/tinyb/build/java/jni:/home/pi/tinyb/build/src -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000 -jar ~/cloud-thermostat/target/cloud-thermostat-0.1.jar
 *
 * Background: nohup java -Djava.library.path=/usr/lib/arm-linux-gnueabihf/ -jar /home/pi/cloud-thermostat/target/cloud-thermostat-0.1.jar &
 */
@SpringBootApplication
public class CloudThermostatApplication {
  public static void main(String[] args) {
    SpringApplication.run(CloudThermostatApplication.class);
  }
}
