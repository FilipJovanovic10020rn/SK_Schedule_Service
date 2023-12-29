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
    private Integer id;
    private Integer managerId;
    private String name;
    private String description;
    private Integer number_of_trainers;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Workout> workouts;

}
