package com.yangxy.cloud.common.exception;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 08:07
 * 服务异常
 */
public class BusinessException extends RuntimeException {

    private int code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
