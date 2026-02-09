package com.github.wuhuhangkong.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component; // 👈 必须有这个，Spring 才能扫描到

import java.security.Key;
import java.util.Date;

@Component // 👈 核心：把它标记为 Spring 管理的组件
public class JwtUtil {

    // 生成足够安全的随机密钥 (真实项目中应该配置在 application.properties)
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 过期时间：24小时 (毫秒)
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username, String tenantId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("tenantId", tenantId) // 把租户ID存入 Token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}