package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    Optional<Order> findByIdAndUserId(Long Id, Long userId);
}