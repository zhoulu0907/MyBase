package com.cmsr.onebase.module.system.vo.corpapprelation;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "企业应用关联表分页")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CorpAppRelationPageReqVO extends PageParam {

    @Schema(description = "企业id" )
    private Long corpId;

    @Schema(description = "状态（启用，禁用，过期）" )
    private Integer status;

    @Schema(description = "应用名称" )
    private String applicationName;



}