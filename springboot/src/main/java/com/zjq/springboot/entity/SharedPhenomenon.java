package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "")
@NoArgsConstructor
@AllArgsConstructor
public class SharedPhenomenon {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String device;
    private String task;
    private String physical;
    private double similarity;
}
