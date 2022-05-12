package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    //根据酒店id查找收藏夹
    Optional<Favorite> findByUserIdAndHotelId(Long userId, Long productId);

    //根据用户id查找该用户收藏夹所有酒店
    List<Favorite> findAllByUserIdOrderByCreateTimeDesc(Long userId);

}