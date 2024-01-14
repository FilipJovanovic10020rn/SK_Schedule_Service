package com.example.scheduleservice.controllers;

import com.example.scheduleservice.messagebroker.MessageSender;
import com.example.scheduleservice.model.Type;
import com.example.scheduleservice.model.UserType;
import com.example.scheduleservice.model.Workout;
import com.example.scheduleservice.requests.*;
import com.example.scheduleservice.security.CheckSecurity;
import com.example.scheduleservice.security.service.TokenService;
import com.example.scheduleservice.service.WorkoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@EnableRetry
@CrossOrigin
@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final MessageSender messageSender;

    private final TokenService tokenService;

    private final RestTemplate restTemplate;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public WorkoutController(WorkoutService workoutService, MessageSender messageSender, TokenService tokenService, RestTemplate restTemplate) {
        this.workoutService = workoutService;
        this.messageSender = messageSender;
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER})
    public ResponseEntity<?> createWorkout(@RequestHeader("Authorization") String authorization,
                                           @RequestBody CreateWorkoutRequest requestWorkout){

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
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER})
    public ResponseEntity<?> updatebyID(@RequestHeader("Authorization") String authorization,
                                        @PathVariable("id")Long id, @RequestBody UpdateWorkoutRequest updateWorkout){
        Optional<Workout> workout= (workoutService.findbyID(id));
            if (workout == null) {
            return ResponseEntity.notFound().build();
            }
            BeanUtils.copyProperties(updateWorkout, workout.get(), "id");
            return new ResponseEntity<>(workoutService.save(workout.get()), HttpStatus.OK);
    }
    @PutMapping(value = "/schedule",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.CLIENT})
    public ResponseEntity<?> scheduleAddClient(@RequestHeader("Authorization") String authorization,
                                               @RequestBody ClientScheduleRequest clientScheduleRequest) throws JsonProcessingException {
        Optional<Workout> workout= (workoutService.findbyID(clientScheduleRequest.getWorkoutID()));

        List<Long> booked = workout.get().getBooked();
        if (booked == null) {
            booked = new ArrayList<>();
        }

        String[] token = authorization.split(" ");
        System.out.println(token[1]);
        Claims claims = tokenService.parseToken(token[1]);
        Long idFromToken = claims.get("id", Long.class);
        UserType userTypeFromToken = UserType.fromString(claims.get("role", String.class));
        System.out.println(idFromToken);
        System.out.println(userTypeFromToken);


        if(booked.contains(idFromToken)){
            throw new RuntimeException("Korisnik vec dodat");
        }
        if(booked.size()>=workout.get().getCapacity()) {
            throw new RuntimeException("Zazueta sva mesta");
        }

        booked.add(idFromToken);


        workout.get().setBooked(booked);

        //Povecaj count za jedan
        messageSender.sendMessage("user-service/book", idFromToken);
        NotifyServiceBookDTO notifyServiceBookDTO = new NotifyServiceBookDTO(idFromToken,workout.get().getRoom().getManagerId(),
                workout.get().getName(),workout.get().getDate());
        messageSender.sendMessage("notify-service/book", notifyServiceBookDTO);

        return new ResponseEntity<>(workoutService.save(workout.get()), HttpStatus.OK);
    }

    @PutMapping(value = "/cancel",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.CLIENT,UserType.MANAGER,UserType.ADMIN})
    public ResponseEntity<?> scheduleDeleteClient(@RequestHeader("Authorization") String authorization,
                                                  @RequestBody ClientScheduleRequest clientScheduleRequest) throws JsonProcessingException {
        Optional<Workout> workout = (workoutService.findbyID(clientScheduleRequest.getWorkoutID()));

        String[] token = authorization.split(" ");
        System.out.println(token[1]);
        Claims claims = tokenService.parseToken(token[1]);
        Long idFromToken = claims.get("id", Long.class);
        UserType userTypeFromToken = UserType.fromString(claims.get("role", String.class));
        System.out.println(idFromToken);
        System.out.println(userTypeFromToken);

        if (userTypeFromToken == UserType.CLIENT){

            List<Long> booked = workout.get().getBooked();
            if (booked == null) {
                booked = new ArrayList<>();
            }

            booked.remove(idFromToken);

            workout.get().setBooked(booked);

            messageSender.sendMessage("user-service/cancel", idFromToken);
            NotifyServiceBookDTO notifyServiceBookDTO = new NotifyServiceBookDTO(idFromToken,workout.get().getRoom().getManagerId(),
                    workout.get().getName(),workout.get().getDate());
            messageSender.sendMessage("notify-service/cancelFromClient", notifyServiceBookDTO);
            return new ResponseEntity<>(workoutService.save(workout.get()), HttpStatus.OK);
        }
        else{
            try {
                List<Long> clients = workoutService.findAllClientsByWorkoutID(workout.get().getId());

                workoutService.delete(workout.get().getId());

                for(Long id: clients){
                    messageSender.sendMessage("user-service/cancel", id);
                    NotifyServiceBookDTO notifyServiceBookDTO = new NotifyServiceBookDTO(id,workout.get().getRoom().getManagerId(),
                            workout.get().getName(),workout.get().getDate());
                    messageSender.sendMessage("notify-service/cancelClient", notifyServiceBookDTO);

                }
                NotifyServiceBookDTO notifyServiceBookDTO = new NotifyServiceBookDTO(clients.get(0),workout.get().getRoom().getManagerId(),
                        workout.get().getName(),workout.get().getDate());
                messageSender.sendMessage("notify-service/cancel", notifyServiceBookDTO);
                return new ResponseEntity<>("Workout deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception or handle it as needed
                return new ResponseEntity<>("Failed to delete workout", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/delete/{id}")
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER})
    public ResponseEntity<String> deleteWorkout(@RequestHeader("Authorization") String authorization,@PathVariable Long id) {
        try {
            workoutService.delete(id);
            return new ResponseEntity<>("Workout deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it as needed
            return new ResponseEntity<>("Failed to delete workout", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(value = "/getType/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER, UserType.CLIENT})
    public List<Workout> getWorkoutsByType(@RequestHeader("Authorization") String authorization, @PathVariable("type") Type type){
        return this.workoutService.findAllByType(type);

    }
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER,UserType.CLIENT})
    public List<Workout> getAllWorkouts(@RequestHeader("Authorization") String authorization){
        return this.workoutService.findAll();

    }

    @GetMapping(value = "/getSlobodni", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER,UserType.CLIENT})
    public List<Workout> getFreeWorkouts(@RequestHeader("Authorization") String authorization){
        return this.workoutService.findAllFree();

    }
    @GetMapping(value = "/getSlobodan/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER,UserType.CLIENT})
    public boolean getIsFree(@RequestHeader("Authorization") String authorization,@PathVariable Long id){
        return this.workoutService.findIsFree(id);
    }


    @PostMapping(value = "/getfilter", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER, UserType.CLIENT})
    public List<Workout> getFilterWorkouts(@RequestHeader("Authorization") String authorization,@RequestBody FilterRequest filterRequest){

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
    @GetMapping(value = "/getClientWorkouts", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER, UserType.CLIENT})
    public List<Workout> getByClientId(@RequestHeader("Authorization") String authorization){

        String[] token = authorization.split(" ");
        System.out.println(token[1]);
        Claims claims = tokenService.parseToken(token[1]);
        Long idFromToken = claims.get("id", Long.class);
        UserType userTypeFromToken = UserType.fromString(claims.get("role", String.class));
        System.out.println(idFromToken);
        System.out.println(userTypeFromToken);

        return this.workoutService.findAllByClientId(idFromToken);

    }
    @Retryable(value = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @GetMapping(value = "/pay/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = { UserType.CLIENT})
    public int getPrice(@RequestHeader("Authorization") String authorization,@PathVariable("id") Long workoutID){

        Optional<Workout> workout = this.workoutService.findbyID(workoutID);


        String[] token = authorization.split(" ");
        System.out.println(token[1]);
        Claims claims = tokenService.parseToken(token[1]);
        Long idFromToken = claims.get("id", Long.class);
        UserType userTypeFromToken = UserType.fromString(claims.get("role", String.class));
        System.out.println(idFromToken);
        System.out.println(userTypeFromToken);

        String trainingServiceUrl = "http://localhost:8084/api/users/get-workout-count/" + idFromToken;
        //get-workout-count

        ResponseEntity<?> responseEntity = restTemplate.exchange(
                trainingServiceUrl,
                HttpMethod.GET,
                null,
                String.class
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("OVO JE BODY:" + responseEntity.getBody().toString());
            int count = Integer.parseInt(responseEntity.getBody().toString());
            System.out.println("OVO JE BROJ" +count);
            if(count >=10)
                return 0;
            else
                return workout.get().getPrice();
        } else {
            // Trening servis nije uspeo obrisati korisnika
            throw new RuntimeException("Error deleting user in training service.");
        }

    }


}
