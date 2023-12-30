package com.example.scheduleservice.model;
import lombok.Data;


import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer price;
    private Date date;
    private Integer duration;
    private Type type;
    private Integer capacity;
    private Integer booked;


    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
