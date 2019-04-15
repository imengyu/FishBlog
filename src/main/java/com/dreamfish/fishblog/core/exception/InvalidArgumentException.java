package com.dreamfish.fishblog.core.exception;

public class InvalidArgumentException extends RuntimeException {
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public InvalidArgumentException(String message){
        this.message = message;
    }
}
