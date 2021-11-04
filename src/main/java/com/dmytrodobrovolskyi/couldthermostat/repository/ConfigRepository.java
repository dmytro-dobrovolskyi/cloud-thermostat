package com.dmytrodobrovolskyi.couldthermostat.repository;

import com.dmytrodobrovolskyi.couldthermostat.model.Config;

import java.util.List;

public interface ConfigRepository {
    List<Config> findAllConfigs();

    Config saveConfig(Config config);
}
