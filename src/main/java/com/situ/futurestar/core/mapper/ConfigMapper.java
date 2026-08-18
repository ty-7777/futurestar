package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfigMapper {

    List<SysConfig> listAll();

    SysConfig getByKey(String key);

    int updateValue(@Param("key") String key, @Param("configValue") String configValue);
}
