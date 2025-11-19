package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageConnectorScriptReqVO extends PageParam {

    @NotNull(message = "连接器ID不能为空")
    private Long connectorId;

    private String scriptName;

}
