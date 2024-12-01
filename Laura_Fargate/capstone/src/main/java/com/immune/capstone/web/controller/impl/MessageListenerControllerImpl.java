package com.immune.capstone.web.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immune.capstone.config.properties.RulesProperties;
import com.immune.capstone.exception.UtilityAppException;
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

@Log4j2
@Controller
@RequiredArgsConstructor
public class MessageListenerControllerImpl implements MessageListenerController {

    private final UtilityCalculationService calculationService;
    private final ReportStorageService reportStorageService;
    private final UtilityDAO utilityDAO;

    @Override
    @SqsListener("${aws.sqs.queue-name:utilities_queue}")
    public void onMessage(ReportMessage message) {
        Map<String, Utility> utilitiesByZone = new HashMap<>(utilityDAO.getUtilsByZonePerDate(message.getDate()));
        utilitiesByZone.remove(message.getZoneId()); // force refresh

        var utilitiesOpt = calculationService.calculateUtility(message.getDate(), utilitiesByZone);

        if (utilitiesOpt.isEmpty()) {
            log.warn("Unable to recalculate utilities, aborting operation.");
            throw new UtilityAppException("Unable to recalculate utils, even for the specified zone id: " +
                    message.getZoneId());
        }

        utilitiesByZone.values().forEach(this::persistAndSend);
    }

    private void persistAndSend(Utility util) {
        utilityDAO.save(util);
        reportStorageService.save(util);
    }

}
