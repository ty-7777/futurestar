package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {
    int save (RefreshToken refreshToken);

    RefreshToken selectByToken(String refreshToken);
    void deleteByUserId(Long userId);
}
