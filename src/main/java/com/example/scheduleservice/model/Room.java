package com.example.scheduleservice.model;

import com.sun.istack.NotNull;
import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;



@Data
@Entity
@Table(name="room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long managerId;
    private String name;
    private String description;
    private Integer number_of_trainers;
//    @ManyToMany(mappedBy = "room", cascade = CascadeType.ALL)
//    @JoinColumn(name = "workout_name")
//    private List<Workout> workouts;

    @ManyToMany
    @JoinTable(
            name = "room_workout",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "workout_id")
    )
    private List<Workout> workouts;

}
