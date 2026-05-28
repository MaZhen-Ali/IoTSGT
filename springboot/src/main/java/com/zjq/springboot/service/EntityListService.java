package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.EntityList;
import com.zjq.springboot.mapper.EntityListMapper;
import org.springframework.stereotype.Service;

@Service
public class EntityListService extends ServiceImpl<EntityListMapper, EntityList> {
}
