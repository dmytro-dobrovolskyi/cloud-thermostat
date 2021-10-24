package com.dmytrodobrovolskyi.couldthermostat.repository;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;

import java.util.Optional;

public interface ConfigRepository {
    Optional<Config> getConfig();

    Config saveConfig(Config config);
}
