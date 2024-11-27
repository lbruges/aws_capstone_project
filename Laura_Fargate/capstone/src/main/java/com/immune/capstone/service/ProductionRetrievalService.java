package com.immune.capstone.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface ProductionRetrievalService {
    Map<String, Double> getProdCostPerZone(Collection<String> availableZones, LocalDate targetDate);

}
