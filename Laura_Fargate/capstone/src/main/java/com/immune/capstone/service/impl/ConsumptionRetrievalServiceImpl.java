package com.immune.capstone.service.impl;

import com.immune.capstone.service.ConsumptionRetrievalService;
import com.immune.capstone.web.client.ConsumptionService;
import com.immune.capstone.web.client.model.GasConsumptionSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.immune.capstone.utils.ServiceConstants.DATE_FORMATTER;

@Log4j2
@Component
@RequiredArgsConstructor
public class ConsumptionRetrievalServiceImpl implements ConsumptionRetrievalService {

    private final ConsumptionService consumptionService;

    @Override
    public List<GasConsumptionSummary> getConsumptionPerZone(Set<String> availableZones, LocalDate targetDate) {
        return availableZones.stream()
                .map(zone -> {
                    LocalDate pastMonth = targetDate.minusMonths(1);
                    try {
                        return consumptionService.getConsumptionRegisters(zone, pastMonth.format(DATE_FORMATTER));
                    } catch (Exception e) {
                        log.warn("Unable to obtain consumption data for {}, using default values.", zone, e);
                        return GasConsumptionSummary.builder()
                                .zone(zone)
                                .year(pastMonth.getYear())
                                .month(pastMonth.getMonthValue())
                                .avgConsumption(100)
                                .build();
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(GasConsumptionSummary::getAvgConsumption))
                .toList();
    }

}
