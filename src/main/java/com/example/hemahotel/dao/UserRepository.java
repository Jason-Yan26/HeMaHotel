package com.example.hemahotel.dao;

import com.example.hemahotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


//Dao层继承JpaRepository<T,ID>
// T:entity层的数据类名
// ID:实体类主键的类型
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByPhoneOrEmail(String phone,String email);


}
