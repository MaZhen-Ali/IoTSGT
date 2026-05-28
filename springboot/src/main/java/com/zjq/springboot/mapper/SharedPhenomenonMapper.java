package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.SharedPhenomenon;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SharedPhenomenonMapper extends BaseMapper<SharedPhenomenon> {

    @Select("select * from system_requirement where similarity > #{similarity} order by task,similarity desc")  //查询，根据相似度阈值
    List<SharedPhenomenon> selectBySimilarity(Double similarity);
}
