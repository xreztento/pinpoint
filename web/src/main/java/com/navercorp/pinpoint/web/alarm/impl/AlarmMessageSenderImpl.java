package com.navercorp.pinpoint.web.alarm.impl;


import com.navercorp.pinpoint.web.alarm.AlarmMessageSender;
import com.navercorp.pinpoint.web.alarm.checker.AlarmChecker;
import com.navercorp.pinpoint.web.service.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AlarmMessageSenderImpl implements AlarmMessageSender{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserGroupService userGroupService;

    @Override
    public void sendSms(AlarmChecker checker, int sequenceCount) {
        List<String> receivers = userGroupService.selectPhoneNumberOfMember(checker.getuserGroupId());

        if(receivers.size() == 0){
            return;
        }

        for(String message : checker.getSmsMessage()){
            logger.info("send alarm sms : {}", message);
        }
    }

    @Override
    public void sendEmail(AlarmChecker checker, int sequenceCount) {
        List<String> receivers = userGroupService.selectEmailOfMember(checker.getuserGroupId());

        if(receivers.size() == 0){
            return;
        }
        logger.info("send alarm email : {}", checker.getEmailMessage());

    }
}

