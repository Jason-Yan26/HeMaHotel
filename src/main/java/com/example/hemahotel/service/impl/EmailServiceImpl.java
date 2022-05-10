package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.VerificationRepository;
import com.example.hemahotel.entity.Verification;
import com.example.hemahotel.service.EmailService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Random;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private VerificationRepository verificationRepository;

    private static final String SYMBOLS = "0123456789";
    /**
     * Math.random生成的是一般随机数，采用的是类似于统计学的随机数生成规则，其输出结果很容易预测，因此可能导致被攻击者击中。
     * 而SecureRandom是真随机数，采用的是类似于密码学的随机数生成规则，其输出结果较难预测，若想要预防被攻击者攻击，最好做到使攻击者根本无法，或不可能鉴别生成的随机值和真正的随机值。
     */
    private static final Random RANDOM = new SecureRandom();

    public static String generateVerCode() {
        char[] nonceChars = new char[6];
        for (int i = 0; i < nonceChars.length; i++) {
            nonceChars[i] = SYMBOLS.charAt(RANDOM.nextInt(nonceChars.length));
        }
        return new String(nonceChars);
    }

    public ResponseUtils sendVerCode(String receiver){

        //生成验证码
        String verCode = generateVerCode();

        SimpleMailMessage simpMsg = new SimpleMailMessage();

        simpMsg.setFrom("459155106@qq.com");//发件人邮箱
        simpMsg.setTo(receiver);//收件人邮箱
        simpMsg.setSubject("【河马酒店】验证码");//主题
        simpMsg.setText("尊敬的用户,您好:\n"
                + "\n您本次请求河马酒店平台进行邮箱绑定的验证码为: " + verCode + " ,本验证码5分钟内有效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封自动发送的邮件，请不要直接回复）");//内容

        javaMailSender.send(simpMsg);//发送邮件

        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        Verification verification = verificationRepository.save(new Verification(verCode,createTime));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("verCodeId",verification.getId());
        log.info("邮箱[{}]验证码发送成功！！",receiver);
        return ResponseUtils.response(200, "验证码发送成功", jsonObject);

    }

}
