package org.gzp.team.geocommerceservice.model.enums;

public enum ServiceErrorCode {
    /**
     * 以下常见错误类型，可根据具体情况扩充新类型
     */
    UNKNOWN_ERROR(1000, "未知异常"),
    NETWORK_ERROR(1001, "网络异常"),
    DATABASE_ERROR(1002, "数据库异常"),
    INVALID_METHOD(1003, "无效方法名"),
    INVALID_PARAMETER(1004, "无效参数名"),
    INVALID_TIMEDATE(1005, "无效时间类型"),
    DATA_ERROR(1006, "无效传入数据"),
    INTERNAL_ERROR(1007, "内部错误"),
    WARN_INVALID(1008, "警告操作无效");

    private int code;
    private String message;

    private ServiceErrorCode(int code, String message) {
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
