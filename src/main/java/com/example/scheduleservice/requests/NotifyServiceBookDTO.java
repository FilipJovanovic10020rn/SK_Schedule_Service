package com.example.scheduleservice.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyServiceBookDTO {

    private Long cilentID;
    private Long managerID;

    private String workoutName;

    private Date date;


}
