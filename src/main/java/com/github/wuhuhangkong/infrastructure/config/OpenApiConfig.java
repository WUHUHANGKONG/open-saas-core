package com.github.wuhuhangkong.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CityPulse SaaS 核心 API") // 文档标题
                        .version("1.0.0")                 // 版本号
                        .description("基于 Spring Boot 3 + MyBatis Plus 的多租户 SaaS 架构演示项目") // 描述
                        .contact(new Contact()
                                .name("WuhuHangkong")     // 换成你的名字
                                .url("https://github.com/WUHUHANGKONG") // 换成你的 GitHub
                                .email("your-email@example.com")));
    }
}