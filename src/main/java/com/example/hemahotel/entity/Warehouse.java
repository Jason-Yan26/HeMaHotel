package com.example.hemahotel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "warehouse") //@Table来指定和哪个数据表对应;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {

    @Id// 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;//仓库id

    @Column
    private Long hotelId;//酒店id

    @Column
    private String itemName;//仓库物品名称

    @Column
    private Long itemNumber;//仓库物品数量

    public Warehouse(Long hotelId, String itemName, Long itemNumber) {
        this.hotelId = hotelId;
        this.itemName = itemName;
        this.itemNumber = itemNumber;
    }
}
