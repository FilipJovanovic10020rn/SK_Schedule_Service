package com.example.scheduleservice.controllers;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.model.UserType;
import com.example.scheduleservice.requests.CreateRoomRequest;
import com.example.scheduleservice.security.CheckSecurity;
import com.example.scheduleservice.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;


    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

/*  // todo ovo ti je autorization kako da splitujes i da dobijes id i roles
//        String[] token = authorization.split(" ");
//        System.out.println(token[1]);
//        Claims claims = tokenService.parseToken(token[1]);
//        Long idFromToken = claims.get("id", Long.class);
//        UserType userTypeFromToken = UserType.fromString(claims.get("role", String.class));
//        System.out.println(idFromToken);
//        System.out.println(userTypeFromToken);
 */


    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER})
    public List<Room> getAllRooms(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id){
//        try {
            return this.roomService.findAll(id);
//        }catch (Exception e){
//            e.printStackTrace();
//            return (List<Room>) new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
    }

    @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER})
    public ResponseEntity<?> createRoom(@RequestHeader("Authorization") String authorization, @RequestBody CreateRoomRequest requestRoom){

        System.out.println();
        try {
            Room room = new Room();
            room.setName(requestRoom.getName());
            room.setDescription((requestRoom.getDescription()));
            room.setManagerId(requestRoom.getManagerId());
            room.setNumber_of_trainers(requestRoom.getNumber_of_trainers());
            return new ResponseEntity<>(roomService.save(room), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @CheckSecurity(roles = {UserType.ADMIN,UserType.MANAGER})
    public ResponseEntity<String> deleteRoom(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        try {
            roomService.delete(id);
            return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it as needed
            return new ResponseEntity<>("Failed to delete room", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
