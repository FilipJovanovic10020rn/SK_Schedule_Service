package com.example.scheduleservice.repositories;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @Override
    void deleteById(Long id);
}
