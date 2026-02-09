package com.github.wuhuhangkong.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseEntity {

    // 插入时自动填充
    @TableField(fill = FieldFill.INSERT)
    private String tenantId;

    // 插入时自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 插入和更新时都自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}