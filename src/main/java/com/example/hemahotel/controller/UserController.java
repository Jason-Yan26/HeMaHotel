package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/test")
    public Map<String, Object> test(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        log.info("用户id：[{}]",verify.getClaim("id").asString());
        log.info("用户名字：[{}]",verify.getClaim("name").asString());
        map.put("code", 200);
        map.put("msg", "请求成功");
        return map;
    }

    /**用户注册接口*/
    @PostMapping("/register")
    public ResponseUtils register(@RequestBody JSONObject jsonObject) {

        String telephone = jsonObject.getString("telephone");
        String password = jsonObject.getString("password");
        String verCode = jsonObject.getString("verCode");
        Long verCodeId = jsonObject.getLong("verCodeId");

        return userService.register(telephone,password,verCode,verCodeId);
    }

    /**用户登录接口(手机号/邮箱+密码)*/
    @PostMapping("/login/password")
    public ResponseUtils Login_Password(@RequestBody JSONObject jsonObject) {

        String teleEmail = jsonObject.getString("tele_email");
        String password = jsonObject.getString("password");

        return userService.Login_Password(teleEmail,password);
    }

    /**用户主页接口*/
    @PostMapping("/information")
    public ResponseUtils information(HttpServletRequest request) {
        //从token中获取id
        String token = request.getHeader("token");

        Long id = Long.valueOf(JWTUtils.getUserId(token));

        return userService.information(id);
    }

}
