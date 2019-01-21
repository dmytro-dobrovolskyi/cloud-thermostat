package com.dmytrodobrovolskyi.couldthermostat.repository.impl;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;
import com.dmytrodobrovolskyi.couldthermostat.repository.ConfigRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryConfigRepository implements ConfigRepository {

  /**
   * Cache-managed.
   */
  @Override
  @Cacheable(cacheNames = "config", keyGenerator = "configKeyGenerator")
  public Optional<Config> getConfig() {
    return Optional.empty();
  }

  @Override
  @CachePut(value = "config", keyGenerator = "configKeyGenerator")
  public Config saveConfig(Config config) {
    return config;
  }
}
