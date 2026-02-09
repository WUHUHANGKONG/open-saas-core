package com.github.wuhuhangkong.infrastructure.filter;

import com.github.wuhuhangkong.common.JwtUtil; // 记得导入你的 JwtUtil
import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import com.github.wuhuhangkong.infrastructure.persistence.entity.SysTenant;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.SysTenantMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Order(-100) // 优先级极高，最先执行
public class TenantSecurityFilter implements Filter {

    private static final String HEADER_TENANT_ID = "X-Tenant-ID";
    private static final String HEADER_AUTH = "Authorization";

    @Autowired
    private SysTenantMapper sysTenantMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // ========================================================================
        // 1. 白名单放行 (注册接口、文档页面、静态资源)
        // ========================================================================
        if (isWhiteList(path)) {
            chain.doFilter(request, response);
            return;
        }

        // ========================================================================
        // 2. 解析租户 ID (优先 Token -> 其次 Header)
        // ========================================================================
        String tenantIdStr = resolveTenantId(httpRequest);

        // ========================================================================
        // 3. 校验租户有效性
        // ========================================================================
        if (StringUtils.hasText(tenantIdStr)) {
            try {
                // 3.1 格式校验
                Long tenantId = Long.parseLong(tenantIdStr);

                // 3.2 数据库查验 (防止伪造ID或访问已禁用的租户)
                SysTenant tenant = sysTenantMapper.selectById(tenantId);

                if (tenant == null) {
                    sendError(httpResponse, 403, "🚫 非法入侵：租户ID不存在！");
                    return;
                }

                if ("DISABLED".equals(tenant.getStatus())) {
                    sendError(httpResponse, 403, "🚫 账号已冻结：该租户已被禁用！");
                    return;
                }

                // 3.3 校验通过，设置上下文
                TenantContext.setTenantId(tenantIdStr);

            } catch (NumberFormatException e) {
                sendError(httpResponse, 400, "❌ 租户ID格式错误");
                return;
            }
        } else {
            // 如果既没有 Token 也没 Header，且不是白名单接口，直接报错
            // 注意：登录接口(/api/auth/login)必须带 X-Tenant-ID Header，否则会进这里
            sendError(httpResponse, 401, "🔒 未授权：请提供租户ID或登录凭证");
            return;
        }

        // ========================================================================
        // 4. 执行后续逻辑 & 清理上下文
        // ========================================================================
        try {
            chain.doFilter(request, response);
        } finally {
            // ⚠️ 极其重要：线程复用时防止数据污染
            TenantContext.clear();
        }
    }

    /**
     * 核心逻辑：从请求中解析 TenantID
     */
    private String resolveTenantId(HttpServletRequest request) {
        // 策略 A: 尝试从 JWT Token 解析
        String authHeader = request.getHeader(HEADER_AUTH);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.parseToken(token);
                // 假设 Token 里存的 key 是 "tenantId" (取决于 JwtUtil 的实现)
                String tenantIdFromToken = claims.get("tenantId", String.class); // 这里可能需要根据实际类型转 String
                if (StringUtils.hasText(tenantIdFromToken)) {
                    return tenantIdFromToken;
                }
            } catch (Exception e) {
                // Token 过期或无效，不报错，降级去读 Header (或者让前端刷新Token)
                System.out.println("⚠️ Token 解析失败: " + e.getMessage());
            }
        }

        // 策略 B: 降级读取 Header (适用于登录接口或无 Token 场景)
        return request.getHeader(HEADER_TENANT_ID);
    }

    /**
     * 判断是否白名单路径 (已增强，防止误伤文档)
     */
    private boolean isWhiteList(String path) {
        return "/api/tenants/register".equals(path) || // 1. 注册接口
                path.startsWith("/doc.html") ||         // 2. Knife4j 主页
                path.startsWith("/swagger-ui") ||       // 3. Swagger 静态资源
                path.startsWith("/v3/api-docs") ||      // 4. Swagger 接口定义
                path.startsWith("/webjars") ||          // 5. 前端依赖 JS/CSS
                path.startsWith("/swagger-resources") ||// 6. ⚠️ 新增：Swagger 资源配置
                path.startsWith("/favicon.ico") ||      // 7. ⚠️ 新增：浏览器图标
                path.equals("/error");                  // 8. ⚠️ 新增：Spring 错误转发 (重要！否则报错会变401)
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        // 返回简单的 JSON 错误
        response.getWriter().write(String.format("{\"code\":%d, \"msg\":\"%s\", \"data\":null}", status, msg));
    }
}