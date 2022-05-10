package com.example.hemahotel.service;


import com.example.hemahotel.utils.ResponseUtils;

import java.sql.Date;

public interface UserService {

    /**用户注册*/
    public ResponseUtils register(String telephone,String password,String verCode,Long verCodeId);


    /**用户登录(手机号/邮箱 + 密码）*/
    public ResponseUtils loginPassword(String teleEmail, String password);

    /** 用户主页 */
    public ResponseUtils information(Long userId);

    public ResponseUtils passwordModify(Long userId,String oldPassword,String newPassword);

    public ResponseUtils informationModify(Long userId, String username, Integer gender, Date birthDate,
                                           Integer age, String signature, String preference_label,
                                           String address, String email, String phone);
}
