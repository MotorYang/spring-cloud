package com.yangxy.cloud.common.exception;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 08:07
 * 服务异常
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

}
