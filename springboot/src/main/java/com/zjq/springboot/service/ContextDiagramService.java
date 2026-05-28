package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.ContextDiagram;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.ContextDiagramMapper;
import org.springframework.stereotype.Service;

@Service
public class ContextDiagramService extends ServiceImpl<ContextDiagramMapper, ContextDiagram> {

    public boolean saveContextDiagram(ContextDiagram contextDiagram){
        return saveOrUpdate(contextDiagram);
    }
}
