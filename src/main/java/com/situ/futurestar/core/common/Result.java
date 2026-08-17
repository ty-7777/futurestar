package com.situ.futurestar.core.common;

import lombok.Data;

/**
 * 统一响应对象
 * 字段与 API 文档 1.1 一致：code / message / data
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ErrorCode.SUCCESS);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return Result.success(null);
    }

    public static <T> Result<T> error(String message) {
        return error(ErrorCode.BAD_REQUEST, message);
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
