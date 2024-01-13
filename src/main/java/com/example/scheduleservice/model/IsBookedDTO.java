package com.example.scheduleservice.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IsBookedDTO {

    private Boolean isBooked;

    public IsBookedDTO(Boolean isBooked) {
        this.isBooked = isBooked;
    }
}
