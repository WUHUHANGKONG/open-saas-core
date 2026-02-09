package com.github.wuhuhangkong.interfaces.controller;

import com.github.wuhuhangkong.common.R; // 引入统一返回类
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysTenant;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysTenantMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "1. 租户管理", description = "企业的注册、审核与管理")
@RestController
@RequestMapping("/api/tenants")
public class SysTenantController {

    @Autowired
    private SysTenantMapper sysTenantMapper;

    @Operation(summary = "注册新租户", description = "无需登录，直接注册一个新的公司，返回生成的 Tenant-ID")
    @PostMapping("/register")
    public R<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String name = params.get("name");

        // 简单校验
        if (name == null || name.trim().isEmpty()) {
            // 直接抛异常，GlobalExceptionHandler 会捕获并返回 R.fail
            throw new IllegalArgumentException("❌ 公司名称不能为空");
        }

        // 创建租户
        SysTenant tenant = new SysTenant();
        tenant.setName(name);
        tenant.setStatus("NORMAL");

        // 插入数据库 (ID自动生成)
        sysTenantMapper.insert(tenant);

        // 构造返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("tenantId", tenant.getId()); // 核心数据
        result.put("companyName", tenant.getName());
        result.put("status", "开通成功");

        // ✅ 使用 R.ok() 包装
        return R.ok(result);
    }
}