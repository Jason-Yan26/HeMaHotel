package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Comment;
import com.example.hemahotel.entity.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 根据酒店Id查找评论
    public List<Comment> findByHotelId(Long productId);

    // 获取用户评论
    Page<Comment> findAllByUserId(Long userId, Pageable pageable); //JPA 分页类

    // 获取用户评论总数
    public Long countByUserId(Long userId);
}