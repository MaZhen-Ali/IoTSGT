package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.SoftwareRequirement;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


public interface SoftwareReqRecommendationMapper extends BaseMapper<SoftwareRequirement> {


    @Select("select * from software_req_recommendation where flag='是'")  //查询已选系统需求
    List<SoftwareRequirement> findAllSelected();
    @Update("update software_req_recommendation set flag='是' where id = #{id}")  //按id选择软件需求
    boolean selectById(Integer id);

    @Update("update software_req_recommendation set flag='否' where id = #{id}")  //按id取消选择软件需求
    boolean cancelSelectById(Integer id);
}