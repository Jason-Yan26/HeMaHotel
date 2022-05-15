package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name="orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private Long categoryId;//房间类型

    @Column
    private Integer number;//房间个数

    @Column
    private Integer status;//0:已删除，1:待支付，2：已支付，3：已取消，4：已入住，5：已退房

    @Column
    private Double paymentMoney;

    @Column
    private String paymentType;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp completeTime;

    public Order(Long userId,Long categoryId,Integer number,Integer status,Double paymentMoney, Timestamp createTime){
        this.userId=userId;
        this.categoryId=categoryId;
        this.number=number;
        this.status=status;
        this.paymentMoney=paymentMoney;
        this.createTime=createTime;
    }
}
