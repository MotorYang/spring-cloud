-- ===============================
-- 1️⃣ 用 postgres 用户创建数据库和应用用户
-- ===============================
-- 注意：执行此部分 SQL 时，JDBC 或 psql 需使用 postgres 用户
CREATE DATABASE cloud
  ENCODING 'UTF8'
  LC_COLLATE='en_US.utf8'
  LC_CTYPE='en_US.utf8';

CREATE USER cloud WITH PASSWORD 'CloudDev';

-- 授权 cloud 用户连接数据库
GRANT CONNECT ON DATABASE cloud TO cloud;

-- ===============================
-- 2️⃣ 用 cloud 用户连接数据库创建 schema、表和索引
-- ===============================

-- 2.1 创建 schema 并授权
CREATE SCHEMA IF NOT EXISTS system AUTHORIZATION cloud;

-- 授权 cloud 用户在 schema 中使用和创建对象
GRANT USAGE, CREATE ON SCHEMA system TO cloud;

-- 设置未来新建表默认权限（增删改查）
ALTER DEFAULT PRIVILEGES IN SCHEMA system
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO cloud;

-- 2.2

-- ===============================
-- ✅ 初始化完成
-- cloud 用户可以操作 system.sys_user 表，并且未来新表也继承权限
-- ===============================

-- ===============================
-- 开始创建业务表单
-- ===============================

-- sys_user 表
CREATE TABLE system.sys_user
(
    id       VARCHAR(50) PRIMARY KEY,
    account  VARCHAR(50) NOT NULL,
    name     VARCHAR(100),
    phone    VARCHAR(20),
    gender   VARCHAR(10),
    city     VARCHAR(100),
    email    VARCHAR(50),
    password VARCHAR(255) NOT NULL
);
CREATE INDEX idx_sys_user_account ON system.sys_user (account);
CREATE INDEX idx_sys_user_phone   ON system.sys_user (phone);
CREATE INDEX idx_sys_user_email   ON system.sys_user (email);