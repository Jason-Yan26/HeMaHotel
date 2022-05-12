package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name="room_category")
@AllArgsConstructor
@NoArgsConstructor
public class RoomCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;

    @Column
    private String name;

    @Column
    private Long hotelId;

    @Column
    private String picture;

    @Column
    private Double price;

    @Column
    private String description;

    @Column
    private int maxPeople;

    @Column
    private double size;

    @Column
    private int wifi;

    @Column
    private int window;

    @Column
    private int breakfast;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

}
