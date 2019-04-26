package com.dreamfish.fishblog.core.exception;

public class BadTokenException extends RuntimeException {
    public BadTokenException(String msg) {
        super(msg);
    }
}
