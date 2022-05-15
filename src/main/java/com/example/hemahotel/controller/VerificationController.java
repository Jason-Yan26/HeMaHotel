package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.service.EmailService;
import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseUtils emailGet(@RequestBody JSONObject jsonObject){

        String receiver = jsonObject.getString("email");

        return emailService.sendVerCode(receiver);
    }

    @PostMapping("/phone")
    public ResponseUtils phoneGet(@RequestBody JSONObject jsonObject){

        String receiver = jsonObject.getString("phone");

        //手机验证码服务还未接入,待后期加入
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("verCodeId",6);
        return ResponseUtils.response(200, "验证码发送成功", jsonObject1);
    }

}
