package com.github.wuhuhangkong.infrastructure.context;

import java.util.Optional;

/**
 * 租户上下文容器
 * 使用 InheritableThreadLocal 确保子线程也能获取到租户信息（这对异步操作很重要）
 */
public final class TenantContext {

    // 核心容器：每个线程独立存储一份 TenantID
    private static final ThreadLocal<String> TENANT_HOLDER = new InheritableThreadLocal<>();

    private TenantContext() {} // 禁止实例化

    // 设置当前租户
    public static void setTenantId(String tenantId) {
        TENANT_HOLDER.set(tenantId);
    }

    // 获取当前租户 (可能为空)
    public static String getTenantId() {
        return TENANT_HOLDER.get();
    }

    // 强制获取 (如果为空报错，用于必须有租户的场景)
    public static String getRequiredTenantId() {
        String tenantId = getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("错误：当前上下文缺少租户ID！(Did you forget X-Tenant-ID header?)");
        }
        return tenantId;
    }

    // 清理上下文 (防止内存泄漏，非常重要！)
    public static void clear() {
        TENANT_HOLDER.remove();
    }
}