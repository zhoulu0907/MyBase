package com.cmsr.onebase.framework.remote.dto.process;

import lombok.Data;

/**
 * 任务依赖关系 DTO（仅数据传输）
 */
@Data
public class TaskRelationDTO {
    private String name = "";
    private Long preTaskCode = 0L;
    private Integer preTaskVersion = 0;
    private Long postTaskCode;
    private Integer postTaskVersion = 0;
    private Integer conditionType = 0;
    private Integer conditionParams;
}

