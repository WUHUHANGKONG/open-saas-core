-- 1. 租户表 (全局)
CREATE TABLE IF NOT EXISTS sys_tenant (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'NORMAL',
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );

INSERT INTO sys_tenant (name, status, create_time) VALUES ('小米科技', 'NORMAL', NOW());
INSERT INTO sys_tenant (name, status, create_time) VALUES ('华为技术', 'NORMAL', NOW());
INSERT INTO sys_tenant (name, status, create_time) VALUES ('字节跳动', 'NORMAL', NOW());

-- 2. 用户表 (多租户)
CREATE TABLE IF NOT EXISTS sys_user (
                                        id BIGSERIAL PRIMARY KEY,
                                        username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    tenant_id VARCHAR(50) NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );

-- 给字节跳动(假设ID是3)加个管理员，注意：如果不确定ID，实际生产中需先查后插，这里演示直接插
INSERT INTO sys_user (username, password, email, tenant_id, create_time)
VALUES ('admin', '123456', 'admin@byte.com', '3', NOW());

-- 3. 商品表 (业务表)
CREATE TABLE IF NOT EXISTS product (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP
    );