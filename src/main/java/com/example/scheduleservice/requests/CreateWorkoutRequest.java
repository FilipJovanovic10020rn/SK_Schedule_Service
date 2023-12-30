package com.example.scheduleservice.requests;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.model.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
public class CreateWorkoutRequest {

    private String name;
    private Integer price;
    private String date;
    private Integer duration;
    private Type type;
    private Integer capacity;
    private Room room;

    public CreateWorkoutRequest(String name, int price, String date, int duration, Type type, int capacity, Room room) {
        this.name = name;
        this.price = price;
        this.date = date;
        this.duration = duration;
        this.type = type;
        this.capacity = capacity;
        this.room = room;
    }
}
