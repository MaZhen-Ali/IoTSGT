package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

// @Mapper
public interface UserMapper extends BaseMapper<User> {

//    @Select("select * from sys_user")
//    List<User> findAll();
//
//    @Insert("insert into sys_user(username,password,nickname,email,phone,address) " +
//            "values (#{username},#{password},#{nickname},#{email},#{phone},#{address})")
//    int insert(User user);
//
//    @Update("update sys_user set username = #{username},password = #{password},nickname = #{nickname},email = #{email},phone = #{phone},address = #{address} where id = #{id}")
//    int update(User user);
//
//    @Delete("delete from sys_user where id = #{id}")
//    Integer deleteById(@Param("id") Integer id);
//
//    @Select("select * from sys_user limit #{pageNum},#{pageSize}")
//    List<User> selectPage(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize);
//
//    @Select("select count(*) from sys_user")
//    Integer selectTotal();
}
