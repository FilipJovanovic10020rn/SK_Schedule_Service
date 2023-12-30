package com.example.scheduleservice.repositories;

import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @Override
    void deleteById(Long id);
    List<Workout> findWorkoutByType(Type type);

    @Query("SELECT w FROM Workout w WHERE w.booked < w.capacity")
    List<Workout> findAllByBookedLessThanCapacity();

}
