package com.dmytrodobrovolskyi.couldthermostat.thirdparty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrewSessionsPage {
    private int count;

    @JsonProperty("brewsessions")
    private List<BrewSession> brewSessions;
}
