package com.github.wuhuhangkong.interfaces.controller;

import com.github.wuhuhangkong.common.R;
import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import com.github.wuhuhangkong.infrastructure.persistence.entity.Product;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "3. 业务演示", description = "模拟商品/交通数据的增删改查")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询接口
     */
    @Operation(summary = "查询商品列表", description = "演示：手动传递 Tenant-ID")
    @GetMapping("/products")
    public R<List<Product>> listProducts(
            // ✅ 方式一：非必填 Header (如果你想用 Token 就不用填这个，所以 required=false)
            @Parameter(description = "租户ID (可选, 没Token时必填)", example = "3")
            @RequestHeader(name = "X-Tenant-ID", required = false) String tenantId
    ) {
        // 如果传了 Header，可以手动打印一下看看
        // 但其实 Filter 已经帮你把 ID 放进 Context 里了，这里不需要手动处理
        // System.out.println("当前租户ID: " + TenantContext.getTenantId());

        List<Product> products = productMapper.selectList(null);
        return R.ok(products);
    }

    /**
     * 新增接口
     */
    @Operation(summary = "新增商品", description = "演示：必须传递 Tenant-ID (模拟无Token场景)")
    @PostMapping("/products/add")
    public R<String> addProduct(
            // ✅ 方式二：必填 Header (强迫你在 Knife4j 里填)
            @Parameter(description = "租户ID", required = true, example = "3")
            @RequestHeader("X-Tenant-ID") String tenantId,

            @Parameter(description = "商品名称", example = "MacBook Pro")
            @RequestParam String name,

            @Parameter(description = "价格", example = "12999.00")
            @RequestParam Double price
    ) {
        if (price < 0) {
            throw new IllegalArgumentException("❌ 商品价格不能为负数！");
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        // MyBatis Plus 会自动填充 tenant_id，不需要我们手动 setTenantId(tenantId)
        // 但前提是 Header 里的 ID 已经被 Filter 拦截并放入了 TenantContext
        productMapper.insert(product);

        return R.ok("✅ 商品已自动保存到租户：" + TenantContext.getTenantId());
    }
}