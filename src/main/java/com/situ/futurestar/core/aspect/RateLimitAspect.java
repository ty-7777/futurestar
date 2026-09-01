package com.situ.futurestar.core.aspect;

import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.common.RateLimit;
import com.situ.futurestar.core.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 接口限流切面：拦截贴了 @RateLimit 的方法，按「IP + 方法」维度执行 Redis 滑动窗口限流
 * <p>
 * 复用 limit.lua（ZSet 滑动窗口）：超限返回 0，未超限由脚本内 ZADD 计数并放行。
 * 切面只做「决定放不放行」，业务方法本体不感知限流存在。
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT;

    static {
        RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();
        RATE_LIMIT_SCRIPT.setLocation(new ClassPathResource("limit.lua"));
        RATE_LIMIT_SCRIPT.setResultType(Long.class);
    }

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // key = rate:{类名.方法名}:{客户端IP}：方法维度隔离，避免不同接口互相干扰限流
        String key = "rate:" + signature.getDeclaringType().getSimpleName()
                + "." + signature.getName() + ":" + clientIp();

        Long allowed = stringRedisTemplate.execute(RATE_LIMIT_SCRIPT, List.of(key),
                String.valueOf(rateLimit.window() * 1000L),   // ARGV[1] 窗口（毫秒）
                String.valueOf(rateLimit.limit()),            // ARGV[2] 最大次数
                String.valueOf(System.currentTimeMillis()));  // ARGV[3] 当前时间戳

        if (allowed == null || allowed == 0) {
            log.warn("接口限流触发：{}", key);
            throw new BizException(ErrorCode.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
        }
        return pjp.proceed();
    }

    /** 客户端 IP：优先取代理转发头，取不到回退 remoteAddr；非 Web 上下文（如定时任务）返回 unknown */
    private String clientIp() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "unknown";
        }
        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            // 多级代理时第一个 IP 是最初客户端的真实 IP
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
