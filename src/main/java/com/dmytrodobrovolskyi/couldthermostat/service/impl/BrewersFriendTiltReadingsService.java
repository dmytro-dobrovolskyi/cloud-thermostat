package com.dmytrodobrovolskyi.couldthermostat.service.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.impl.Tilt;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.service.ConfigService;
import com.dmytrodobrovolskyi.couldthermostat.service.ReadingsService;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.BrewersFriendClient;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.FermentationData;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.BrewSession;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrewersFriendTiltReadingsService implements ReadingsService {
    private static final int LIMIT = 5;

    private final BrewersFriendClient client;
    private final ConfigService configService;
    private final Tilt tilt;

    @Override
    public void sendReadings() {
        log.info("Sending readings to Brewer's Friend");

        var configByBatchcode = configService.getAllConfigs()
                .stream()
                .filter(Config::isEnabled)
                .collect(Collectors.toMap(Config::getBatchcode, Function.identity()));

        if (!configByBatchcode.isEmpty()) {
            client.getLatestBrewSessions(LIMIT)
                    .getBrewSessions()
                    .stream()
                    .filter(BrewSession::isFermentationInProgress)
                    .map(brewSession -> CompletableFuture.supplyAsync(() -> new BrewSessionIdToTiltReadings(
                            brewSession.getId(),
                            tilt.manufacturerData(configByBatchcode.get(brewSession.getBatchcode()))))
                    )
                    .map(readingsFuture -> readingsFuture.thenAccept(this::sendReadingsToBrewersFriend))
                    .forEach(CompletableFuture::join);
        }
        log.info("Operation's finished");
    }

    private void sendReadingsToBrewersFriend(BrewSessionIdToTiltReadings readings) {
        var fermentationData = Collections.singletonList(FermentationData.builder()
                .name("Tilt")
                .temp(tilt.temperature(readings.getReadings()))
                .tempUnit("F")
                .gravity(tilt.gravity(readings.getReadings()))
                .gravityUnit("G")
                .createdAt(Instant.now())
                .build());

        client.importFermentationData(readings.getBrewSessionId(), fermentationData);
    }

    @Value
    private static class BrewSessionIdToTiltReadings {
        String brewSessionId;
        Map<Short, byte[]> readings;
    }
}
