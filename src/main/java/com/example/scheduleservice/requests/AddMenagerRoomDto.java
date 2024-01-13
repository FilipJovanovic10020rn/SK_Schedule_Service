package com.example.scheduleservice.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddMenagerRoomDto {
    private Long managerId;
    private String name; // naziv room-a
}
