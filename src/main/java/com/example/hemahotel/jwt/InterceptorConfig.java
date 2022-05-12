package com.example.hemahotel.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptors())
                .addPathPatterns("/api/user")//用于设置拦截器的过滤路径规则
                .addPathPatterns("/hello")
                .excludePathPatterns("/api/user/login/password")//用于设置不需要拦截的过滤规则
                .excludePathPatterns("/api/user/register");
    }
}
