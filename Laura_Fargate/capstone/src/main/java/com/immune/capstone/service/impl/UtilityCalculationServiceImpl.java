package com.immune.capstone.service.impl;

import com.immune.capstone.config.properties.RulesProperties;
import com.immune.capstone.exception.UtilityAppException;
import com.immune.capstone.model.Utility;
import com.immune.capstone.service.ConsumptionRetrievalService;
import com.immune.capstone.service.ProductionRetrievalService;
import com.immune.capstone.service.UtilityCalculationService;
import com.immune.capstone.web.client.model.GasConsumptionSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.immune.capstone.utils.ServiceConstants.DATE_FORMATTER;

@Log4j2
@Service
@RequiredArgsConstructor
public class UtilityCalculationServiceImpl implements UtilityCalculationService {

    private final ConsumptionRetrievalService consumptionRtrvlService;
    private final ProductionRetrievalService productionRtrvlService;
    private final RulesProperties rulesProperties;

    @Override
    public Optional<Map<String, Utility>> calculateUtility(String dateStr, String zoneId) {
        var availableZones = rulesProperties.getAvailableZones();

        if (!availableZones.contains(zoneId)) {
            log.warn("Zone id {} not available in configured zones {}", zoneId, availableZones);
            throw new UtilityAppException("Zone " + zoneId + " not available.");
        }

        LocalDate targetDate = LocalDate.parse(dateStr, DATE_FORMATTER).withDayOfMonth(1);

        List<GasConsumptionSummary> avgConsumptions = consumptionRtrvlService.getConsumptionPerZone(availableZones, targetDate);

        if (avgConsumptions.isEmpty()) {
            log.warn("No consumption data found for prior month.");
            return Optional.empty();
        }

        Set<String> zonesWithConsumption = avgConsumptions.stream()
                .map(GasConsumptionSummary::getZone)
                .collect(Collectors.toSet());

        Map<String, Double> prodCostPerZone = productionRtrvlService.getProdCostPerZone(zonesWithConsumption, targetDate);
        if (prodCostPerZone.isEmpty()) {
            log.warn("No production data found for current month.");
            return Optional.empty();
        }

        double currUtil = rulesProperties.getUtility().getMinPenalty();
        double increment = rulesProperties.getUtility().getPenaltyIncrement();

        return getUtilPerZone(avgConsumptions, prodCostPerZone, currUtil, increment, targetDate);
    }


    private Optional<Map<String, Utility>> getUtilPerZone(List<GasConsumptionSummary> avgConsumptions, Map<String, Double> prodCostPerZone,
                                                          double currUtil, double increment, LocalDate targetDate) {
        Map<String, Utility> utils = new HashMap<>();
        for (var consumption : avgConsumptions) {
            String zone = consumption.getZone();
            String dateStr = targetDate.format(DATE_FORMATTER);

            Double cost = prodCostPerZone.get(zone);
            if (cost == null) {
                continue;
            }

            Utility util = Utility.builder()
                    .id(zone + "_" + dateStr)
                    .zone(zone)
                    .date(dateStr)
                    .pricePerM3(cost * (1 + currUtil))
                    .utility(currUtil)
                    .build();

            utils.put(zone, util);

            currUtil += increment;
        }

        return Optional.of(utils);
    }

}
