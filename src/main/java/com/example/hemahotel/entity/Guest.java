package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "guest") //@Table来指定和哪个数据表对应;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guest {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;//住客id

    @Column
    private Long userId;//用户id

    @Column
    private String name ;//住客姓名

    @Column
    private String phone;//住客电话

    @Column
    private String idNumber;//住客身份证号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;//住客信息创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;//住客信息更新时间

    public Guest(Long userId, String name, String phone, String idNumber, Timestamp createTime, Timestamp updateTime) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.idNumber = idNumber;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
