package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.SystemReq;
import com.zjq.springboot.mapper.SystemReqMapper;
import org.springframework.stereotype.Service;

@Service
public class SystemReqService extends ServiceImpl<SystemReqMapper, SystemReq> {
}
