package com.situ.futurestar.core.aspect;

import com.situ.futurestar.core.common.Log;
import com.situ.futurestar.core.dto.LoginDTO;
import com.situ.futurestar.core.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 切面日志：记录贴了 @Log 的方法的入参、耗时、结果
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("@annotation(logAnno)")
    public Object around(ProceedingJoinPoint pjp, Log logAnno) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String method = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        long start = System.currentTimeMillis();
        log.info("[{}] {} 开始 | user={} | 入参={}", logAnno.value(), method, currentUserId(), safeArgs(pjp.getArgs()));
        try {
            Object result = pjp.proceed();
            log.info("[{}] {} 成功 | 耗时={}ms", logAnno.value(), method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            log.warn("[{}] {} 失败 | 耗时={}ms | 异常={}", logAnno.value(), method,
                    System.currentTimeMillis() - start, e.getMessage());
            throw e;
        }
    }

    /** 当前用户id，未登录（如登录接口）时返回 - */
    private String currentUserId() {
        try {
            return String.valueOf(SecurityUtil.getCurrentUserId());
        } catch (Exception e) {
            return "-";
        }
    }

    /** 入参摘要：DTO 走 lombok toString，敏感字段脱敏 */
    private String safeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object arg = args[i];
            if (arg instanceof LoginDTO login) {
                // 密码脱敏
                sb.append("LoginDTO(phone=").append(login.getPhone()).append(", password=***)");
            } else {
                sb.append(arg);
            }
        }
        return sb.append("]").toString();
    }
}
