package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.GuestRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.entity.Guest;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl  implements UserService {

    private JSONObject jsonObject;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestRepository guestRepository;

    /**用户注册*/
    //TODO:手机验证码功能还未加入,默认verCode = 123456，verCodeId = 6;
    public ResponseUtils register(String telephone,String password,String verCode,Long verCodeId) {

        if(verCode.equals("123456") && verCodeId == 6L) {
            //如果该手机号已被注册，则不可以继续注册
            if(userRepository.findByPhone(telephone).isPresent())
                return ResponseUtils.response(404,"该手机号已被注册", jsonObject);

            Timestamp createTime = new Timestamp(System.currentTimeMillis());
            Timestamp updateTime = new Timestamp(System.currentTimeMillis());
            User user = new User(telephone, password, telephone, 0, createTime, updateTime);

            userRepository.save(user);

            Optional<User> u = userRepository.findByPhone(telephone);

            //用户注册成功,返回token,前端进入登录状态
            Map<String, String> map = new HashMap<>();
            map.put("id",u.get().getId().toString());
            map.put("phone",u.get().getPhone());
            String Token = JWTUtils.getToken(map);

            jsonObject = new JSONObject();
            jsonObject.put("token",Token);

            return ResponseUtils.response(200,"用户注册成功", jsonObject);
        }
        else{
            return ResponseUtils.response(403,"验证码错误,请重新输入验证码", jsonObject);
        }
    }

    /**用户登录(手机号/邮箱 + 密码）*/
    public ResponseUtils Login_Password(String teleEmail, String password){
        jsonObject = new JSONObject();

        Optional<User> user = userRepository.findByPhoneOrEmail(teleEmail,teleEmail);
        if(user.isPresent()){
            if(user.get().getPassword().equals(password)){
                Map<String, String> map = new HashMap<>();
                map.put("id",user.get().getId().toString());
                map.put("phone",user.get().getPhone());

                System.out.println(map);

                String Token = JWTUtils.getToken(map);

                jsonObject.put("token",Token);

                log.info("用户登录成功,用户id[{}]",user.get().getId());
                return ResponseUtils.response(200,"用户登录成功", jsonObject);
            }
            else{
                log.info("用户登录失败,用户id[{}]",user.get().getId());
                return ResponseUtils.response(403,"用户密码错误", jsonObject);
            }
        }
        else{
                return ResponseUtils.response(401,"用户并未注册账户，请注册后登录平台", jsonObject);
        }
    }

    /** 用户主页 */
    public ResponseUtils information(Long userId){
        jsonObject = new JSONObject();

        Optional<User> u = userRepository.findById(userId);
        //用户存在，返回用户的个人主页信息
        if(u.isPresent()) {
            User user = u.get();
            List<Guest> Guests = guestRepository.findAllByUserIdOrderByUpdateTimeDesc(user.getId());
            jsonObject.put("user",user);
            jsonObject.put("guests", Guests);

            return ResponseUtils.success("用户信息获取成功",jsonObject);
        }
        //用户不存在，返回错误提示信息
        else {
            jsonObject.put("id",userId);
            return ResponseUtils.response(401, "用户不存在", jsonObject);
        }
    }
}
