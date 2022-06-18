package com.example.hemahotel.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.example.hemahotel.dao.VerificationRepository;
import com.example.hemahotel.entity.Verification;
import com.example.hemahotel.service.EmailService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Random;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
@Slf4j
public class VerificationController {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseUtils emailGet(@RequestBody JSONObject jsonObject){

        String receiver = jsonObject.getString("email");

        return emailService.sendVerCode(receiver);
    }

    @PostMapping("/phone")
    public ResponseUtils phoneGet(@RequestBody JSONObject jsonObject){

        String phoneNumber = jsonObject.getString("phoneNumber");
        JSONObject jsonObject1 = new JSONObject();

        try {
            //生成6位验证码
            String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
            //设置超时时间(不必修改)
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            //(不必修改)
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化ascClient，("***"分别填写自己的ID、AccessKey、Secret)
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tMHhhhLYBY1Nt8wjE6N", "30Gl0tJqAxepugbBfN1jFTINzeG9NM");
            //(不必修改)
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
            //(不必修改)
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象(不必修改)
            SendSmsRequest request = new SendSmsRequest();
            //****处填写接收方的手机号码
            request.setPhoneNumbers(phoneNumber);
            //****填写已申请的短信签名
            request.setSignName("河马先生酒店平台");
            //****填写获得的短信模版CODE
            request.setTemplateCode("SMS_243545507");
            //笔者的短信模版中有${code}, 因此此处对应填写验证码
            request.setTemplateParam("{\"code\":\""+verifyCode+"\"}");
            //不必修改
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            Timestamp createTime = new Timestamp(System.currentTimeMillis());
            Verification verification = verificationRepository.save(new Verification(verifyCode,createTime));


            jsonObject1.put("verCodeId",verification.getId());
            log.info("手机号[{}]验证码发送成功！！",phoneNumber);
            return ResponseUtils.response(200, "验证码发送成功", jsonObject1);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.response(401, "验证码发送失败", jsonObject1);
        }
    }

}
