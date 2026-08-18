package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlayerMapper {

    List<User> selectPlayerList(@Param("keyword") String keyword,
                                @Param("position") String position,
                                @Param("status") String status);

    User selectPlayerById(Long id);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int updateLevel(@Param("id") Long id, @Param("memberLevel") String memberLevel);

    List<CourseAppointmentVO> selectAppointmentsByUserId(Long userId);
}
