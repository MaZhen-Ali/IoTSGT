package com.zjq.springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjq.springboot.controller.dto.UserDTO;
import com.zjq.springboot.entity.User;
import com.zjq.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/user")
public class UserController {

//    @Autowired
//    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    //查询所有数据
    @GetMapping
    public List<User> findAll(){

        return userService.list();
//        List<User> all = userMapper.findAll();
//        return all;
    }

    //登录
    @PostMapping("/login")
    public boolean login(@RequestBody UserDTO userDTO){
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if(StrUtil.isBlank(username) || StrUtil.isBlank(password)){
            return false;

        }
        return userService.login(userDTO);
    }

    //新增和修改
    @PostMapping
    public boolean save(@RequestBody User user){
        //新增或者更新
        return userService.saveUser(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id){  //[1,2,3]

        return userService.removeById(id);
//        return userMapper.deleteById(id);
    }

    @PostMapping ("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids){
        return userService.removeBatchByIds(ids);
    }


    //分页查询
    //接口路径：/user/page?pageNum=1&pageSize=10
    //@RequestParam接收
    //limit第一个参数 = (pageNum - 1) * pageSize
    //pageSize 不变
//    @GetMapping("/page")
//    public Map<String, Object> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
//        pageNum = (pageNum - 1) * pageSize;
//        List<User> data = userMapper.selectPage(pageNum,pageSize);
//        Integer total = userMapper.selectTotal();
//        Map<String, Object> res = new HashMap<>();
//        res.put("data",data);
//        res.put("total",total);
//        return res;
//    }


    //分页查询 - mybatis-plus的方式
    @GetMapping("/page")
    public IPage<User> findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,
                                @RequestParam(defaultValue = "") String username,
                                //前端不传值的话，默认为空
                                @RequestParam(defaultValue = "") String email,
                                @RequestParam(defaultValue = "") String address){
        IPage<User> page = new Page<>(pageNum,pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(!"".equals(username)){
            queryWrapper.like("username",username);
        }
        if(!"".equals(email)){
            queryWrapper.like("email",email);
        }
        if(!"".equals(address)){
            queryWrapper.like("address",address);
        }
        queryWrapper.orderByDesc("id");

        return userService.page(page,queryWrapper);
    }


}
