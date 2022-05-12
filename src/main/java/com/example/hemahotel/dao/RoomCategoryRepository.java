package com.example.hemahotel.dao;

import com.example.hemahotel.entity.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomCategoryRepository extends JpaRepository<RoomCategory, Long> {

    Optional<RoomCategory> findById(Long Id);

    List<RoomCategory> findByHotelId(Long hotelId);

    Optional<RoomCategory> findByIdAndHotelId(Long Id,Long hotelId);
}