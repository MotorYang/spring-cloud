package com.yangxy.cloud.security.vo;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 08:18
 */
public class LoginRequest {

    private String account;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
