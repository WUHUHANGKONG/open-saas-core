package com.github.wuhuhangkong.infrastructure.filter;

import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysTenant; // 导入实体
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysTenantMapper; // 导入Mapper
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired; // 导入Autowired
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Order(-100)
public class TenantSecurityFilter implements Filter {

    private static final String HEADER_TENANT_ID = "X-Tenant-ID";

    // 注入 Mapper 用于查表
    @Autowired
    private SysTenantMapper sysTenantMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String tenantIdStr = httpRequest.getHeader(HEADER_TENANT_ID);
        String path = httpRequest.getRequestURI();

        // 【新增】白名单放行：注册接口不需要租户ID
        if ("/api/tenants/register".equals(path)) {
            chain.doFilter(request, response);
            return; // 直接结束，不走后面的校验了
        }
        if (StringUtils.hasText(tenantIdStr)) {
            // 1. 尝试解析 ID (防止用户传 "abc" 这种非数字导致报错)
            Long tenantId;
            try {
                tenantId = Long.parseLong(tenantIdStr);
            } catch (NumberFormatException e) {
                httpResponse.sendError(400, "❌ 租户ID格式错误，必须是数字！");
                return;
            }

            // 2. 【核心升级】去数据库查一下，这个 ID 真的存在吗？
            SysTenant tenant = sysTenantMapper.selectById(tenantId);

            if (tenant == null) {
                httpResponse.sendError(403, "🚫 非法入侵：租户ID不存在！");
                return;
            }

            if ("DISABLED".equals(tenant.getStatus())) {
                httpResponse.sendError(403, "🚫 账号已冻结：该租户已被禁用！");
                return;
            }

            // 3. 校验通过，放行
            System.out.println("🛡️ [过滤器] 认证成功，租户: " + tenant.getName());
            TenantContext.setTenantId(tenantIdStr);
        } else {
            // 暂时允许无租户ID访问（比如登录页），具体看业务需求
            System.out.println("⚠️ [过滤器] 警告：无租户ID");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}