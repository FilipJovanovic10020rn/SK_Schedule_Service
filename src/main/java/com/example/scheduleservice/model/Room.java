package com.example.scheduleservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;



@Data
@Entity
@Table(name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long managerId;
    private String name;
    private String description;
    private Integer number_of_trainers;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Workout> workouts;

}
