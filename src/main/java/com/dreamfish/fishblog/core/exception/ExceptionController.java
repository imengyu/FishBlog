package com.dreamfish.fishblog.core.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public ErrorResponseEntity error(Exception ex){
        ex.printStackTrace();
        return new ErrorResponseEntity(500, "服务暂时不可用 : " + ex.toString());
    }
}