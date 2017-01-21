package com.alext.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
class JmsPublisher {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void publish(final Transaction message){
        jmsTemplate.convertAndSend(message);
    }
}
