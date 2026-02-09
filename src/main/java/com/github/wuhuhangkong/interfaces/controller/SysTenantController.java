package com.github.wuhuhangkong.interfaces.controller;

import com.github.wuhuhangkong.infrastructure.persistence.entity.SysTenant;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysTenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation; // 导入这个
import io.swagger.v3.oas.annotations.tags.Tag;    // 导入这个

import java.util.HashMap;
import java.util.Map;

@Tag(name = "1. 租户管理", description = "企业的注册、审核与管理") // ✨ 整个类的标签
@RestController
@RequestMapping("/api/tenants")
public class SysTenantController {

    @Autowired
    private SysTenantMapper sysTenantMapper;

    /**
     * 注册新租户接口
     * 请求体示例：{"name": "字节跳动"}
     */
    @Operation(summary = "注册新租户", description = "无需登录，直接注册一个新的公司") // ✨ 接口说明
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params){
        String name = params.get("name");

        // 1. 简单的校验
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("公司名称不能为空");
        }

        // 2. 创建租户对象
        SysTenant tenant = new SysTenant();
        tenant.setName(name);
        tenant.setStatus("NORMAL"); // 默认为正常状态

        // 注意：这里不需要设置 id，数据库 Auto Increment 会自动生成
        // createTime 和 updateTime 也会由 MyMetaObjectHandler 自动填充

        // 3. 插入数据库
        sysTenantMapper.insert(tenant);

        // 4. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("msg", "🎉 恭喜！租户开通成功");
        result.put("tenantId", tenant.getId()); // 返回生成的 ID，这是最重要的！
        result.put("companyName", tenant.getName());

        return result;
    }
}