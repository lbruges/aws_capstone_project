package com.immune.capstone.web.controller;

import com.immune.capstone.model.ReportMessage;

public interface MessageListenerController {

    void onMessage(ReportMessage message);

}
