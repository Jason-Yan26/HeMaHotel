package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "hotel")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id; // 酒店id

    @Column
    private String name;//酒店名

    @Column
    private String location;//位置

    @Column
    private String picture;//酒店图片

    //@Column
    //private Double price;//酒店房间中最低价

    @Column
    private Integer star;//星级

    @Column
    private String phone;//酒店客服联系电话

    @Column
    private String description;//酒店描述

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;//创建时间


    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;//更新时间

    public Hotel(String name, String location, String picture, Integer star, String phone, String description, Timestamp createTime, Timestamp updateTime) {
        this.name = name;
        this.location = location;
        this.picture = picture;
        this.star = star;
        this.phone = phone;
        this.description = description;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
