package com.dreamfish.fishblog.core.exception;

public class NoPrivilegeException extends Exception {

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer code;

    public NoPrivilegeException(String message, Integer code){
        super(message);
        this.code=code;
    }
}
