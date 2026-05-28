package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.ApplicationList;
import com.zjq.springboot.mapper.ApplicationListMapper;
import org.springframework.stereotype.Service;

@Service
public class ApplicationListService extends ServiceImpl<ApplicationListMapper, ApplicationList> {
}
