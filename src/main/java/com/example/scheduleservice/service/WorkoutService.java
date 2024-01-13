package com.example.scheduleservice.service;

import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.repositories.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public List<Workout> findAvailableByType(Type type){
        return this.workoutRepository.findAvailableWorkoutsByType(type);
    }

    public boolean findIsFree(Long workoutId) {

//        Boolean result = workoutRepository.isFullyBooked(workoutId);
//        System.out.println(result);
//        IsBookedDTO bookedDTO = null;
//        if(result != null && result){
//            bookedDTO = new IsBookedDTO(result);
//            return bookedDTO;
//        }
//        else
//            throw new RuntimeException("GRESKAAAAAAAAAAAAAA");

        Boolean result = workoutRepository.isFullyBooked(workoutId);
        return result != null && result;
    }

    public List<Workout> findFilter(Type type){
        return this.workoutRepository.findWorkoutByType(type);
    }

    public List<Workout> findAllByClientId(Long id){
        return this.workoutRepository.findWorkoutsByClientId(id);
    }


    public List<Workout> findAll(){
        return this.workoutRepository.findAll();
    }

    public void delete(Long id){
        Optional<Workout> workout = this.workoutRepository.findById(id);
        if(workout!=null){
            this.workoutRepository.deleteById(id);
        }
    }

    public List<Workout> findAllFree(){
        return this.workoutRepository.findAllByClientsLessThanCapacity();
    }

    public List<Workout> findUpcomingWorkouts() {
        Date currentDate = new Date();
        return workoutRepository.findAllByDateAfter(currentDate);
    }

    public int ClientOccurrences(Long id){
        return workoutRepository.countClientOccurrences(id);
    }

    public void delete(Workout workout ){
            this.workoutRepository.deleteById(workout.getId());
            this.workoutRepository.deleteWorkoutClientsByWorkoutId(workout.getId());
    }


}
