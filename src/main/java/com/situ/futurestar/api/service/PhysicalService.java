package com.situ.futurestar.api.service;

import com.situ.futurestar.core.dto.PhysicalRecordDTO;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.vo.PageResult;
import com.situ.futurestar.core.vo.PhysicalTrendVO;
import org.apache.ibatis.annotations.Mapper;

public interface PhysicalService {
    void recordPhysical(PhysicalRecordDTO physicalRecordDTO);

    PageResult<PhysicalRecord> listPhysicalRecord(int pageNum, int pageSize);

    PhysicalTrendVO trend(int months);
}
