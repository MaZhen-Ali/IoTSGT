package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.SoftwareRequirement;
import com.zjq.springboot.mapper.SoftwareReqRecommendationMapper;
import org.springframework.stereotype.Service;

@Service
public class SoftwareReqRecommendationService extends ServiceImpl<SoftwareReqRecommendationMapper, SoftwareRequirement> {

    public boolean saveSoftwareReqRecommendation(SoftwareRequirement softwareRequirement){
        return saveOrUpdate(softwareRequirement);
    }

}
