package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "task_intent")
@NoArgsConstructor
@AllArgsConstructor
public class TaskIntent {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String sys;
    private String task;
    private String entity;
    private String target;
    private String sentence;
}
