package com.example.scheduleservice.controllers;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.requests.CreateRoomRequest;
import com.example.scheduleservice.requests.CreateWorkoutRequest;
import com.example.scheduleservice.requests.UpdateWorkoutRequest;
import com.example.scheduleservice.service.WorkoutService;
import org.hibernate.jdbc.Work;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWorkout(@RequestBody CreateWorkoutRequest requestWorkout){

        try {
            Workout workout = new Workout();
            workout.setName(requestWorkout.getName());
            workout.setPrice(requestWorkout.getPrice());
            Date parsedDate = dateFormat.parse(requestWorkout.getDate());
            System.out.println(parsedDate);
            workout.setDate(parsedDate);
            workout.setType(requestWorkout.getType());
            if(requestWorkout.getType().equals(Type.INDIVIDUAL))
                workout.setCapacity(1);
            else
                workout.setCapacity(requestWorkout.getCapacity());
            workout.setDuration(requestWorkout.getDuration());
            workout.setRoom(requestWorkout.getRoom());
            workout.setBooked(0);
            return new ResponseEntity<>(workoutService.save(workout), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update/{id}",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatebyID(@PathVariable("id")Long id, @RequestBody UpdateWorkoutRequest updateWorkout){
        Optional<Workout> workout= (workoutService.findbyID(id));
            if (workout == null) {
            return ResponseEntity.notFound().build();
            }
            BeanUtils.copyProperties(updateWorkout, workout.get(), "id");
            return new ResponseEntity<>(workoutService.save(workout.get()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWorkout(@PathVariable Long id) {
        try {
            workoutService.delete(id);
            return new ResponseEntity<>("Workout deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it as needed
            return new ResponseEntity<>("Failed to delete workout", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
