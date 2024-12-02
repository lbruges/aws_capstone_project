package com.immune.capstone.service;

import com.immune.capstone.model.Utility;

import java.util.Map;
import java.util.Optional;

public interface UtilityCalculationService {

    /**
     * Calculates utilities based on historical values.
     * @param dateStr target date
     * @param existingUtils available util data
     * @return non pre-existent util data
     */
    Optional<Map<String, Utility>> calculateUtility(String dateStr, Map<String, Utility> existingUtils);

}
