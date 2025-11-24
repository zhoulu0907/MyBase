package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "自动化工作流 - 分页查询脚本连接器动作请求VO")
@Data
public class PageConnectorScriptReqVO extends PageParam {

    @Schema(description = "连接器ID")
    @NotNull(message = "连接器ID不能为空")
    private Long connectorId;

    @Schema(description = "脚本名称")
    private String scriptName;

}
