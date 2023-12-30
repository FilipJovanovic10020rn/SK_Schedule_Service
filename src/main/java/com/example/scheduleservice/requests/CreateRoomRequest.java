package com.example.scheduleservice.requests;

import com.example.scheduleservice.model.Workout;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class CreateRoomRequest {

    private Long managerId;
    private String name;
    private String description;
    private Integer number_of_trainers;

    public CreateRoomRequest(Long managerId, String name, String description, Integer number_of_trainers) {
        this.managerId = managerId;
        this.name = name;
        this.description = description;
        this.number_of_trainers = number_of_trainers;
    }
}
