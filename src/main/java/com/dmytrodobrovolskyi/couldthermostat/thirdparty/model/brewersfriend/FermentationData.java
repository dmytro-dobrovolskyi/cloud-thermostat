package com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewersfriend;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FermentationData {
    private String name;
    private double temp;
    private String tempUnit;
    private BigDecimal gravity;
    private String gravityUnit;
    private Instant createdAt;
}
