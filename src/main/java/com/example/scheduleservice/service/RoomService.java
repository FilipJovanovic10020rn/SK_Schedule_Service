package com.example.scheduleservice.service;

import com.example.scheduleservice.model.Room;
import com.example.scheduleservice.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll(Long id){
        return this.roomRepository.findRoomsByManagerId(id);
    }

    public Room save(Room room){
        return this.roomRepository.save(room);
    }

    public void delete(Long id){
        Optional<Room> room = this.roomRepository.findById(id);
        if(room!=null){
            this.roomRepository.deleteById(id);
        }
    }
}
