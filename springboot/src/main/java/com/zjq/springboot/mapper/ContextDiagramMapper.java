package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.ContextDiagram;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ContextDiagramMapper extends BaseMapper<ContextDiagram> {

    @Select("select * from context_diagram where type='系统-设备'")  //按类型查询
    List<ContextDiagram> selectByType1();

    @Select("select * from context_diagram where type='设备-实体'")  //按类型查询
    List<ContextDiagram> selectByType2();

    @Select("select * from context_diagram where type='意图-实体'")  //按类型查询
    List<ContextDiagram> selectByType3();

    @Update("insert into context_diagram(begin,link,end,type) select name,physical,entity,'设备-实体' as type from application_list where flag='是'")
    boolean updateContextDiagramType2();

    @Update("insert into context_diagram(type,begin,link,end) select '设备-实体',name,physical,entity from application_list where flag='是'")
    boolean updateContextDiagram();

    @Delete("DELETE FROM context_diagram")
    boolean delContextDiagram();

    @Insert("insert into context_diagram(begin,link,end,type) values (#{device}, #{interactiveInformation},#{entity}, '设备-实体')")
    int NoLibAddInfo(@Param("device")String device, @Param("interactiveInformation")String interactiveInformation, @Param("entity")String entity);

}
