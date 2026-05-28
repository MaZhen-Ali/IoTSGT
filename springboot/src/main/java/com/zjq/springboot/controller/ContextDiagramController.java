package com.zjq.springboot.controller;

import com.zjq.springboot.controller.dto.InteractiveInfoDTO;
import com.zjq.springboot.entity.ContextDiagram;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.ContextDiagramMapper;
import com.zjq.springboot.service.ContextDiagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contextDiagram")
public class ContextDiagramController {
    @Autowired
    private ContextDiagramService contextDiagramService;
    @Autowired
    private ContextDiagramMapper contextDiagramMapper;

    //查询所有数据
    @GetMapping
    public List<ContextDiagram> findAll(){
        return contextDiagramService.list();
    }

    //新增和修改
    @PostMapping
    public boolean save(@RequestBody ContextDiagram contextDiagram){
        //新增或者更新
        //return taskIntentService.saveTaskIntent(taskIntent);
        contextDiagramService.saveContextDiagram(contextDiagram);
        return contextDiagramService.saveContextDiagram(contextDiagram);
    }



    @PostMapping("/update/type2")
    public boolean updateContextDiagramType2(){
        return contextDiagramMapper.updateContextDiagramType2();
    }

    //按类型查询数据
    @GetMapping("/select/type1")
    public List<ContextDiagram> findByType1(){
        return contextDiagramMapper.selectByType1();
    }

    //按类型查询数据
    @GetMapping("/select/type2")
    public List<ContextDiagram> findByType2(){
        return contextDiagramMapper.selectByType2();
    }

    //按类型查询数据
    @GetMapping("/select/type3")
    public List<ContextDiagram> findByType3(){
        return contextDiagramMapper.selectByType3();
    }

    @PostMapping("/update")
    public boolean updateContextDiagram(){
        return contextDiagramMapper.updateContextDiagram();
    }

    @PostMapping("/del")
    public boolean delContextDiagram(){
        return contextDiagramMapper.delContextDiagram();
    }


    @PostMapping("/NoLibAddInfo")
    public int NoLibAddInfo(@RequestBody InteractiveInfoDTO interactiveInfoDTO){
        return contextDiagramMapper.NoLibAddInfo(interactiveInfoDTO.getDevice(),interactiveInfoDTO.getInteractiveInformation(),interactiveInfoDTO.getEntity());
    }

    @DeleteMapping("/NoLibDelInfo/{id}")
    public boolean NoLibDelInfo(@PathVariable Integer id){
        return contextDiagramService.removeById(id);
    }

}
