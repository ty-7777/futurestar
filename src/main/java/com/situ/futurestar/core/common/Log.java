package com.situ.futurestar.core.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法日志注解：贴在方法上，由 LogAspect 记录入参、耗时、结果
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    /** 操作描述，如 "用户登录" */
    String value() default "";
}
