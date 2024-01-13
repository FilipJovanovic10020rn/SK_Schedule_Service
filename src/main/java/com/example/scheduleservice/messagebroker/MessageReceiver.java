package com.example.scheduleservice.messagebroker;

import com.example.scheduleservice.security.service.TokenService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// Receiver poruka od drugih servisa
@Service
public class MessageReceiver {

    private final TokenService tokenService;

    public MessageReceiver(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @JmsListener(destination = "schedule-service")
    public void receiveMessage(String message) {
        // Process the received message
        System.out.println("Received message: " + message);
    }

    @JmsListener(destination = "schedule-service/setkey")
    public void receiveMessageForKey(String encodedKey) {
        // Process the received message
        System.out.println("Received message: " + encodedKey);

        this.tokenService.setJwtSecretKey(encodedKey);

    }
}