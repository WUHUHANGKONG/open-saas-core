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

-- ----------------------------
-- 4. 交通传感器数据表 (核心业务表)
-- ----------------------------
CREATE TABLE traffic_sensor_data (
                                     id BIGSERIAL PRIMARY KEY,
                                     sensor_id VARCHAR(50) NOT NULL,   -- 传感器/设备编号 (例如: "CAM-001")
                                     location VARCHAR(100),            -- 安装位置 (例如: "中山路-解放路路口")
                                     flow_rate INT,                    -- 当前车流量 (辆/小时)
                                     avg_speed DECIMAL(5, 2),          -- 平均车速 (km/h)
                                     congestion_level INT,             -- 拥堵指数 (1:畅通, 2:缓行, 3:拥堵, 4:严重拥堵)

    -- 核心：租户隔离 (不同城市的数据互不可见)
                                     tenant_id VARCHAR(50) NOT NULL,
                                     create_time TIMESTAMP,
                                     update_time TIMESTAMP
);

-- 插入几条测试数据
INSERT INTO traffic_sensor_data (sensor_id, location, flow_rate, avg_speed, congestion_level, tenant_id, create_time)
VALUES ('CAM-BJ-001', '长安街东口', 1200, 45.5, 2, '1', NOW()); -- 假设 1 是北京