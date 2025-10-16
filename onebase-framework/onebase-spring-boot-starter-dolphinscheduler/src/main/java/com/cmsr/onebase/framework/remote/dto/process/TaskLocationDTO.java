package com.cmsr.onebase.framework.remote.dto.process;

import lombok.Data;

/** 任务在画布中的位置信息 DTO */
@Data
public class TaskLocationDTO {
    private Long taskCode;
    private int x;
    private int y;
}

