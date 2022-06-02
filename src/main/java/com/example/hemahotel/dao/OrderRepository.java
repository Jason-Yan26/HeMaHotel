package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findByCategoryIdIn(List<Long> categoryIds);

    List<Order> findByUserIdAndCategoryIdIn(Long userId,List<Long> categoryIds);

    Optional<Order> findByIdAndUserId(Long Id, Long userId);

    /**
     * 查询累计订单数量
     */
    long count();
    @Query(value="select count(*) as userNum from orders group by user_id", nativeQuery = true)
    List findGroupByUserId();

    /**查询某一区间的订单数量*/
    int countAllByCreateTimeBetween(Timestamp startTime,Timestamp endTime);

    List<Order> findByCompleteTimeBetween(Timestamp startTime,Timestamp endTime);

//    /**查询某一时间区间进行交易的用户数量*/
//    int countUserIdByCreateTimeBetween(Timestamp startTime,Timestamp endTime);
//
//    /**查询进行交易的用户总数量*/
//    int countUserId();

}