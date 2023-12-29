package com.example.scheduleservice.model;
import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="workout")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int price;
    private Type type;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
