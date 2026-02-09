package com.github.wuhuhangkong.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.wuhuhangkong.infrastructure.persistence.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 不需要写任何方法，MyBatis Plus 自带了 CRUD
}