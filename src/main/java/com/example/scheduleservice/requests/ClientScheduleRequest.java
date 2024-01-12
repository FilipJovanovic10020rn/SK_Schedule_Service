package com.example.scheduleservice.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientScheduleRequest {

    private Long workoutID;
    private Long clientID;

    public ClientScheduleRequest(Long workoutID, Long clientID) {
        this.workoutID = workoutID;
        this.clientID = clientID;
    }
}
