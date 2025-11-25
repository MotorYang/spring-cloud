package com.yangxy.cloud.common.response;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 05:23
 * <p>
 * 通用API返回结果封装
 */
public class RestResult<T> implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 486463854867486343L;

    private Integer code;
    private String msg;
    private T data;
    private LocalDateTime timestamp;

    public RestResult() {
    }

    public RestResult(Integer code, String msg, T data, LocalDateTime timestamp) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = timestamp;
    }

    // 静态方法来创建成功响应
    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(ResponseStatusEnums.HTTP_OK.getCode(), "请求成功", data, LocalDateTime.now());
    }

    // 静态方法来创建失败响应
    public static <T> RestResult<T> error(String message) {
        return new RestResult<>(ResponseStatusEnums.HTTP_INTERNAL_SERVER_ERROR.getCode(), message, null, LocalDateTime.now());
    }

    // 创建带有状态码的响应
    public static <T> RestResult<T> build(int status, String message, T data) {
        return new RestResult<>(status, message, data, LocalDateTime.now());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
