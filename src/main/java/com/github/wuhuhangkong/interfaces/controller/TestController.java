package com.github.wuhuhangkong.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import com.github.wuhuhangkong.infrastructure.persistence.entity.Product;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "3. 业务演示", description = "模拟商品/交通数据的增删改查")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProductMapper productMapper;

    @Operation(summary = "查询商品列表", description = "系统会自动根据 Tenant-ID 隔离数据")
    @Parameters({
            @Parameter(name = "X-Tenant-ID", description = "租户ID", in = ParameterIn.HEADER, required = true, example = "10086")
    })
    @GetMapping("/products")
    public List<Product> listProducts() {
        return productMapper.selectList(null);
    }

    @Operation(summary = "新增商品", description = "自动填充租户ID和时间")
    @PostMapping("/products/add")
    public String addProduct(
            @Parameter(description = "商品名称") @RequestParam String name,
            @Parameter(description = "价格") @RequestParam Double price){
        // 【新增调试打印】看看上下文里到底有没有身份ID
        System.out.println("🔍 Controller 收到的租户ID: " + TenantContext.getTenantId());

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        productMapper.insert(product);
        return "✅ 商品已自动保存到租户：" + TenantContext.getTenantId();
    }
}