package com.example.hemahotel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private Long guestId1;

    @Column
    private Long guestId2;

    @Column
    private Long guestId3;

    @Column
    private Timestamp startTime;

    @Column
    private Timestamp endTime;

    public Reservation(Long userId, Long orderId, Long roomId,Timestamp startTime){
        this.userId=userId;
        this.orderId=orderId;
        this.roomId=roomId;
        this.startTime=startTime;
    }

}
