package com.situ.futurestar.core.exception;

import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.common.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 * 按异常类型返回对应的业务错误码，并设置匹配的 HTTP 状态码（与 API 文档 1.2 一致）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：使用其携带的错误码 */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        return build(Result.error(e.getCode(), e.getMsg()), e.getCode());
    }

    /** 参数校验异常（@RequestBody 上的校验注解） */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Result<Void>> handleValidation(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "请求参数错误";
        return build(Result.error(ErrorCode.BAD_REQUEST, msg), ErrorCode.BAD_REQUEST);
    }

    /** 参数格式类异常 */
    @ExceptionHandler({MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class,
            HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Result<Void>> handleBadRequest(Exception e) {
        return build(Result.error(ErrorCode.BAD_REQUEST, "请求参数错误"), ErrorCode.BAD_REQUEST);
    }

    /** 无权限 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDenied(AccessDeniedException e) {
        return build(Result.error(ErrorCode.FORBIDDEN, "无权限访问"), ErrorCode.FORBIDDEN);
    }

    /** 资源不存在 */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoResourceFoundException e) {
        return build(Result.error(ErrorCode.NOT_FOUND, "资源不存在"), ErrorCode.NOT_FOUND);
    }

    /** 兜底异常：不向外暴露内部细节，记录日志 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统内部异常", e);
        return build(Result.error(ErrorCode.INTERNAL_ERROR, "服务器内部异常"), ErrorCode.INTERNAL_ERROR);
    }

    /** 按业务码设置响应体与 HTTP 状态码 */
    private ResponseEntity<Result<Void>> build(Result<Void> body, int code) {
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(body);
    }
}
