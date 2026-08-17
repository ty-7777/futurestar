package com.situ.futurestar.core.common;

/**
 * 统一错误码（与 API 文档 1.2 一致）
 */
public final class ErrorCode {

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 请求参数错误 */
    public static final int BAD_REQUEST = 400;

    /** 未登录或 Token 无效/过期 */
    public static final int UNAUTHORIZED = 401;

    /** 无权限访问 */
    public static final int FORBIDDEN = 403;

    /** 资源不存在 */
    public static final int NOT_FOUND = 404;

    /** 业务冲突（名额已满、积分不足、重复操作） */
    public static final int CONFLICT = 409;

    /** 请求过于频繁（限流） */
    public static final int TOO_MANY_REQUESTS = 429;

    /** 系统内部错误 */
    public static final int INTERNAL_ERROR = 500;

    private ErrorCode() {
    }
}
