package com.dmytrodobrovolskyi.couldthermostat.service;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;

    public List<Config> getAllConfigs() {
        return configRepository.findAllConfigs();
    }

    public Map<String, Config> getAllGroupingByDeviceKey() {
        return getAllConfigs()
                .stream()
                .collect(Collectors.toMap(Config::getMeasuringDeviceKey, Function.identity()));
    }

    public Optional<Config> getConfigByDeviceKey(String deviceKey) {
        return Optional.ofNullable(getAllGroupingByDeviceKey().get(deviceKey));
    }
    
    @CacheEvict(value = "tapo-p100-plug-cache", keyGenerator = "tapoCacheKeyGenerator")
    public Config saveConfig(Config config) {
        return configRepository.saveConfig(config);
    }
}
