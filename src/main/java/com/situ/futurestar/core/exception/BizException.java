package com.situ.futurestar.core.exception;
import com.situ.futurestar.core.common.ErrorCode;


/**
 * 业务异常
 * 可携带业务错误码，默认 400（请求参数错误）
 */
public class BizException extends RuntimeException {
    private final int code;
    private final String msg;

    public BizException(String msg) {
        this(ErrorCode.BAD_REQUEST, msg);
    }

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
