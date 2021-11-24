package com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewfather;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StreamingData {
    private String name;
    private double temp;
    private String tempUnit;
    private BigDecimal gravity;
    private String gravityUnit;
    private String comment;
}
