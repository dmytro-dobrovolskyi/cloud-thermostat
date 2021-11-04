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
        var configByBatchcode = configService.getAllConfigs()
                .stream()
                .filter(Config::isEnabled)
                .collect(Collectors.toMap(Config::getBatchcode, Function.identity()));

        if (!configByBatchcode.isEmpty()) {
            log.info("Sending readings to Brewer's Friend for batches: {}", configByBatchcode.keySet());

            client.getLatestBrewSessions(LIMIT)
                    .getBrewSessions()
                    .stream()
                    .filter(BrewSession::isFermentationInProgress)
                    .filter(brewSession -> configByBatchcode.get(brewSession.getBatchcode()) != null)
                    .map(brewSession -> CompletableFuture.supplyAsync(() -> new BrewSessionIdToTiltReadings(
                            brewSession.getId(),
                            tilt.manufacturerData(configByBatchcode.get(brewSession.getBatchcode()))))
                    )
                    .map(readingsFuture -> readingsFuture.thenAccept(this::sendReadingsToBrewersFriend))
                    .map(readingsFuture -> readingsFuture.exceptionally(this::handleError))
                    .forEach(CompletableFuture::join);

            log.info("Send readings operation's finished");
        }
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

    private Void handleError(Throwable ex) {
        log.error("Failed to send readings to Brewer's Friend", ex);

        return null;
    }

    @Value
    private static class BrewSessionIdToTiltReadings {

        String brewSessionId;
        Map<Short, byte[]> readings;
    }
}
