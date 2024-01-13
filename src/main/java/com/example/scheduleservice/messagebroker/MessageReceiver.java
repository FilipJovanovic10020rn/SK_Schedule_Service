package com.example.scheduleservice.messagebroker;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

// Receiver poruka od drugih servisa
@Component
public class MessageReceiver {
    @JmsListener(destination = "schedule-service")
    public void receiveMessage(String message) {
        // Process the received message
        System.out.println("Received message: " + message);
    }
}