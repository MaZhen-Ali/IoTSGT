package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.SystemReq;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface SystemReqMapper extends BaseMapper<SystemReq> {

    @Insert("insert into system_requirement(requirement,description) values (#{userInput},#{systemRequirement}) ")
    int AddSystemRequirement(@Param("userInput")String requirement,@Param("systemRequirement")String description);



}
