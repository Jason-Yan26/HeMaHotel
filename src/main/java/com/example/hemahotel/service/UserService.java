package com.example.hemahotel.service;


import com.example.hemahotel.utils.ResponseUtils;

import java.util.Map;

public interface UserService {

    /**用户登录(手机号/邮箱 + 密码）*/
    public ResponseUtils Login_Password(String teleEmail, String password);

}
