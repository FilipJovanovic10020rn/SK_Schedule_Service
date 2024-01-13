package com.example.scheduleservice.messagebroker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

// Za slanje poruka
@Service
public class MessageSender {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);
    }


    // todo ovde napraviti odvojenu koja ceslati odredjene stvari
//    public void sendMessage(String destination, ) {
//        jmsTemplate.convertAndSend(destination, message);
//    }
    // todo ovo se koristi kada god mi radis ono povecavanje i smanjivanje treninga ali ako ne mozes tako onda cemo da resimo
    public void sendMessage(String destination, Long id) {
        jmsTemplate.convertAndSend(destination, id);
    }
}