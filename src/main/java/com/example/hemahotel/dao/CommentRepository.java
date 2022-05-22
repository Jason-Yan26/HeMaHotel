package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Comment;
import com.example.hemahotel.entity.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 根据酒店Id查找评论
    Page<Comment> findByHotelId(Long hotelId, Pageable pageable);

    List<Comment> findByHotelId(Long hotelId);

    // 获取用户评论
    Page<Comment> findAllByUserId(Long userId, Pageable pageable); //JPA 分页类

    // 获取用户评论总数
    Long countByUserId(Long userId);

    Long countByHotelId(Long hotelId);
}