package com.immune.capstone.service;

import com.immune.capstone.model.Utility;

import java.util.Map;
import java.util.Optional;

public interface UtilityCalculationService {

    Optional<Map<String, Utility>> calculateUtility(String dateStr, String zoneId);

}
