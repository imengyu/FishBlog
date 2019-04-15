package com.dreamfish.fishblog.core.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通用返回值类
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String code;
    private String extendCode;
    private String message;

    private T data;

    public boolean isSuccess() { return success; }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getExtendCode() { return extendCode; }
    public T getData() { return data; }

    public void setData(T data) { this.data = data; }
    public void setMessage(String message) { this.message = message; }
    public void setCode(String code) { this.code = code; }
    public void setExtenedCode(String extendCode) { this.extendCode = extendCode; }
    public void setSuccess(boolean success) { this.success = success; }


    public static Result success() {
        return Result.success(null);
    }
    public static <T> Result success(T obj) {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCodeEnum.OK.getCode());
        result.setMessage(ResultCodeEnum.OK.getMsg());
        if (obj == null) {
            // 若返回数据为null 统一返回给前端{}
            result.setData(null);
        } else {
            result.setData(obj);
        }
        return result;
    }
    public static <T> Result success(T obj, String message) {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCodeEnum.OK.getCode());
        result.setMessage(message);
        if (obj == null) {
            // 若返回数据为null 统一返回给前端{}
            result.setData(new HashMap<>(1));
        } else {
            result.setData(obj);
        }
        return result;
    }

    public static Result failure(ResultCodeEnum resultCodeEnum) {
        return Result.failure(resultCodeEnum.getCode(), resultCodeEnum.getMsg());
    }
    public static Result failure(ResultCodeEnum resultCodeEnum, String extenedCode) {
        return Result.failure(resultCodeEnum.getCode(), resultCodeEnum.getMsg(), extenedCode);
    }
    public static Result failure(String code, String msg) {
        return Result.failure(code, msg, null);
    }
    public static Result failure(String code, String msg, String extenedCode) {
        Result result = new Result();
        result.setData(null);
        result.setExtenedCode(extenedCode);
        result.setCode(code);
        result.setSuccess(false);
        result.setMessage(msg);
        return result;
    }
    public static Result failure(Throwable e, ResultCodeEnum resultCodeEnum) {
        return failure(e, resultCodeEnum.getCode(), resultCodeEnum.getMsg(), e.getMessage());
    }
    public static <T> Result failure(T obj, String code, String msg) {
        return failure(obj, code, msg, null);
    }
    public static <T> Result failure(T obj, String code, String msg, String extendCode) {
        Result result = new Result();
        result.setData(obj);
        result.setCode(code);
        result.setExtenedCode(extendCode);
        result.setSuccess(false);
        result.setMessage(msg);
        return result;
    }
    public static Result failure(BindingResult br) {
        if (null != br && br.hasErrors()) {
            Map<String, String> map = new HashMap(16);
            List<FieldError> list = br.getFieldErrors();
            Iterator var3 = list.iterator();
            StringBuilder s = new StringBuilder();
            while (var3.hasNext()) {
                FieldError error = (FieldError) var3.next();
                map.put(error.getField(), error.getDefaultMessage());
                s.append(error.getDefaultMessage()).append("，");
            }
            if (s.length() > 0) {
                s.deleteCharAt(s.length() - 1);
            }
            return failure(map, ResultCodeEnum.PARAM_ERROR.getCode(), s.toString());
        } else {
            return failure(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
