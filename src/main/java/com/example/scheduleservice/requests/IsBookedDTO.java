package com.example.scheduleservice.requests;

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
