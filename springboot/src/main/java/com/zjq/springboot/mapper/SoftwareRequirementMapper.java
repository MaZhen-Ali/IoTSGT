package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.SoftwareRequirement;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SoftwareRequirementMapper extends BaseMapper<SoftwareRequirement> {

    @Select("select * from software_requirement where flag='是'")  //查询已选软件需求
    List<SoftwareRequirement> findAllSelected();
    @Update("update software_requirement set flag='是' where id = #{id}")  //按id选择软件需求
    boolean selectById(Integer id);

    @Update("update software_requirement set flag='否' where id = #{id}")  //按id取消选择软件需求
    boolean cancelSelectById(Integer id);

    //插入软件需求
    @Insert("insert into software_requirement(description, flag) values(#{description}, #{flag})")
    boolean insertRequirement(@Param("description")String description, @Param("flag")String flag);

    @Update("update software_requirement set description=#{description} where id = #{id}")  //按id取消选择软件需求
    boolean editSoftwareRequirement(@Param("description")String description, @Param("id")int id);
}
