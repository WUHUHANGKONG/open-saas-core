package com.github.wuhuhangkong.interfaces.controller;

import com.github.wuhuhangkong.common.R; // 导入我们刚写的统一返回类
import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import com.github.wuhuhangkong.infrastructure.persistence.entity.Product;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
     * 查询所有商品
     * 自动触发多租户隔离，只会查到当前Header里租户的数据
     */
    @Operation(summary = "查询商品列表", description = "系统会自动根据 Tenant-ID 隔离数据")
    @Parameters({
            @Parameter(name = "X-Tenant-ID", description = "租户ID", in = ParameterIn.HEADER, required = true, example = "1")
    })
    @GetMapping("/products")
    public R<List<Product>> listProducts() {
        // selectList(null) 意味着查所有，但 MyBatis Plus 拦截器会自动加 WHERE tenant_id = ...
        List<Product> products = productMapper.selectList(null);

        // 使用 R.ok() 包装返回结果
        return R.ok(products);
    }

    /**
     * 新增商品
     * 自动填充 tenant_id, create_time, update_time
     */
    @Operation(summary = "新增商品", description = "自动填充租户ID和时间，演示异常拦截")
    @PostMapping("/products/add")
    public R<String> addProduct(
            @Parameter(description = "商品名称") @RequestParam String name,
            @Parameter(description = "价格") @RequestParam Double price) {

        // 【新增】模拟业务校验：如果价格是负数，直接抛出异常
        // 全局异常处理器 (GlobalExceptionHandler) 会捕获它，并返回友好的 JSON
        if (price < 0) {
            throw new IllegalArgumentException("❌ 商品价格不能为负数！");
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        // 插入时，MyMetaObjectHandler 会自动填充 tenantId 和时间
        productMapper.insert(product);

        // 返回成功信息
        return R.ok("✅ 商品已自动保存到租户：" + TenantContext.getTenantId());
    }
}