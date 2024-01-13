package com.example.scheduleservice.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilterRequest {

    private String type;
    private String booked;

    public FilterRequest(String type, String booked) {
        this.type = type;
        this.booked = booked;
    }
}
