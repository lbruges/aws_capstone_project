package com.immune.capstone.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "svc.rules")
public class RulesProperties {

    private Set<String> availableZones = new HashSet<>();
    private UtilityConfig utility;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtilityConfig {

        private double minPenalty = 0.1;
        private double penaltyIncrement = 0.05;

    }

}
