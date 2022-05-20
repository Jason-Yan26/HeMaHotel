package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest,Long> {

    List<Guest> findAllByUserIdOrderByUpdateTimeDesc(Long userId);


    List<Guest> findByUserId(Long userId);
}
