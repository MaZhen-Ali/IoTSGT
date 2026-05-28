package com.zjq.springboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjq.springboot.entity.TaskIntent;
import com.zjq.springboot.mapper.TaskIntentMapper;
import org.springframework.stereotype.Service;

@Service
public class TaskIntentService extends ServiceImpl<TaskIntentMapper,TaskIntent> {


    public boolean saveTaskIntent(TaskIntent taskIntent){
        return saveOrUpdate(taskIntent);
    }
}
