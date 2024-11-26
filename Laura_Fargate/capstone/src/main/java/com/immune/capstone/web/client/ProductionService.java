package com.immune.capstone.web.client;

import com.immune.capstone.config.FeignClientConfig;
import com.immune.capstone.web.model.GasConsumption;
import com.immune.capstone.web.model.GasProduction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "production-svc", url = "${feign.production-svc.url}", configuration = FeignClientConfig.class)
public interface ProductionService {

    @RequestMapping(method = RequestMethod.GET, value = "/api/production/{zone}/{date}")
    GasProduction getMonthProductionCost(@PathVariable("zone") String zoneId, @PathVariable("date") String date);

}
