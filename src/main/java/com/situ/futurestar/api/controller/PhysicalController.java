package com.situ.futurestar.api.controller;

import com.situ.futurestar.api.service.PhysicalService;
import com.situ.futurestar.core.common.Log;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.PhysicalRecordDTO;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.vo.PageResult;
import com.situ.futurestar.core.vo.PhysicalTrendVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/physical")
@RequiredArgsConstructor
public class PhysicalController {
    private final  PhysicalService physicalService;
    //录入体能记录
    @Log("录入体能记录")
    @PostMapping
    public Result<Void> recordPhysical(@Valid  @RequestBody PhysicalRecordDTO physicalRecordDTO){
        physicalService.recordPhysical(physicalRecordDTO);
        return Result.success();
    }
    //查询体能记录的历史记录
    @GetMapping
    public  Result<PageResult<PhysicalRecord>>  listPhysicalRecord(
            @RequestParam(defaultValue = "1")  int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        return Result.success(physicalService.listPhysicalRecord(pageNum,pageSize));
    }
    //对近几个月的体能记录做趋势分析
    @GetMapping("/trend")
    public Result<PhysicalTrendVO> trend(@RequestParam(defaultValue = "6") int months){
        return Result.success(physicalService.trend(months));
    }
}
