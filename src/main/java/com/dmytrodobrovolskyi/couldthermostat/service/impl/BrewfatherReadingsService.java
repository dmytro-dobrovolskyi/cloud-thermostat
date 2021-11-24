package com.dmytrodobrovolskyi.couldthermostat.service.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.impl.Tilt;
import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.service.ConfigService;
import com.dmytrodobrovolskyi.couldthermostat.service.ReadingsService;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.BrewfatherStreamingClient;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewfather.StreamingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrewfatherReadingsService implements ReadingsService {
    private final BrewfatherStreamingClient client;
    private final ConfigService configService;
    private final Tilt tilt;

    @Override
    public void sendReadings() {
        log.info("Sending readings to Brewfather");

        configService.getAllConfigs()
                .stream()
                .filter(Config::isEnabled)
                .map(config -> CompletableFuture.runAsync(() -> streamReadingsToBrewfather(config)))
                .map(readingsFuture -> readingsFuture.exceptionally(this::handleError))
                .forEach(CompletableFuture::join);

        log.info("Send readings operation's finished");
    }

    private void streamReadingsToBrewfather(Config config) {
        var tiltData = tilt.manufacturerData(config);

        var data = StreamingData.builder()
                .name(config.getDeviceName())
                .temp(tilt.temperature(tiltData))
                .tempUnit("F")
                .gravity(tilt.gravity(tiltData))
                .gravityUnit("G")
                .comment(Optional.ofNullable(config.getAdditionalData())
                        .map(Config.AdditionalData::getComment)
                        .orElse("")
                )
                .build();

        client.stream(data);
    }

    private Void handleError(Throwable ex) {
        log.error("Failed to send readings to Brewer's Friend", ex);

        return null;
    }
}
