package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "favorite")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class Favorite {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;//收藏夹id

    @Column
    private Long userId;//用户id

    @Column
    private Long hotelId;//酒店id

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;//收藏记录创建时间

    public Favorite(Long userId, Long hotelId, Timestamp createTime) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.createTime = createTime;
    }
}