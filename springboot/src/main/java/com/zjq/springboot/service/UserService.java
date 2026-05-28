package com.zjq.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.controller.dto.UserDTO;
import com.zjq.springboot.entity.User;
import com.zjq.springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    public boolean saveUser(User user){
//        if (user.getId() == null) {
//            return save(user);  //mybatis-plus提供的方法，表示插入数据
//        }else {
//            return updateById(user);
//        }
        return saveOrUpdate(user);

    }

    public boolean login(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userDTO.getUsername());
        queryWrapper.eq("password",userDTO.getPassword());
        User one = getOne(queryWrapper);
        return one != null;
    }

//    @Autowired
//    private UserMapper userMapper;
//
//    public int save(User user){
//        if(user.getId() == null){   //user没有id，则表示是新增
//            return userMapper.insert(user);
//        }else {     //否则为更新
//            return userMapper.update(user);
//        }
//    }

}
