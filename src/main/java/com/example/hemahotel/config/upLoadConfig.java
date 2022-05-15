package com.example.hemahotel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class upLoadConfig implements WebMvcConfigurer {

    //服务器地址
    @Value("${file.uploadUrl}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 配置资源映射
         * 意思是：如果访问的资源路径是以“/upload/”开头的，
         * 就给我映射到本机的“D:/upload/”这个文件夹内，去找你要的资源
         * 注意：D:/upload/ 后面的 “/”一定要带上
         */
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:"+uploadPath);
    }

}
