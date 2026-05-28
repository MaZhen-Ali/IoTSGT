package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.ContextDiagram;
import com.zjq.springboot.entity.SoftwareRequirement;
import com.zjq.springboot.mapper.SoftwareRequirementMapper;
import org.springframework.stereotype.Service;

@Service
public class SoftwareRequirementService extends ServiceImpl<SoftwareRequirementMapper, SoftwareRequirement> {

    public boolean saveSoftwareRequirement(SoftwareRequirement softwareRequirement){
        return saveOrUpdate(softwareRequirement);
    }
}
