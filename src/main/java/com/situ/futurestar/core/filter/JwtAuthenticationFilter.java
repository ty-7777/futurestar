package com.situ.futurestar.core.filter;


import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private  final JwtUtil jwtUtil;
    private  final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final String BLACK_LIST = "jwt:blacklist:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1.取请求头 Authorization: Bearer <token>
        String header = request.getHeader("Authorization");
        //2.只有带Bearer token 且当前还没登录时才处理
          if((header != null && header.startsWith("Bearer "))
          && SecurityContextHolder.getContext().getAuthentication() == null){
              String token = header.substring(7);
              try {
                  Long userId = jwtUtil.parseUserId(token); // 解析失败会抛异常
                  String jti = jwtUtil.parseJti(token);//拿到这个token的唯一id
                  if(stringRedisTemplate.hasKey(BLACK_LIST+jti)){//判断redis的黑名单里有没有这个token
                      // 这张 access token 被拉黑过 → 不设身份 → 走 401
                      filterChain.doFilter(request,response);
                      return;
                  }
                  User user = userMapper.selectById(userId);
                  if(user!=null){
                      // 3. 把用户身份放进 SecurityContext
                      var authentication = new UsernamePasswordAuthenticationToken(
                              user,                            // principal：放你的实体User，后面Controller直接取
                              null,                            // credentials：已认证，无需密码
                              List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))  // 权限
                      );
                      SecurityContextHolder.getContext().setAuthentication(authentication);
                  }

              } catch (JwtException | IllegalArgumentException e) {
                 //token 无效/过期：不设置身份，走后续的 401
              }

          }
        // 4. 继续走过滤器链
        filterChain.doFilter(request,response);

    }
}
