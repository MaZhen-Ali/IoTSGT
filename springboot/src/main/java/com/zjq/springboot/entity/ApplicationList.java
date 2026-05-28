package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "application_list")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationList {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    private String application;
    private String physical;
    private String intent;
    private String behavior;
    private String flag;

}
