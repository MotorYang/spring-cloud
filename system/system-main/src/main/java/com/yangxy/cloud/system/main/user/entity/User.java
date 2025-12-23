package com.yangxy.cloud.system.main.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_user")
public class User {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String gender;
    private String city;
    private String email;
    private String password;
    @TableLogic(value = "0", delval = "1")
    private int deleted;

    public User() {}

    public User(String id, String username, String nickname, String phone, String gender, String city, String email, String password, int deleted) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.gender = gender;
        this.city = city;
        this.email = email;
        this.password = password;
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
