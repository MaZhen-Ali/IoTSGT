package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.entity.DeviceLibrary;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ApplicationListMapper extends BaseMapper<ApplicationList> {


    @Select("select * from application_list where flag='是'")  //查询已选系统需求
    List<ApplicationList> findAllSelected();

    @Select("select * from application_list where flag='否'")  //查询未选系统需求
    List<ApplicationList> findAllUnSelected();

    @Select("select * from application_list where intent in  (select task from task_intent)")  //查询
    List<ApplicationList> findNameByIntent();

    @Select("select * from application_list where id in #{ids}")  //按id批量查询
    List<ApplicationList> findUnSelectedById(@Param("ids") List<Integer> ids);  //按推荐id批量查询未选的系统需求

    @Update("update application_list set flag='是' where id = #{id}")  //按id选择系统需求
    boolean selectById(Integer id);

    @Update("update application_list set flag='否' where id = #{id}")  //按id取消选择系统需求
    boolean cancelSelectById(Integer id);
}
