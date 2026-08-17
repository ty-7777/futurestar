package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.PhysicalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PhysicalRecordMapper {
    void insert(PhysicalRecord physicalRecord);
    List<PhysicalRecord> selectListByUserId(Long userId);

     List<PhysicalRecord> trend(@Param("userId") Long userId, @Param("months") int months);
}
