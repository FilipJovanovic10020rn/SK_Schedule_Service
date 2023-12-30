package com.example.scheduleservice.requests;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.model.Type;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateWorkoutRequest {

    private String name;
    private Integer price;
    private String date;
    private Integer duration;
    private Type type;
    private Integer capacity;
    private Room room;
    private Integer booked;

    public UpdateWorkoutRequest(String name, int price, String date, int duration, Type type, int capacity, Room room, int booked) {
        this.name = name;
        this.price = price;
        this.date = date;
        this.duration = duration;
        this.type = type;
        this.capacity = capacity;
        this.room = room;
        this.booked = booked;
    }
}
