package com.example.hemahotel.jwt;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptors())
                .addPathPatterns("/user/test")  //用于设置拦截器的过滤路径规则
                .excludePathPatterns("/api/user/login/password")
                .excludePathPatterns("/api/user/register");//用于设置不需要拦截的过滤规则
    }
}
