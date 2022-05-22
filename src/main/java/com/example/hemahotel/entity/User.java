package com.example.hemahotel.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "user")
@Data//提供类的get、set、equals、hashCode、canEqual、toString方法
@AllArgsConstructor//提供类的全参构造
@NoArgsConstructor//提供类的无参构造
public class User {

    @Id//主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
    private Long id;//用户id

    @Column
    private String username;//用户名

    @Column
    @JsonIgnore  //返回时排除掉这个字段
    private String password;//密码

    @Column
    private String email;//邮箱

    @Column
    private String phone;//手机号

    @Column
    private String avatar;//头像

    @Column
    private Integer gender;//性别

    @Column
    private Date birthDate;//出生年月


    @Column
    private String signature;//个性签名

    @Column
    private String preferenceLabel;//个人偏好标签

    @Column
    private String address;//现居地

    @Column
    private Integer identity;//身份

    @Column
    private Long hotelId;//酒店id

    @Column
    private Integer status;//身份

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;//用户创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;//用户更新时间

    public User(String username, String password, String phone, Integer status, Timestamp createTime, Timestamp updateTime) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
