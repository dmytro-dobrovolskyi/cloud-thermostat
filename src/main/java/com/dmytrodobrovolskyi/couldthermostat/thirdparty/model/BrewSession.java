package com.dmytrodobrovolskyi.couldthermostat.thirdparty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrewSession {
    private String id;
    private Phase phase;
    private String recipeTitle;

    public boolean isFermentationInProgress() {
        return phase == Phase.PRIMARY_FERMENTATION
                || phase == Phase.SECONDARY_FERMENTATION
                || phase == Phase.CONDITIONING;
    }

    public enum Phase {

        @JsonProperty("Planning")
        PLANNING,

        @JsonProperty("Brewing")
        BREWING,

        @JsonProperty("Primary Fermentation")
        PRIMARY_FERMENTATION,

        @JsonProperty("Secondary Fermentation")
        SECONDARY_FERMENTATION,

        @JsonProperty("Conditioning")
        CONDITIONING,

        @JsonProperty("Ready To Drink")
        READY_TO_DRINK,

        @JsonProperty("All Gone")
        ALL_GONE;
    }
}
