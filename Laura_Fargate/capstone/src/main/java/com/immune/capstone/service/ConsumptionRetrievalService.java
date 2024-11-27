package com.immune.capstone.service;

import com.immune.capstone.web.client.model.GasConsumptionSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ConsumptionRetrievalService {

    List<GasConsumptionSummary> getConsumptionPerZone(Set<String> availableZones, LocalDate targetDate);

}
