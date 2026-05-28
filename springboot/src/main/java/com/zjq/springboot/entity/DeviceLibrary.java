package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "device_library")
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLibrary {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private String applicationlist;
    private String deviceinterface;
    private String physical;
    private String intent;
    private String behavior;
    private String flag;
}
