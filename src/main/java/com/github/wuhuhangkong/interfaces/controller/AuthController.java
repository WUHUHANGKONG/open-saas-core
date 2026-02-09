package com.github.wuhuhangkong.interfaces.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.wuhuhangkong.common.JwtUtil;
import com.github.wuhuhangkong.common.R;
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysUser;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // 导入 RequestHeader

import java.util.HashMap;
import java.util.Map;

@Tag(name = "2. 认证中心", description = "用户登录与Token获取")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "用户登录", description = "需要携带 Tenant-ID 才能登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(
            // 🌟 核心修改：直接把 Header 加到方法参数里
            // Knife4j 一定会渲染这个，而且 Spring 会自动校验它不能为 null
            @Parameter(description = "租户ID", required = true, example = "3")
            @RequestHeader("X-Tenant-ID") String tenantId,

            @RequestBody Map<String, String> params
    ) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            throw new IllegalArgumentException("❌ 用户名或密码不能为空");
        }

        // 1. 构造查询条件
        // 注意：虽然 Filter 已经设置了 TenantContext，但在登录接口
        // 我们也可以显式地用参数里的 tenantId 做双重验证，或者直接信任 Filter 里的 Context
        // 这里 MybatisPlus 依然会去读 TenantContext
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);

        // 2. 查询用户
        SysUser user = sysUserMapper.selectOne(wrapper);

        // 3. 校验逻辑
        if (user == null) {
            throw new IllegalArgumentException("❌ 登录失败：该租户下未找到此用户");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("❌ 登录失败：密码错误");
        }

        // 4. 生成 Token
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("token", jwtUtil.generateToken(user.getId(), user.getUsername(), user.getTenantId()));

        return R.ok(result);
    }
}