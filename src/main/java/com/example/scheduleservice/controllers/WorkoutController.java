package com.example.scheduleservice.controllers;

import com.example.scheduleservice.messagebroker.MessageSender;
import com.example.scheduleservice.model.IsBookedDTO;
import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.requests.ClientScheduleRequest;
import com.example.scheduleservice.requests.CreateWorkoutRequest;
import com.example.scheduleservice.requests.FilterRequest;
import com.example.scheduleservice.requests.UpdateWorkoutRequest;
import com.example.scheduleservice.service.WorkoutService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final MessageSender messageSender;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public WorkoutController(WorkoutService workoutService, MessageSender messageSender) {
        this.workoutService = workoutService;
        this.messageSender = messageSender;
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
            workout.setBooked(new ArrayList<>());
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

    @PutMapping(value = "/schedule",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleAddClient(@RequestBody ClientScheduleRequest clientScheduleRequest){
        Optional<Workout> workout= (workoutService.findbyID(clientScheduleRequest.getWorkoutID()));

        List<Long> booked = workout.get().getBooked();
        if (booked == null) {
            booked = new ArrayList<>();
        }


        if(booked.contains(clientScheduleRequest.getClientID())){
            throw new RuntimeException("Korisnik vec dodat");
        }
        if(booked.size()>=workout.get().getCapacity()) {
            throw new RuntimeException("Zazueta sva mesta");
        }

        booked.add(clientScheduleRequest.getClientID());


        workout.get().setBooked(booked);

        //Povecaj count za jedan
        messageSender.sendMessage("user-service", clientScheduleRequest.getClientID().toString());


        return new ResponseEntity<>(workoutService.save(workout.get()), HttpStatus.OK);
    }


    @PutMapping(value = "/cancel",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleDeleteClient(@RequestBody ClientScheduleRequest clientScheduleRequest){
        Optional<Workout> workout= (workoutService.findbyID(clientScheduleRequest.getWorkoutID()));

        List<Long> booked = workout.get().getBooked();
        if (booked == null) {
            booked = new ArrayList<>();
        }

        booked.remove(clientScheduleRequest.getClientID());

        workout.get().setBooked(booked);

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

    @GetMapping(value = "/getType/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Workout> getWorkoutsByType(@PathVariable("type") Type type){
        return this.workoutService.findAllByType(type);

    }

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Workout> getAllWorkouts(){
        return this.workoutService.findAll();

    }

    @GetMapping(value = "/getSlobodni", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Workout> getFreeWorkouts(){
        return this.workoutService.findAllFree();

    }

    @GetMapping(value = "/getSlobodan/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean getIsFree(@PathVariable Long id){
        return this.workoutService.findIsFree(id);
    }



    @PostMapping(value = "/getfilter", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Workout> getFilterWorkouts(@RequestBody FilterRequest filterRequest){

        Type type = null;
        try {
            type = Type.valueOf(filterRequest.getType());
            System.out.println("Enum instance: " + type);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid enum value: " + filterRequest.getType());
        }

        if(!filterRequest.getType().equals("ALL") && filterRequest.getBooked().equals("ALL"))
            return this.workoutService.findAllByType(type);

        if(filterRequest.getType().equals("ALL") && !filterRequest.getBooked().equals("ALL"))
            return this.workoutService.findAllFree();

        if(!filterRequest.getType().equals("ALL") && !filterRequest.getBooked().equals("ALL"))
            return this.workoutService.findAvailableByType(type);

        return this.workoutService.findAll();

    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Workout> getByClientId(@PathVariable("id") Long id){
        return this.workoutService.findAllByClientId(id);

    }


}
