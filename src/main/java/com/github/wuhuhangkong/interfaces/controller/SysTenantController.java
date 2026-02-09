package com.github.wuhuhangkong.interfaces.controller;

import com.github.wuhuhangkong.common.R;
import com.github.wuhuhangkong.infrastructure.context.TenantContext; // 导入上下文
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysTenant;
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysUser; // 导入用户实体
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysTenantMapper;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysUserMapper; // 导入用户Mapper
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; // 导入事务
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

    @Autowired
    private SysUserMapper sysUserMapper; // 注入用户 Mapper

    @Operation(summary = "注册新租户", description = "注册公司并自动创建管理员账号(admin/密码)")
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证同时成功或同时失败
    public R<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String companyName = params.get("companyName");
        String username = params.get("username");
        String password = params.get("password");

        // 1. 简单校验
        if (companyName == null || username == null || password == null) {
            throw new IllegalArgumentException("❌ 信息不完整：公司名、用户名、密码均必填");
        }

        // 2. 创建租户 (SysTenant)
        SysTenant tenant = new SysTenant();
        tenant.setName(companyName);
        tenant.setStatus("NORMAL");
        sysTenantMapper.insert(tenant); // 插入后，tenant.getId() 会有值

        // 3. ⚠️ 重要：临时设置租户上下文
        // 因为 SysUser 是多租户表，MyBatisPlus 会拦截并在 SQL 里强制加上 tenant_id
        // 如果我们不设置上下文，插入用户时 tenant_id 可能会是 null 或报错
        String newTenantId = String.valueOf(tenant.getId());
        TenantContext.setTenantId(newTenantId);

        try {
            // 4. 创建该租户下的管理员 (SysUser)
            SysUser adminUser = new SysUser();
            adminUser.setUsername(username);
            adminUser.setPassword(password); // 实际项目中记得加密！
            adminUser.setRole("ADMIN");      // 标记为管理员
            adminUser.setEmail("admin@" +  tenant.getId() + ".com"); // 模拟个邮箱
            adminUser.setStatus("NORMAL");

            // 这里的 insert 会自动带上 tenant_id = newTenantId
            sysUserMapper.insert(adminUser);

        } finally {
            // 5. ⚠️ 清理上下文 (非常重要，防止影响后续请求)
            TenantContext.clear();
        }

        // 6. 构造返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("tenantId", tenant.getId());
        result.put("companyName", tenant.getName());
        result.put("adminUsername", username);
        result.put("msg", "注册成功！请使用返回的 tenantId + 账号密码登录");

        return R.ok(result);
    }
}