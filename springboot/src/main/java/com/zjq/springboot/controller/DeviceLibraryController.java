package com.zjq.springboot.controller;

import com.zjq.springboot.entity.DeviceLibrary;
import com.zjq.springboot.mapper.DeviceLibraryMapper;
import com.zjq.springboot.service.DeviceLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/deviceLibrary")
public class DeviceLibraryController {


    @Autowired
    private DeviceLibraryService deviceLibraryService;
    @Autowired
    private DeviceLibraryMapper deviceLibraryMapper;

    //查询所有数据
    @GetMapping
    public List<DeviceLibrary> findAll(){
        return deviceLibraryService.list();
    }

    //查询已选设备
    @GetMapping("/selected")
    public List<DeviceLibrary> findAllSelected(){
        return deviceLibraryMapper.findAllSelected();
    }

    //查询未选设备
    @GetMapping("/unselected")
    public List<DeviceLibrary> findAllUnSelected(){
        return deviceLibraryMapper.findAllUnSelected();
    }

    //查询所有物理量
    @GetMapping("/physical")
    public List<String> findAllPhysical(){
        return deviceLibraryMapper.findAllPhysical();
    }

    //按id选择设备
    @PostMapping("/select/{id}")
    public boolean selectDevice(@PathVariable Integer id){  //[1,2,3]

        return deviceLibraryMapper.selectById(id);
    }

    //按id取消选择设备
    @PostMapping("/cancelSelect/{id}")
    public boolean cancelSelectDevice(@PathVariable Integer id){  //[1,2,3]

        return deviceLibraryMapper.cancelSelectById(id);
    }

    //有大问题！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    @PostMapping("/select/batch")
    public boolean selectBatch(@RequestBody List<Integer> ids){

        return true;

//        return deviceLibraryService.updateBatchById(ids);

//        return deviceLibraryMapper.removeBatchByIds(ids);
    }

    @GetMapping("/reqRecommendation")
    public List<Object> reco(){
        List<Object> deviceTypeList = deviceLibraryMapper.findDeviceType();
        List<Object> reco = new ArrayList<>();
        if(deviceTypeList.contains("采集器")) reco.add("数据采集、设备初始化、故障诊断");
        if(deviceTypeList.contains("执行器")) reco.add("控制计算、设备初始化、故障诊断");
        if(deviceTypeList.contains("控制器")) reco.add("控制计算、遥控处理");
        return reco;
    }
}
