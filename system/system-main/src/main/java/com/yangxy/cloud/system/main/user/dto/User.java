package com.yangxy.cloud.system.main.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/24 17:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String account;
    private String phone;
    private String gender;
    private String city;
    private String email;
    private String password;

}
