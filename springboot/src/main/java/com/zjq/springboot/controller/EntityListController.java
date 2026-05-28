package com.zjq.springboot.controller;

import com.zjq.springboot.entity.EntityList;
import com.zjq.springboot.mapper.EntityListMapper;
import com.zjq.springboot.service.EntityListService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entityList")
public class EntityListController {

    @Autowired
    private EntityListService entityListService;
    @Autowired
    private EntityListMapper entityListMapper;


    //查询所有实体名称
    @GetMapping("/findAllEntity")
    public List<EntityList> findAll(){
        return entityListService.list();

    }

    //添加实体名称
    @PostMapping("/addEntity")
    public int addEntity(@RequestBody String entityName){
        // 去掉字符串两端的双引号（如果有的话）
        String entityNameReplace = entityName.replaceAll("^\"|\"$", "");
        return entityListMapper.AddEntity(entityNameReplace);
    }

    //删除实体名称
    @DeleteMapping("/delEntity")
    public int delEntity(@RequestBody Map<String, String> request){

        String entityName = request.get("entityName");
        // 去掉字符串两端的双引号（如果有的话）
        String entityNameReplace = entityName.replaceAll("^\"|\"$", "");
        return entityListMapper.DelEntity(entityNameReplace);
    }

}
