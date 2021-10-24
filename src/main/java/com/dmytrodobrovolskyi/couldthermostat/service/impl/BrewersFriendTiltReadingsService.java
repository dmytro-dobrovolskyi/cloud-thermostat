package com.dmytrodobrovolskyi.couldthermostat.service.impl;

import com.dmytrodobrovolskyi.couldthermostat.contract.impl.Tilt;
import com.dmytrodobrovolskyi.couldthermostat.service.ReadingsService;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.BrewersFriendClient;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.FermentationData;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.BrewSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrewersFriendTiltReadingsService implements ReadingsService {
    private final BrewersFriendClient client;
    private final Tilt tilt;

    @Override
    public void sendReadings() {
        log.info("Sending readings to Brewer's Friend");
        
        client.getLatestBrewSessions(3)
                .getBrewSessions()
                .stream()
                .filter(BrewSession::isFermentationInProgress)
                .findFirst()
                .map(BrewSession::getId)
                .ifPresent(brewSessionId -> client.importFermentationData(
                        brewSessionId,
                        toTiltFermentationData()
                ));
        
        log.info("Operation's finished");
    }

    private List<FermentationData> toTiltFermentationData() {
        var data = tilt.getManufacturerData();

        return Collections.singletonList(FermentationData.builder()
                .name("Tilt")
                .temperature(tilt.temperature(data))
                .tempUnit("F")
                .gravity(tilt.gravity(data))
                .gravityUnit("G")
                .createdAt(Instant.now())
                .build()
        );
    }
}
