package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.SelectedDevice;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SelectedDeviceMapper extends BaseMapper<SelectedDevice> {



    @Select("select * from selected_device")  //查询所有已选设备信息
    List<SelectedDevice> findAllSelected();
    @Select("select * from device_library where name not in (select name from selected_device)")  //查询所有未选设备信息
    List<SelectedDevice> findAllUnSelected();
    @Update("insert into selected_device(name,type,applicationlist,physical,intent,behavior,deviceinterface) select name,type,applicationlist,physical,intent,behavior,deviceinterface from device_library where id = #{id}")  //按id选择设备
    boolean selectById(Integer id);

    // 根据ID查询设备名称
    @Select("select name from device_library WHERE id = #{id}")
    String findNameById(Integer id);

    @Update("update selected_device set number = #{num} where name = #{name} ")  //按id选择设备
    boolean updateNumById(@Param("name")String name, @Param("num")Integer num);

    @Update("update selected_device set number = 1 where name = #{name} ")  //按id设置设备默认数量为1
    boolean initNum(String name);

    @Delete("delete from selected_device where id = #{id}")  //按id取消选择设备
    boolean removeByName(String name);

    @Select("select name from selected_device")  //查询所有设备名称
    List<String> findDeviceName();

    @Select("select name,number from selected_device")  //查询所有设备名称
    List<Object> findDeviceNameAndNum();

    @Select("select deviceinterface from selected_device")  //查询所有设备名称
    List<String> findInterface();

    @Select("select applicationlist from selected_device")  //查询设备功能列表
    List<Object> findDeviceCapacity();

    @Select("select distinct type from selected_device ")  //查询设备类型，去重
    List<Object> findDeviceType();

    @Select("select name from selected_device where type = #{type}")  //查询一种设备类型的所有设备名称
    List<Object> findDeviceNameByType(String type);

    @Select("select physical from selected_device where name = #{name}")  //查询交互信息，根据设备名称
    List<Object> findPhysicalByName(String name);

    @Select("select intent from selected_device where name = #{name}")  //查询意图（系统能力），根据设备名称
    List<Object> findIntentByName(String name);

    @Select("select behavior from selected_device where name = #{name}")  //查询行为，根据设备名称
    List<Object> findBehaviorByName(String name);

    @Select("select physical from selected_device")  //查询所有物理量
    List<String> findAllPhysical();


    @Insert("insert into selected_device (name) values (#{name})")  //按设备名称添加设备
    void addDeviceByName(String name);
}
