package com.example.hemahotel.dao;

import com.example.hemahotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


//Dao层继承JpaRepository<T,ID>
// T:entity层的数据类名
// ID:实体类主键的类型
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByPhoneOrEmail(String phone,String email);

    public Optional<User> findByUsername(String username);

    public Optional<User> findById(Long id);


    /**
     * 查询累计用户注册数量
     */
    long count();

    /**查询某一区间注册的用户数量*/
    int countAllByCreateTimeBetween(Timestamp startTime, Timestamp endTime);
}
