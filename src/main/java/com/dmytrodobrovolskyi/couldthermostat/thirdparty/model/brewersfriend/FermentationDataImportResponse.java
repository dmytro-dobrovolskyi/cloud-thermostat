package com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewersfriend;

import lombok.Data;

@Data
public class FermentationDataImportResponse {
    private String message;
    private int imported;
}
