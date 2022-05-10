package com.example.hemahotel.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class JWTUtils {

    private static final String SING = "XIAOSHUANG";

    /**
     * 默认的过期时间，30分钟
     */
    private static final Integer DEFAULT_EXPIRES = 60 * 30;


    /**
     * 生成token:header.payload.singature
     */
    public static String getToken(Map<String, String> map) {

        Calendar instance = Calendar.getInstance();
        // 默认过期时间：30分钟
        instance.add(Calendar.SECOND, DEFAULT_EXPIRES);

        //创建jwt builder
        JWTCreator.Builder builder = JWT.create();

        // payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        String token = builder.withExpiresAt(instance.getTime())  //指定令牌过期时间
                .sign(Algorithm.HMAC256(SING));  // 设置加密算法

        return token;
    }

    /**
     * 验证token  合法性
     */
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }


    /**
     *从token中解析出用户id
     */
    public static String getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("id").asString();
        } catch (JWTDecodeException e) {
            return "0";
        }
    }

}