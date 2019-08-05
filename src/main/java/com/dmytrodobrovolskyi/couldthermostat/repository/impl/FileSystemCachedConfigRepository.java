package com.dmytrodobrovolskyi.couldthermostat.repository.impl;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FileSystemCachedConfigRepository implements ConfigRepository {
  private static final File CONFIG = new File("/home/pi/cloud-thermostat/config.json");

  private final ObjectMapper objectMapper;

  /**
   * Cache-managed.
   */
  @Override
  @SneakyThrows
  @Cacheable(cacheNames = "config", keyGenerator = "configKeyGenerator")
  public Optional<Config> getConfig() {
    return Optional.of(objectMapper.readValue(new FileInputStream(CONFIG), Config.class));
  }

  @Override
  @SneakyThrows
  @CachePut(value = "config", keyGenerator = "configKeyGenerator")
  public Config saveConfig(Config config) {
    IOUtils.write(objectMapper.writeValueAsString(config), new FileOutputStream(CONFIG), StandardCharsets.UTF_8);

    return config;
  }
}
