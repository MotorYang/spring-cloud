package com.yangxy.cloud.system.main.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 16:12
 * <p>
 * CREATE TABLE public."user"
 * (
 * id       varchar(50)  PRIMARY KEY,                   -- 使用 UUID 作为主键
 * account  VARCHAR(50)  NOT NULL,                      -- 用户账号，不能为空，最大长度50
 * name     VARCHAR(100),                               -- 用户名称，最大长度100
 * phone    VARCHAR(20),                                -- 手机号，最大长度20
 * gender   VARCHAR(10),                                -- 性别，最大长度10
 * city     VARCHAR(100),                               -- 城市，最大长度100
 * email    varchar(50),                                -- 邮箱，最大长度50
 * password VARCHAR(255) NOT NULL                       -- 密码，不能为空，最大长度255
 * );
 * CREATE INDEX idx_user_username ON public."user" (account);
 * CREATE INDEX idx_user_phone ON public."user" (phone);
 * CREATE INDEX idx_user_email ON public."user" (email);
 */
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class UserEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String account;
    private String phone;
    private String gender;
    private String city;
    private String email;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
