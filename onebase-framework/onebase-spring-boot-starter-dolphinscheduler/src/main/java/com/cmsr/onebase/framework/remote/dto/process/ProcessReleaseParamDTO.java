package com.cmsr.onebase.framework.remote.dto.process;

import com.cmsr.onebase.framework.remote.enums.ReleaseStateEnum;
import lombok.Data;

/**
 * 工作流发布参数（上线/下线） DTO
 */
@Data
public class ProcessReleaseParamDTO {
    /** 发布状态：ONLINE 或 OFFLINE */
    private ReleaseStateEnum releaseState;
}
