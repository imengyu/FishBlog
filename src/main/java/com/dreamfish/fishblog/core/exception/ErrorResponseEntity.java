package com.dreamfish.fishblog.core.exception;

/**
 * 异常信息模板
 * Created by Donghua.Chen on 2018/7/24.
 */
public class ErrorResponseEntity {

    private boolean success;
    private int code;
    private String message;

    public boolean isSuccess() {
        return success;
    }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public void setCode(int code) { this.code = code; }
    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorResponseEntity(int code, String message) {
        this.code = code;
        this.message = message;
        success = false;
    }
}
