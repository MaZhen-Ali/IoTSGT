package com.zjq.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "context_diagram")
@NoArgsConstructor
@AllArgsConstructor
public class ContextDiagram {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String type;
    private String begin;
    private String link;
    private String end;

}
