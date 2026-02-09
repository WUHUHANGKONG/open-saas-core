package com.github.wuhuhangkong.infrastructure.filter;

import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 租户安全过滤器 (保安)
 * 作用：从 HTTP 请求头里抓取 X-Tenant-ID，放入上下文
 */
@Component // 1. 让 Spring 管理这个类
@Order(-100) // 2. 优先级设为很高（负数越小越靠前），保证它比 Spring Security 先执行
public class TenantSecurityFilter implements Filter {

    private static final String HEADER_TENANT_ID = "X-Tenant-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 3. 把普通的 ServletRequest 强转为 HttpServletRequest (为了能取 Header)
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 4. 关键动作：从 Header 里拿身份证
        String tenantId = httpRequest.getHeader(HEADER_TENANT_ID);

        // 5. 如果拿到了，就存进“心脏”里
        if (StringUtils.hasText(tenantId)) {
            System.out.println("🛡️ [过滤器] 识别到租户 ID: " + tenantId); // 打印日志方便调试
            TenantContext.setTenantId(tenantId);
        } else {
            System.out.println("⚠️ [过滤器] 警告：当前请求没有携带 X-Tenant-ID");
        }

        try {
            // 6. 放行！让请求继续去找 Controller
            chain.doFilter(request, response);
        } finally {
            // 7. 【最关键的一步】请求结束，必须清理现场！
            // 无论请求成功还是报错，这一步都会执行。
            TenantContext.clear();
        }
    }
}