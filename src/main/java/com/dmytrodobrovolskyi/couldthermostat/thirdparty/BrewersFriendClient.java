package com.dmytrodobrovolskyi.couldthermostat.thirdparty;

import com.dmytrodobrovolskyi.couldthermostat.config.BrewersFriendConfig;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewersfriend.BrewSessionsPage;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewersfriend.FermentationData;
import com.dmytrodobrovolskyi.couldthermostat.thirdparty.model.brewersfriend.FermentationDataImportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "BrewersFriend", url = "https://api.brewersfriend.com", configuration = BrewersFriendConfig.class)
public interface BrewersFriendClient {

    @GetMapping(value = "v1/brewsessions?sort=created_at:0")
    BrewSessionsPage getLatestBrewSessions(@RequestParam("limit") int limit);

    @PostMapping(value = "v1/fermentation/{brewSessionId}/import")
    FermentationDataImportResponse importFermentationData(@PathVariable("brewSessionId") String brewSessionId,
                                                          @RequestBody List<FermentationData> data
    );
}
