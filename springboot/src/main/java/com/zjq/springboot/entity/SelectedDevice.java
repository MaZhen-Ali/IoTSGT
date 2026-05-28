package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "selected_device")
@NoArgsConstructor
@AllArgsConstructor
public class SelectedDevice {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer number;
    private String name;
    private String type;
    private String applicationlist;
    private String deviceinterface;
    private String physical;
    private String intent;
    private String behavior;
}
