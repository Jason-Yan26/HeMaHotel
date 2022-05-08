package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl  implements UserService {

    private JSONObject jsonObject;

    @Autowired
    private UserRepository userRepository;

    /**用户登录(手机号/邮箱 + 密码）*/
    public ResponseUtils Login_Password(String teleEmail, String password){
        jsonObject = new JSONObject();

        //当用户输入的teleEmail为Phone
        Optional<User> user = userRepository.findByPhone(teleEmail);
        if(user.isPresent()){
            //phone + password 组合在数据库中查询成功
            if(user.get().getPassword().equals(password)){
                Map<String, String> map = new HashMap<>();
                map.put("id",user.get().getId().toString());
                map.put("phone",user.get().getPhone());

                String Token = JWTUtils.getToken(map);

                jsonObject.put("token",Token);
                return ResponseUtils.response(200,"用户登录成功", jsonObject);
            }
            else{
                return ResponseUtils.response(403,"用户密码错误", jsonObject);
            }
        }
        else{
            //当用户输入的teleEmail为Email
            Optional<User> u = userRepository.findByEmail(teleEmail);
            if(u.isPresent()){
                if(u.get().getPassword().equals(password)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", u.get().getId().toString());
                    map.put("phone", u.get().getPhone());

                    String Token = JWTUtils.getToken(map);

                    jsonObject.put("token", Token);
                    return ResponseUtils.response(200, "用户登录成功", jsonObject);
                }
                else{
                    return ResponseUtils.response(403,"用户密码错误", jsonObject);
                }
            }
            else{
                return ResponseUtils.response(401,"用户并未注册账户，请注册后登录平台", jsonObject);
            }
        }

    }
}
