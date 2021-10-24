package com.dmytrodobrovolskyi.couldthermostat.thirdparty.model;

import lombok.Data;

@Data
public class FermentationDataImportResponse {
    private String message;
    private int imported;
}
