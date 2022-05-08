package com.example.hemahotel.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.hemahotel.jwt.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

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
}
