package com.cmsr.onebase.module.bpm.runtime.vo.agent;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 *流程代理列表查询条件VO
 */
@Data
public class BpmAgentPageReqVO extends PageParam {

    @Schema(description = "应用ID", example = "1332334434343")
    private Long appId;

    @Schema(description = "人员名称", example = "张三")
    private String personName ;

    @Schema(description = "代理状态", example = "inactive")
    private String agentStatus;
}
