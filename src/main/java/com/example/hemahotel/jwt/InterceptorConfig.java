package com.example.hemahotel.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptors())
                //用于设置拦截器的过滤路径规则
                .addPathPatterns("/api/user")
                .addPathPatterns("/api/user/favorite")
                .addPathPatterns("/api/forum/add")
                .addPathPatterns("/api/guest")
                .addPathPatterns("/api/order")
                //用于设置不需要拦截的过滤规则
                .excludePathPatterns("/api/user/login/password")
                .excludePathPatterns("/api/user/register");
    }
}
