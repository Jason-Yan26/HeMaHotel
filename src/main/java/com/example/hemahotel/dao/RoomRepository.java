package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findById(Long Id);

    List<Room> findByRoomCategoryIdAndStatus(Long roomCategory,Integer status);//用于找空闲的某个房型的房间序列

    Long countByRoomCategoryIdIn(List<Long> RoomCategoryId);

    /**找个某个RoomCategoryId的所有room*/
    List<Room> findByRoomCategoryId(Long roomCategory);
}