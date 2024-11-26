package com.immune.capstone.web.client;

import com.immune.capstone.config.FeignClientConfig;
import com.immune.capstone.web.model.GasConsumption;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "consumption-svc", url = "${feign.consumption-svc.url}", configuration = FeignClientConfig.class)
public interface ConsumptionService {

    @RequestMapping(method = RequestMethod.GET, value = "/api/consumption/{zone}/{date}")
    List<GasConsumption> getConsumptionRegisters(@PathVariable("zone") String zoneId,
                                                 @PathVariable("date") String date);

}
