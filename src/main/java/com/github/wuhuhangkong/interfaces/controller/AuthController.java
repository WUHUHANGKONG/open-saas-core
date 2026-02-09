package com.github.wuhuhangkong.interfaces.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.wuhuhangkong.common.R; // 引入统一返回类
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysUser;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "2. 认证中心", description = "用户登录与Token获取")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Operation(summary = "用户登录", description = "需要携带 Tenant-ID 才能登录，返回用户信息和Token")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            throw new IllegalArgumentException("❌ 用户名或密码不能为空");
        }

        // 1. 构造查询条件 (MyBatis Plus 会自动追加 WHERE tenant_id = ...)
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);

        // 2. 查询用户
        SysUser user = sysUserMapper.selectOne(wrapper);

        // 3. 校验逻辑
        if (user == null) {
            // 抛出异常，GlobalExceptionHandler 会转成 json 返回
            throw new IllegalArgumentException("❌ 登录失败：该租户下未找到此用户");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("❌ 登录失败：密码错误");
        }

        // 4. 登录成功，构造返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        // 模拟 Token (后续可接 JWT)
        result.put("token", "fake-jwt-token-" + System.currentTimeMillis());

        // ✅ 使用 R.ok() 包装
        return R.ok(result);
    }
}