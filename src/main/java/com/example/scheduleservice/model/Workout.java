package com.example.scheduleservice.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.sql.Time;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
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
    private int capacity;

    @ElementCollection
    @CollectionTable(name = "workout_clients", joinColumns = @JoinColumn(name = "workout_id"))
    @Column(name = "client_id")
    private List<Long> booked;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;


    public boolean isCancellationRequired() {
        Date now = new Date();
        long timeDifference = date.getTime() - now.getTime();
        long hoursDifference = timeDifference / (60 * 60 * 1000);

        if (hoursDifference <= 24) {

            Set<Long> uniqueClients = new HashSet<>(booked);
            return uniqueClients.size() < 3;
        }

        return false;
    }
}
