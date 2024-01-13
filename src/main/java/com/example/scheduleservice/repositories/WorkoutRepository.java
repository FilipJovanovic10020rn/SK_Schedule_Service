package com.example.scheduleservice.repositories;

import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @Override
    void deleteById(Long id);
    List<Workout> findWorkoutByType(Type type);

    @Query("SELECT CASE WHEN SIZE(w.booked) >= w.capacity THEN true ELSE false END FROM Workout w WHERE w.id = :workoutId")
    Boolean isFullyBooked(@Param("workoutId") Long workoutId);

    @Query("SELECT w FROM Workout w WHERE w.type = :type AND SIZE(w.booked) < w.capacity")
    List<Workout> findAvailableWorkoutsByType(@Param("type") Type type);

    @Query("SELECT w FROM Workout w JOIN w.booked wc WHERE wc = :clientId")
    List<Workout> findWorkoutsByClientId(@Param("clientId") Long clientId);

    @Query("SELECT w FROM Workout w WHERE SIZE(w.booked) < w.capacity")
    List<Workout> findAllByClientsLessThanCapacity();

    List<Workout> findAllByDateAfter(Date date);

    @Query("SELECT COUNT(wc) FROM Workout w JOIN w.booked wc WHERE wc = :clientId")
    int countClientOccurrences(@Param("clientId") Long clientId);

    @Modifying
    @Query("DELETE FROM Workout w WHERE w.id = :workoutId")
    void deleteWorkoutClientsByWorkoutId(@Param("workoutId") Long workoutId);

    @Query("SELECT clientId FROM Workout w JOIN w.booked clientId WHERE w.id = :workoutId")
    List<Long> findClientIdsByWorkoutId(@Param("workoutId") Long workoutId);
}
