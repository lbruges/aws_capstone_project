package com.immune.capstone.service.impl;

import com.immune.capstone.config.properties.RulesProperties;
import com.immune.capstone.exception.UtilityAppException;
import com.immune.capstone.model.Utility;
import com.immune.capstone.service.UtilityCalculationService;
import com.immune.capstone.web.client.ConsumptionService;
import com.immune.capstone.web.client.ProductionService;
import com.immune.capstone.web.client.model.GasConsumptionSummary;
import com.immune.capstone.web.client.model.GasProduction;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class UtilityCalculationServiceImpl implements UtilityCalculationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");

    private final ConsumptionService consumptionService;
    private final ProductionService productionService;
    private final RulesProperties rulesProperties;

    @Override
    public Optional<Map<String, Utility>> calculateUtility(String dateStr, String zoneId) {
        var availableZones = rulesProperties.getAvailableZones();

        if (!availableZones.contains(zoneId)) {
            log.warn("Zone id {} not available in configured zones {}", zoneId, availableZones);
            throw new UtilityAppException("Zone " + zoneId + " not available.");
        }

        LocalDate targetDate = LocalDate.parse(dateStr, DATE_FORMATTER).withDayOfMonth(1);

        List<GasConsumptionSummary> avgConsumptions = getConsumptionPerZone(availableZones, targetDate);

        if (avgConsumptions.isEmpty()) {
            log.warn("No consumption data found for prior month.");
            return Optional.empty();
        }

        Set<String> zonesWithConsumption = avgConsumptions.stream()
                .map(GasConsumptionSummary::getZone)
                .collect(Collectors.toSet());

        Map<String, Double> prodCostPerZone = getProdCostPerZone(zonesWithConsumption, targetDate);
        if (prodCostPerZone.isEmpty()) {
            log.warn("No production data found for current month.");
            return Optional.empty();
        }

        double currUtil = rulesProperties.getUtility().getMinPenalty();
        double increment = rulesProperties.getUtility().getPenaltyIncrement();

        return getUtilPerZone(avgConsumptions, prodCostPerZone, currUtil, increment, targetDate);
    }

    private List<GasConsumptionSummary> getConsumptionPerZone(Set<String> availableZones, LocalDate targetDate) {

        return availableZones.stream()
                .map(zone -> {
                    try {
                        return consumptionService.getConsumptionRegisters(zone, targetDate.minusMonths(1)
                                .format(DATE_FORMATTER));
                    } catch (Exception e) {
                        log.warn("Unable to obtain consumption data for {}, skipping.", zone, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(GasConsumptionSummary::getConsumption))
                .toList();

    }

    private Map<String, Double> getProdCostPerZone(Collection<String> availableZones, LocalDate targetDate) {
        return availableZones.stream()
                .map(zone -> {
                   try {
                       return productionService.getMonthProductionCost(zone, targetDate.format(DATE_FORMATTER))
                               .withZone(zone);
                   } catch (Exception e) {
                       log.warn("Unable to obtain production data for {}, skipping.", zone, e);
                       return null;
                   }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(GasProduction::getZone, GasProduction::getCostPerM3, (p1, p2) -> p1));
    }

    private Optional<Map<String, Utility>> getUtilPerZone(List<GasConsumptionSummary> avgConsumptions, Map<String, Double> prodCostPerZone,
                                                          double initialUtil, double increment, LocalDate targetDate) {

        double currUtil = initialUtil;

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
        }

        return Optional.of(utils);
    }

}
