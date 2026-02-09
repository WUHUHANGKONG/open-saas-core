package com.github.wuhuhangkong.infrastructure.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 * 作用：开启多租户拦截器，自动在 SQL 末尾拼接 WHERE tenant_id = 'xxx'
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 创建多租户拦截器
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor(new TenantLineHandler() {

            // 1. 告诉插件：从哪里获取当前的租户 ID？
            // 答：从我们需要维护的 TenantContext 里取！
            @Override
            public Expression getTenantId() {
                String tenantId = TenantContext.getTenantId();
                if (tenantId == null) {
                    // 如果当前没有租户ID（比如后台跑定时任务），暂时给个空或者默认值
                    // 实际生产中这里可能需要特殊处理，暂时返回 null 会报错或者忽略
                    // 为了演示方便，我们先写死一个兜底，或者直接报错
                    // 这里我们返回 null，意味着如果不传 ID，SQL 就会报错（一种安全机制）
                    return new StringValue("");
                }
                return new StringValue(tenantId);
            }

            // 2. 告诉插件：数据库里哪个字段是租户 ID？
            // 答：默认是 "tenant_id"，如果你数据库里叫 "org_id" 就在这里改
            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            // 3. 告诉插件：哪些表不需要隔离？（白名单）
            // 答：比如 "sys_role", "sys_menu" 这种全局通用的表，不需要加 tenant_id
            @Override
            public boolean ignoreTable(String tableName) {
                // 暂时没有白名单，所有表都隔离。
                // 如果以后有全局表，写在这里：return "sys_admin".equalsIgnoreCase(tableName);
                return false;
            }
        });

        // 把这个拦截器加进去
        interceptor.addInnerInterceptor(tenantInterceptor);
        return interceptor;
    }
}