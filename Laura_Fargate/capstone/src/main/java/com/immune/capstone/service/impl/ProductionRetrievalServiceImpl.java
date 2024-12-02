package com.immune.capstone.service.impl;

import com.immune.capstone.service.ProductionRetrievalService;
import com.immune.capstone.web.client.ProductionService;
import com.immune.capstone.web.client.model.GasProduction;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.immune.capstone.utils.ServiceConstants.DATE_FORMATTER;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductionRetrievalServiceImpl implements ProductionRetrievalService {

    private final ProductionService productionService;

    @Override
    public Map<String, Float> getProdCostPerZone(Collection<String> availableZones, LocalDate targetDate) {
        return availableZones.stream()
                .map(zone -> {
                    String dateStr = targetDate.format(DATE_FORMATTER);

                    try {
                        return productionService.getMonthProductionCost(zone, dateStr)
                                .withZone(zone);
                    } catch (Exception e) {
                        log.warn("Unable to obtain production data for {}, using default value.", zone, e);
                        return GasProduction.builder()
                                .costPerM3(10)
                                .date(dateStr)
                                .zone(zone)
                                .build();
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(GasProduction::getZone, GasProduction::getCostPerM3, (p1, p2) -> p1));
    }

}