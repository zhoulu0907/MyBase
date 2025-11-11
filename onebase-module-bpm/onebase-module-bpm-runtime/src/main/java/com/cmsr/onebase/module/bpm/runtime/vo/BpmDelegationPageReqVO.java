package com.cmsr.onebase.module.bpm.runtime.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 *流程代理列表查询条件VO
 */
@Data
public class BpmDelegationPageReqVO extends PageParam {

    @Schema(description = "应用ID", example = "1332334434343")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "人员名称", example = "张三")
    private String delegatePersonName ;

    @Schema(description = "代理状态", example = "inactive")
    private String delegateStatus;
}
