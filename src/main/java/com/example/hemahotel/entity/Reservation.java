package com.example.hemahotel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
@Table(name="reservation")
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private Long orderId;

    @Column
    private Long roomId;

    @Column
    private Long orderIthRoom;//表示 roomId表示的房间 是 orderId表示的订单中的第 i 个

    @Column
    private Long guestId1;

    @Column
    private Long guestId2;

    @Column
    private Long guestId3;

    @Column
    private Date startTime;

    @Column
    private Date endTime;

    public Reservation(Long userId, Long orderId, Long roomId,Long orderIthRoom,Date startTime,Date endTime){
        this.userId=userId;
        this.orderId=orderId;
        this.roomId=roomId;
        this.orderIthRoom = orderIthRoom;
        this.startTime=startTime;
        this.endTime = endTime;
    }

}
