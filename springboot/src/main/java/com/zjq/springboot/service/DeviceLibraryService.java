package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.DeviceLibrary;
import com.zjq.springboot.mapper.DeviceLibraryMapper;
import org.springframework.stereotype.Service;

@Service
public class DeviceLibraryService extends ServiceImpl<DeviceLibraryMapper, DeviceLibrary>{
}
