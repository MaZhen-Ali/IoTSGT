package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.SharedPhenomenon;
import com.zjq.springboot.mapper.SharedPhenomenonMapper;
import org.springframework.stereotype.Service;

@Service
public class SharedPhenomenonService extends ServiceImpl<SharedPhenomenonMapper, SharedPhenomenon> {

    public boolean saveSSharedPhenomenon(SharedPhenomenon sharedPhenomenon){
        return saveOrUpdate(sharedPhenomenon);
    }
}
