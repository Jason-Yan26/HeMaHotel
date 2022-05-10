package com.example.hemahotel.dao;

import com.example.hemahotel.entity.User;
import com.example.hemahotel.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findById(Long id);

}