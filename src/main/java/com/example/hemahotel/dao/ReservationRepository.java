package com.example.hemahotel.dao;

import com.example.hemahotel.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    /**根据订单id和 orderIthRoom 查找reservation*/
    Optional<Reservation> findByOrderIdAndOrderIthRoom(Long orderId,Long orderIthRoom);

    /**根据客房id查找reservation*/
    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByOrderId(Long orderId);

}