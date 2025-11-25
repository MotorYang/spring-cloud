package com.yangxy.cloud.common.response;

/**
 * @author MotorYang
 * @email motoyangxy@outlook.com
 * @date 2025/11/25 05:27
 * 响应状态枚举
 */
public enum ResponseStatusEnums {

    HTTP_OK(200, "OK"),
    HTTP_BAD_REQUEST(400, "Bad Request"),
    HTTP_UNAUTHORIZED(401, "Unauthorized"),
    HTTP_FORBIDDEN(403, "Forbidden"),
    HTTP_NOT_FOUND(404, "Not Found"),
    HTTP_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    HTTP_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    HTTP_NOT_IMPLEMENTED(501, "Not Implemented"),
    HTTP_BAD_GATEWAY(502, "Bad Gateway"),
    HTTP_SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    HTTP_GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private final int code;
    private final String message;

    ResponseStatusEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
