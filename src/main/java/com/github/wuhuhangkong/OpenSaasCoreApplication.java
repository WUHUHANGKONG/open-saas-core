package com.github.wuhuhangkong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// ✅ 修正：直接扫描整个项目根路径，包含了 common, infrastructure, interfaces 等所有子包
@ComponentScan("com.github.wuhuhangkong")
@MapperScan("com.github.wuhuhangkong.infrastructure.persistence.mapper")
public class OpenSaasCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenSaasCoreApplication.class, args);
    }
}