package com.cmsr.onebase.framework.remote.dto.instance;

import com.cmsr.onebase.framework.remote.enums.ExecuteTypeEnum;
import lombok.Data;

/**
 * 流程实例执行参数 DTO（重跑/暂停/停止等）
 */
@Data
public class ProcessInstanceRunParamDTO {
    private Long processInstanceId;
    private ExecuteTypeEnum executeType;
}

