package com.example.scheduleservice.repositories;

import com.example.scheduleservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findRoomsByManagerId(Long id);

//    void delete(Optional<Room> room);


}
