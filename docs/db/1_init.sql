-- **创建数据库&用户并授权**
CREATE DATABASE cloud
  ENCODING 'UTF8'
  LC_COLLATE='en_US.utf8'
  LC_CTYPE='en_US.utf8';
CREATE USER cloud WITH PASSWORD 'CloudDev';
GRANT ALL PRIVILEGES ON DATABASE cloud TO cloud;
GRANT CREATE ON SCHEMA public TO cloud;

-- **初始化系统服务相关表单**
CREATE TABLE "sys_user"
(
    id       VARCHAR(50) PRIMARY KEY,            -- 使用 UUID 作为主键
    account  VARCHAR(50) NOT NULL,               -- 用户账号，不能为空，最大长度50
    name     VARCHAR(100),                       -- 用户名称，最大长度100
    phone    VARCHAR(20),                        -- 手机号，最大长度20
    gender   VARCHAR(10),                        -- 性别，最大长度10
    city     VARCHAR(100),                       -- 城市，最大长度100
    email    VARCHAR(50),                        -- 邮箱，最大长度50
    password VARCHAR(255) NOT NULL               -- 密码，不能为空，最大长度255
);
CREATE INDEX idx_sys_user_account ON "sys_user" (account);
CREATE INDEX idx_sys_user_phone ON "sys_user" (phone);
CREATE INDEX idx_sys_user_email ON "sys_user" (email);