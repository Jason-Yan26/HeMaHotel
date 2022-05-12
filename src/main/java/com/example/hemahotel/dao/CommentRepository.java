package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //根据酒店Id查找评论
    public List<Comment> findByHotelId(Long productId);

}