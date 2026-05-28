package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "software_requirement")
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareRequirement {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String description;
    private String flag;

    public SoftwareRequirement(String description) {
        this.description = description;
    }

    public SoftwareRequirement(String description, String flag) {
        this.description = description;
        this.flag = flag;
    }
}
