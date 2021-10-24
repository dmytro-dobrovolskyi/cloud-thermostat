package com.dmytrodobrovolskyi.couldthermostat.thirdparty;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FermentationData {
    private String name;
    private double temperature;
    private String tempUnit;
    private BigDecimal gravity;
    private String gravityUnit;
    private Instant createdAt;
}
