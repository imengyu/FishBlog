package com.dreamfish.fishblog.core.exception;

public class ConstraintViolationErrorResponseEntity extends ErrorResponseEntity {

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    private String error;

    public ConstraintViolationErrorResponseEntity(int code, String message, String error) {
        super(code, message);
        this.error = error;
    }
}
