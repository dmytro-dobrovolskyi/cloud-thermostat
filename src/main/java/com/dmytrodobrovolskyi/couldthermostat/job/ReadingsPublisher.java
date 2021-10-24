package com.dmytrodobrovolskyi.couldthermostat.job;

import com.dmytrodobrovolskyi.couldthermostat.service.ReadingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReadingsPublisher {
    private final ReadingsService readingsService;

    @Scheduled(fixedDelayString = "${application.readings-publisher.delayInMilliseconds}")
    public void regulateTemperature() {
        log.info("Job to send readings has started");

        readingsService.sendReadings();

        log.info("Job to send readings has finished");
    }
}
