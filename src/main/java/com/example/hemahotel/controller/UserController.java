package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.HotelService;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HotelService hotelService;

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
    public ResponseUtils loginPassword(@RequestBody JSONObject jsonObject) {

        String teleEmail = jsonObject.getString("tele_email");
        String password = jsonObject.getString("password");

        return userService.loginPassword(teleEmail,password);
    }

    /**用户主页接口*/
    @PostMapping("/information")
    public ResponseUtils information(HttpServletRequest request) {
        //从token中获取id
        String token = request.getHeader("token");

        Long id = Long.valueOf(JWTUtils.getUserId(token));

        return userService.information(id);
    }

    @PostMapping("/password")
    public ResponseUtils passwordModify(@RequestBody JSONObject jsonObject,HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        String oldPassword = jsonObject.getString("oldPassword");
        String newPassword = jsonObject.getString("newPassword");

        return userService.passwordModify(id,oldPassword,newPassword);
    }


    //用户修改个人资料接口
    @PostMapping("/information/modify")
    public ResponseUtils informationModify(@RequestBody JSONObject jsonObject,HttpServletRequest request) {

        //从token中获取id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        String username = jsonObject.getString("username");
        Integer gender = jsonObject.getInteger("gender");
        Date birthDate = new java.sql.Date(jsonObject.getDate("birth_date").getTime());
        Integer age = jsonObject.getInteger("age");

        String signature = jsonObject.getString("signature");
        String preferenceLabel = jsonObject.getString("preference_label");

        String address = jsonObject.getString("address");
        String email = jsonObject.getString("email");
        String phone = jsonObject.getString("phone");

        return userService.informationModify(id,username,gender,birthDate,age,signature,
                preferenceLabel,address,email,phone);
    }

    @PostMapping("/avatar/upload")
    public ResponseUtils avatarUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request){

        //从token中获取id
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));

        //url前缀：协议://ip地址:端口号/
        String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        return userService.avatarUpload(id,file,urlPrefix);
    }

    @PostMapping("/comment/make")
    public ResponseUtils MakeComments(@RequestBody JSONObject jsonObject, HttpServletRequest request){

        //获取用户相关信息
        String token = request.getHeader("token");
        Long id = Long.valueOf(JWTUtils.getUserId(token));


        //获取酒店相关信息
        String comment = jsonObject.getString("comment");
        Long hotelId = jsonObject.getLong("hotelId");
        int star = jsonObject.getInteger("star") == null ? 5 : jsonObject.getInteger("star");

        return hotelService.CreateComment(id,comment,hotelId,star);
    }
}
