package com.immune.capstone.web.client;

import com.immune.capstone.config.FeignClientConfig;
import com.immune.capstone.web.client.model.GasConsumptionSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "consumption-svc", url = "${feign.consumption-svc.url}", configuration = FeignClientConfig.class)
public interface ConsumptionService {

    @RequestMapping(method = RequestMethod.GET, value = "/api/consumption/{zone}/{date}")
    GasConsumptionSummary getConsumptionRegisters(@PathVariable("zone") String zoneId,
                                                        @PathVariable("date") String date);

}
