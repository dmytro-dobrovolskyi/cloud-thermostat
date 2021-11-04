package com.dmytrodobrovolskyi.couldthermostat.repository.impl;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FileSystemCachedConfigRepository implements ConfigRepository {
    private static final File STORE = createFileBasedStore();

    private final ObjectMapper objectMapper;

    @SneakyThrows
    private static File createFileBasedStore() {
        var store = Paths.get("config.json").toAbsolutePath().toFile();

        store.createNewFile();

        return store;
    }

    /**
     * Cache-managed.
     */
    @Override
    @SneakyThrows
    public List<Config> findAllConfigs() {
        var rawConfigs = IOUtils.toString(new FileInputStream(STORE), StandardCharsets.UTF_8);

        if (StringUtils.hasText(rawConfigs)) {
            return objectMapper.readValue(rawConfigs, new TypeReference<>() {});
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @SneakyThrows
    public Config saveConfig(Config config) {
        var configByDeviceKey = findAllConfigs()
                .stream()
                .collect(Collectors.toMap(Config::getMeasuringDeviceKey, Function.identity()));

        configByDeviceKey.put(config.getMeasuringDeviceKey(), config);

        writeConfig(configByDeviceKey.values());

        return config;
    }

    @SneakyThrows
    private void writeConfig(Collection<Config> configs) {
        IOUtils.write(objectMapper.writeValueAsString(configs), new FileOutputStream(STORE), StandardCharsets.UTF_8);
    }
}
