package com.github.wuhuhangkong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// 强制扫描这两个核心包路径，防止因为层级问题漏掉 Controller
@ComponentScan(basePackages = {
        "com.github.wuhuhangkong.interfaces",
        "com.github.wuhuhangkong.infrastructure"
})
@MapperScan("com.github.wuhuhangkong.infrastructure.persistence.mapper")
public class OpenSaasCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenSaasCoreApplication.class, args);
    }
}