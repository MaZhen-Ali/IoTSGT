package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.SelectedDevice;
import com.zjq.springboot.mapper.SelectedDeviceMapper;
import org.springframework.stereotype.Service;

@Service
public class SelectedDeviceService extends ServiceImpl<SelectedDeviceMapper, SelectedDevice> {

    public boolean saveSelectedDevice(SelectedDevice selectedDevice){
        return saveOrUpdate(selectedDevice);
    }
}
