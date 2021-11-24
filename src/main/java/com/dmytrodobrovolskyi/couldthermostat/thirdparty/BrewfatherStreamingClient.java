package com.dmytrodobrovolskyi.couldthermostat.thirdparty;

import com.dmytrodobrovolskyi.couldthermostat.config.BrewfatherStreamingClientConfig;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewfather.StreamingData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "Brewfather", url = "http://log.brewfather.net", configuration = BrewfatherStreamingClientConfig.class)
public interface BrewfatherStreamingClient {

    @PostMapping(value = "/stream")
    void stream(StreamingData streamingRequest);
}
