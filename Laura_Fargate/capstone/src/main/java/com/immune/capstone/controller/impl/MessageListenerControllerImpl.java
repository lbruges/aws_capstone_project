package com.immune.capstone.controller.impl;

import com.immune.capstone.controller.MessageListenerController;
import com.immune.capstone.model.ReportMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Controller;

@Controller
public class MessageListenerControllerImpl implements MessageListenerController {

    private static final String UTILITIES_QUEUE_NAME = "utilities_queue";

    @Override
    @SqsListener(UTILITIES_QUEUE_NAME)
    public void listen(ReportMessage message) {

    }

}
