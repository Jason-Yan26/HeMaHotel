package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comment") //@Table来指定和哪个数据表对应;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Comment {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;//评论id

    @Column
    private String content;//评论

    @Column
    private Long userId;//用户id

    @Column
    private Long hotelId;//商品id

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;//商品更新时间

    @Column
    private int star;//酒店评分
}
