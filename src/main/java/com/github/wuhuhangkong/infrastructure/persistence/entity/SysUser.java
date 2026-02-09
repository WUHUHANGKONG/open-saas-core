package com.github.wuhuhangkong.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity { // 继承它，自动拥有租户隔离能力
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String email;
}