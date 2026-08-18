package com.situ.futurestar.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 静态资源映射：将 /report/** 映射到本地报告上传目录，
 * 使管理端上传的 PDF 报告可通过 report_url 直接访问下载。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String reportDir = Paths.get("data/upload/report/").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/report/**").addResourceLocations(reportDir);
    }
}
