package com.github.wuhuhangkong.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 保证 equals 方法考虑父类字段
@TableName("product")
public class Product extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Double price;
}