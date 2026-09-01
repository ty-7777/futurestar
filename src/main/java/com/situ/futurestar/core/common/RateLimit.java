package com.situ.futurestar.core.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解：贴在方法上，由 RateLimitAspect 执行 Redis 滑动窗口限流（limit.lua）
 * <p>
 * 限流维度为「客户端IP + 方法」：同一 IP 在窗口内访问同一方法超过 limit 次即被拦截，
 * 不同方法之间互不影响，不同 IP 之间互不影响。
 */
//@RateLimit(limit = ??, window = ??)   // 或者 @RateLimit 用默认值，效果一样
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** 窗口内最大请求数 */
    int limit() default 3;

    /** 窗口大小（秒） */
    int window() default 60;
}
