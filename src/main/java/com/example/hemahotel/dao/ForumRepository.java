package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForumRepository extends JpaRepository<Forum, Long> {

    Page<Forum> findAll(Pageable pageable); // JPA 分页类

    Optional<Forum> findById(Long forumId);
}