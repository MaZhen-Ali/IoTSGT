package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.DeviceLibrary;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface DeviceLibraryMapper extends BaseMapper<DeviceLibrary> {


    @Select("select name from device_library")  //查询所有设备名称
    List<Object> findDeviceName();

    @Select("select * from device_library where flag='是'")  //查询已选设备信息
    List<DeviceLibrary> findAllSelected();

    @Select("select * from device_library where flag='否'")  //查询未选设备信息
    List<DeviceLibrary> findAllUnSelected();

    @Select("select physical from device_library")  //查询所有物理量
    List<String> findAllPhysical();

    @Select("select * from device_library where id in #{ids}")  //按id查询
    List<DeviceLibrary> findAllById(@Param("ids") List<Integer> ids);

    @Update("update device_library set flag='否' where id in #{ids}")  //批量按id选择设备
    boolean removeBatchByIds(List<Integer> ids);

    @Update("update device_library set flag='是' where id = #{id}")  //按id选择设备
    boolean selectById(Integer id);

    @Update("update device_library set flag='否' where id = #{id}")  //按id取消选择设备
    boolean cancelSelectById(Integer id);

    @Select("select applicationlist from device_library")  //查询设备功能列表
    List<Object> findDeviceCapacity();

    @Select("select name,applicationlist from device_library")  //查询设备名称、设备功能列表
    List<Object> findDeviceNameAndCapacity();


    @Select("select distinct type from device_library ")  //查询设备类型，去重
    List<Object> findDeviceType();

    @Select("select name from device_library where type = #{type}")  //查询一种设备类型的所有设备名称
    List<Object> findDeviceNameByType(String type);

    @Select("select physical from device_library where name = #{name}")  //查询交互信息，根据设备名称
    List<Object> findPhysicalByName(String name);

    @Select("select intent from device_library where name = #{name}")  //查询意图（系统能力），根据设备名称
    List<Object> findIntentByName(String name);

    @Select("select behavior from device_library where name = #{name}")  //查询行为，根据设备名称
    List<Object> findBehaviorByName(String name);


    @Select("select deviceinterface from device_library")  //查询设备接口
    List<Object> findDeviceInterface();

}
