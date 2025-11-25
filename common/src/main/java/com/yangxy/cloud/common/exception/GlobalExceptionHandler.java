package com.yangxy.cloud.common.exception;

import com.yangxy.cloud.common.response.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 08:08
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public RestResult<Void> handleServiceException(ServiceException e) {
        // 业务预期内的错误，不需要打印堆栈，只记录警告即可
        log.warn("业务阻断: {}", e.getMessage());
        return RestResult.error(e.getMessage());
    }

    /**
     * 拦截所有 RuntimeException (包括你抛出的 "密码错误")
     */
    @ExceptionHandler(RuntimeException.class)
    public RestResult<Void> handleRuntimeException(RuntimeException e) {
        log.error("业务异常: {}", e.getMessage()); // 打印日志，方便排查
        // 将异常的 message (例如 "密码错误") 放入返回结果中
        return RestResult.error(e.getMessage());
    }

    /**
     * 拦截所有未知的 Exception (兜底)
     * 防止出现 NullPointerException 等系统级错误直接暴露给用户
     */
    @ExceptionHandler(Exception.class)
    public RestResult<Void> handleException(Exception e) {
        log.error("系统内部异常", e);
        return RestResult.error("系统内部繁忙，请稍后再试");
    }
}
