package com.immune.capstone.service.impl;

import com.immune.capstone.config.properties.RulesProperties;
import com.immune.capstone.model.Utility;
import com.immune.capstone.service.ConsumptionRetrievalService;
import com.immune.capstone.service.ProductionRetrievalService;
import com.immune.capstone.service.UtilityCalculationService;
import com.immune.capstone.web.client.model.GasConsumptionSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
    public Optional<Map<String, Utility>> calculateUtility(String dateStr, Map<String, Utility> existingUtils) {
        Set<String> zonesToQuery = rulesProperties.getAvailableZones()
                .stream()
                .filter(zone -> !existingUtils.containsKey(zone))
                .collect(Collectors.toSet());

        LocalDate targetDate = LocalDate.parse(dateStr, DATE_FORMATTER).withDayOfMonth(1);

        // Historical consumption, 1 month prior
        List<GasConsumptionSummary> avgConsumptions = consumptionRtrvlService.getConsumptionPerZone(zonesToQuery,
                targetDate);

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

        SortedSet<Double> sortedUtils = generateSortedUtils(rulesProperties.getAvailableZones().size(), currUtil,
                increment, existingUtils.values());

        return Optional.of(getUtilPerZone(avgConsumptions, prodCostPerZone, targetDate,
                sortedUtils));
    }

    private Map<String, Utility> getUtilPerZone(List<GasConsumptionSummary> avgConsumptions,
                                                          Map<String, Double> prodCostPerZone,
                                                          LocalDate targetDate, SortedSet<Double> sortedUtils) {

        Map<String, Utility> utils = new HashMap<>();

        for (var consumption : avgConsumptions) {
            String zone = consumption.getZone();
            String dateStr = targetDate.format(DATE_FORMATTER);

            Double cost = prodCostPerZone.get(zone);
            if (cost == null) {
                continue;
            }

            var utility = sortedUtils.first();

            Utility util = Utility.builder()
                    .id(zone + "_" + dateStr)
                    .zone(zone)
                    .date(dateStr)
                    .pricePerM3(cost * (1 + utility))
                    .utility(utility)
                    .build();

            utils.put(zone, util);
            sortedUtils.remove(utility);
        }

        return utils;
    }

    private SortedSet<Double> generateSortedUtils(int size, double currUtil, double increment,
                                                  Collection<Utility> currUtils) {
        SortedSet<Double> sortedUtils = new TreeSet<>();

        for (int i = 0; i < size; i++) {
            sortedUtils.add(currUtil);
            currUtil += increment;
        }

        currUtils.stream()
                .filter(util -> sortedUtils.contains(util.getUtility()))
                .forEach(util -> sortedUtils.remove(util.getUtility()));

        return sortedUtils;
    }

}
