package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface EmailService {

    /**电子邮件发送短信验证码*/
    public ResponseUtils sendVerCode(String receiver);
}
