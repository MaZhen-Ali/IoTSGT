package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.EntityList;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface EntityListMapper extends BaseMapper<EntityList> {

    @Insert("insert into entity_list(entity) values (#{entityName}) ")
    int AddEntity(@Param("entityName")String entityName);

    @Delete("delete from entity_list where entity=#{entityName}")
    int DelEntity(@Param("entityName")String entityName);
}
