package com.immune.capstone.web.controller.impl;

import com.immune.capstone.config.properties.RulesProperties;
import com.immune.capstone.model.Utility;
import com.immune.capstone.persistence.dao.UtilityDAO;
import com.immune.capstone.service.ReportStorageService;
import com.immune.capstone.service.UtilityCalculationService;
import com.immune.capstone.web.controller.MessageListenerController;
import com.immune.capstone.model.ReportMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MessageListenerControllerImpl implements MessageListenerController {

    private static final String UTILITIES_QUEUE_NAME = "utilities_queue";

    private final UtilityCalculationService calculationService;
    private final ReportStorageService reportStorageService;
    private final RulesProperties rulesProperties;
    private final UtilityDAO utilityDAO;

    @Override
    @SqsListener(UTILITIES_QUEUE_NAME)
    public void onMessage(ReportMessage message) {
        Map<String, Utility> utilitiesByZone = new HashMap<>(utilityDAO.getUtilsByZonePerDate(message.getDate()));

        Set<String> targetZones = rulesProperties.getAvailableZones()
                .stream()
                .filter(zone -> !utilitiesByZone.containsKey(zone))
                .collect(Collectors.toSet());

        // Force refresh for target zone and others that were not found
        targetZones.add(message.getZoneId());
        var utilitiesOpt = calculationService.calculateUtility(message.getDate(), targetZones);

        if (utilitiesOpt.isEmpty()) {
            log.warn("Unable to recalculate utilities, aborting operation.");
            return;
        }

        utilitiesByZone.putAll(utilitiesOpt.get());
        utilitiesByZone.values().forEach(this::persistAndSend);
    }

    private void persistAndSend(Utility util) {
        utilityDAO.save(util);
        reportStorageService.save(util);
    }

}
