package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectByPhone (String phone);

    User selectById(Long id);
    int insert (User user);
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);

    void updatePoints(@Param("userId") Long userId,@Param("points") int points);
    void decreasePoints(@Param("userId") Long userId,@Param("points") int points);
}
