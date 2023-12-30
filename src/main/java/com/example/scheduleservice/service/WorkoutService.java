package com.example.scheduleservice.service;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.repositories.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public Workout save(Workout workout){
        return this.workoutRepository.save(workout);
    }

    public Optional<Workout> findbyID(Long id){
        return this.workoutRepository.findById(id);
    }

    public List<Workout> findAllByType(Type type){
        return this.workoutRepository.findWorkoutByType(type);
    }

    public void delete(Long id){
        Optional<Workout> workout = this.workoutRepository.findById(id);
        if(workout!=null){
            this.workoutRepository.deleteById(id);
        }
    }

    public List<Workout> findAllFree(){
        return this.workoutRepository.findAllByBookedLessThanCapacity();
    }

}
