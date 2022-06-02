package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Guest;
import com.example.hemahotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest,Long> {

    List<Guest> findAllByUserIdOrderByUpdateTimeDesc(Long userId);

    public Optional<Guest> findById(Long id);

    List<Guest> findByUserId(Long userId);

    List<Guest> findByIdIn(List<Long> Ids);
}
