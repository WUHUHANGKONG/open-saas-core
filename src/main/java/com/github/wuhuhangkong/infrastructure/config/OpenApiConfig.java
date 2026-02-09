package com.github.wuhuhangkong.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CityPulse SaaS 核心 API")
                        .version("1.0.0")
                        .description("基于 Spring Boot 3 + MyBatis Plus 的多租户 SaaS 架构演示项目")
                        .contact(new Contact().name("WuhuHangkong").url("https://github.com/WUHUHANGKONG")))
                // 配置全局鉴权 (Token 小锁)
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("BearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    // ✅ 已删除：全局添加 X-Tenant-ID 的代码，改为在 AuthController 中单独控制
}