package com.zjq.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjq.springboot.entity.TaskIntent;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TaskIntentMapper extends BaseMapper<TaskIntent> {

    @Update("update task_intent set sentence=CONCAT(sys,'需要',task,'以达成',target,'的目的') where id = #{id}")
    int updateSentence(TaskIntent taskIntent);

    @Select("select sentence from task_intent")
    List<Object> findSentence();

    @Select("select task from task_intent")
    List<String> findTask();
}
