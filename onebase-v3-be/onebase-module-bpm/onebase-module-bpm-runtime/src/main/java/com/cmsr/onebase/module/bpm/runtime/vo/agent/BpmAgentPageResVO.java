package com.cmsr.onebase.module.bpm.runtime.vo.agent;

import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "流程代理列表VO")
@Data
public class BpmAgentPageResVO {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 代理人信息
     */
    private UserBasicInfoVO agent;
    /**
     * 被代理人信息
     */
    private UserBasicInfoVO principal;
    /**
     * 代理生效开始时间
     */
    private LocalDateTime startTime;

    /**
     * 代理结束时间
     */
    private LocalDateTime endTime;
    /**
     * 代理状态
     */
    private String agentStatus;
    /**
     * 创建人
     */
    private UserBasicInfoVO creator;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
