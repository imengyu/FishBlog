package com.dreamfish.fishblog.core.config;

import com.dreamfish.fishblog.core.exception.ConstraintViolationErrorResponseEntity;
import com.dreamfish.fishblog.core.exception.ErrorResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    //捕获 ConstraintViolationException 参数 异常
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponseEntity constraintViolationExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        ConstraintViolationException exception = (ConstraintViolationException) e;
        return new ConstraintViolationErrorResponseEntity(HttpStatus.BAD_REQUEST.value(), "请求参数验证失败", exception.getMessage());
    }
    //捕获 FileNotFoundException 参数 异常
    @ExceptionHandler(FileNotFoundException.class)
    public ErrorResponseEntity fileNotFoundExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        FileNotFoundException exception = (FileNotFoundException) e;
        return new ConstraintViolationErrorResponseEntity(HttpStatus.NOT_FOUND.value(), "未找到指定文件", exception.getMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponseEntity httpRequestMethodNotSupportedExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        e.printStackTrace();
        return new ErrorResponseEntity(HttpStatus.BAD_REQUEST.value(), "不支持的请求方法");
    }
    //捕获  RuntimeException 异常
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponseEntity runtimeExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        RuntimeException exception = (RuntimeException) e;
        return new ErrorResponseEntity(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
    @ExceptionHandler(NullPointerException.class)
    public ErrorResponseEntity nullPointerExceptionHandler(HttpServletRequest request, final Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        e.printStackTrace();
        return new ErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务出现异常，请稍后再试");
    }

    /**
     * 通用的接口映射异常处理方法
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (ex instanceof MissingServletRequestParameterException) {
            return new ResponseEntity<>(new ErrorResponseEntity(status.value(), "请求缺少参数"), status);
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return new ResponseEntity<>(new ErrorResponseEntity(status.value(), "不支持的请求方法"), status);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) ex;
            return new ResponseEntity<>(new ErrorResponseEntity(status.value(), exception.getBindingResult().getAllErrors().get(0).getDefaultMessage()), status);
        }
        if (ex instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) ex;
            logger.error("参数转换失败，方法：" + (exception.getParameter().getMethod() != null ? exception.getParameter().getMethod().getName() : "未知方法") + "，参数：" + exception.getName()
                    + ",信息：" + exception.getLocalizedMessage());
            return new ResponseEntity<>(new ErrorResponseEntity(status.value(), "参数 " + exception.getName() + " 不合法"), status);
        }
        ex.printStackTrace();
        return new ResponseEntity<>(new ErrorResponseEntity(status.value(), "错误的请求"), status);
    }
}
