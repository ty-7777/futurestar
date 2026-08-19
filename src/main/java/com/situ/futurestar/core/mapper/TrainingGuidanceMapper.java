package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.TrainingGuidance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrainingGuidanceMapper {
    void insert(TrainingGuidance g);

    int existToday(@Param("userId") Long userId, @Param("type") String type);
}
