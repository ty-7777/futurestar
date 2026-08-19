package com.situ.futurestar.core.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-token-expire}")
    private long accessExpire;
    @Value("${jwt.refresh-token-expire}")
    private long refreshExpire;

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    //生成token
    public  String generateToken(Long userId,String phone){
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("phone",phone)
                .claim("type","access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessExpire*1000))//过期时间两小时
                .signWith(getKey())
                .compact();
    }
    //生成refreshToken
    public  String generateRefreshToken(Long userId,String phone){
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("phone",phone)
                .claim("type","refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+refreshExpire*1000))//过期时间七天
                .signWith(getKey())
                .compact();
    }
    //解析token ，返回userId（解析失败会抛jwtException）
    public  Long  parseUserId(String token){
        Claims claims =Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }
    //取jti也就是token的唯一id
    public String parseJti(String token){
        return Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload().getId();
    }
    //取token的类型：access 或 refresh
    public String parseType(String token){
        Claims claims = Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token).getPayload();
        return claims.get("type", String.class);
    }
    public long getRemainingMills(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date expiration = claims.getExpiration();// 过期时间
        return expiration.getTime() - System.currentTimeMillis();// 过期 - 现在 = 剩余毫秒
    }
}
