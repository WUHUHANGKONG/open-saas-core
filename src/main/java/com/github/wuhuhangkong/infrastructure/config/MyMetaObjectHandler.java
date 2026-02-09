package com.github.wuhuhangkong.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充处理器
 * 作用：在插入或更新时，自动填充租户ID、创建时间、更新时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 1. 自动填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 2. 自动填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // 3. 【核心】自动填充当前租户 ID
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            this.strictInsertFill(metaObject, "tenantId", String.class, tenantId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时，只自动更新“更新时间”
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}