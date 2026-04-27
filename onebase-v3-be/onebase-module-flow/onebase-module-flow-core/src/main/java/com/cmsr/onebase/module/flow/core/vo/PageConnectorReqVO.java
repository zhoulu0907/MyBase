package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "自动化工作流 - 分页查询连接器")
@Data
public class PageConnectorReqVO extends PageParam {

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "连接器名称")
    private String connectorName;

    @Schema(description = "Level1 编号")
    private String level1Code;

    @Schema(description = "Level2 编号")
    private String level2Code;

    @Schema(description = "Level3 编号")
    private String level3Code;

    @Schema(description = "连接器类型编码")
    private String typeCode;

    @Schema(description = "启用状态（0-禁用，1-启用）")
    private Integer activeStatus;

}
