package com.zjq.springboot.controller.dto;

import lombok.Data;

@Data
public class IntentTaskDTO {
    private String task;
    private String physical;
    private double similarity;
}
