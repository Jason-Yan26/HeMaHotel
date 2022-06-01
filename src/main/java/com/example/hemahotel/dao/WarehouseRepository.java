package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Verification;
import com.example.hemahotel.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse,Long> {

    List<Warehouse> findByHotelId(Long hotelId);

    @Modifying
    void deleteAllByHotelId(Long hotelId);

    Optional<Warehouse> findByHotelIdAndItemName(Long hotelId,String itemName);
}
