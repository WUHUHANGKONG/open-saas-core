package com.github.wuhuhangkong.interfaces.controller;

import com.github.wuhuhangkong.infrastructure.context.TenantContext;
import com.github.wuhuhangkong.infrastructure.persistence.entity.Product;
import com.github.wuhuhangkong.infrastructure.persistence.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/products")
    public List<Product> listProducts() {
        // 注意：我们在这里没有写任何 where tenant_id = ... 的逻辑！
        return productMapper.selectList(null);
    }
    @PostMapping("/products/add")
    public String addProduct(@RequestParam String name, @RequestParam Double price) {
        // 【新增调试打印】看看上下文里到底有没有身份ID
        System.out.println("🔍 Controller 收到的租户ID: " + TenantContext.getTenantId());

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);

        productMapper.insert(product);
        return "✅ 商品已自动保存到租户：" + TenantContext.getTenantId();
    }
}