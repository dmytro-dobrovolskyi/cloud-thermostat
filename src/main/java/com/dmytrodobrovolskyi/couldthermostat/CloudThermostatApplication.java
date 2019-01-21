package com.dmytrodobrovolskyi.couldthermostat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * To debug:
 * sudo java -Djava.library.path=/usr/lib/arm-linux-gnueabihf/ -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=y -jar ~/cloud-thermostat/target/cloud-thermostat-0.1.jar
 */
@SpringBootApplication
public class CloudThermostatApplication {
  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(CloudThermostatApplication.class);
  }
}
