package com.github.wuhuhangkong.interfaces.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysUser;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        // 1. 构造查询条件
        // 注意：我们不需要写 tenant_id，MyBatis Plus 会自动帮我们加！
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);

        // 2. 查询用户
        SysUser user = sysUserMapper.selectOne(wrapper);

        Map<String, Object> result = new HashMap<>();

        // 3. 校验逻辑
        if (user == null) {
            // 如果查不到，说明该租户下没有这个用户
            throw new RuntimeException("登录失败：用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("登录失败：密码错误");
        }

        // 4. 登录成功
        result.put("msg", "登录成功！欢迎回来，" + user.getUsername());
        result.put("userId", user.getId());
        // 在真实项目中，这里应该返回一个 JWT Token
        result.put("token", "fake-jwt-token-" + System.currentTimeMillis());

        return result;
    }
}