package com.situ.futurestar.core.util;


import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.common.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前登录用户工具类
 */
public final class SecurityUtil {

    /** 取当前登录用户实体（未登录会抛 401） */
    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        return (User) auth.getPrincipal();
    }

    /** 取当前用户ID（最常用） */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private SecurityUtil() {
    }
}