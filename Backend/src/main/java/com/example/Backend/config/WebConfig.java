package com.example.Backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String basePath = "file:src/main/resources/static/uploads/";
                // 大頭貼
                registry.addResourceHandler("/api/uploads/**")
                                .addResourceLocations(basePath);

                // 課程封面圖片
                registry.addResourceHandler("/uploads/covers/**")
                                .addResourceLocations(basePath + "covers/");

                // 課程影片
                registry.addResourceHandler("/uploads/media/**")
                                .addResourceLocations(basePath + "media/");

                // 學歷證明
                registry.addResourceHandler("/uploads/TeacherUpload/**")
                                .addResourceLocations(basePath + "TeacherUpload/");

        }
}
