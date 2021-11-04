package com.dmytrodobrovolskyi.couldthermostat.api;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConfigApi {
    private final ConfigService configService;

    @GetMapping("/config")
    public List<Config> getConfig() {
        return configService.getAllConfigs();
    }

    @PostMapping("/config")
    public ResponseEntity<Config> saveConfig(@RequestBody Config config, UriComponentsBuilder uriComponentsBuilder) {
        Config savedConfig = configService.saveConfig(config);

        UriComponents uriComponents = uriComponentsBuilder.path("api/v1/config").build();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());

        return new ResponseEntity<>(savedConfig, headers, HttpStatus.CREATED);
    }
}
