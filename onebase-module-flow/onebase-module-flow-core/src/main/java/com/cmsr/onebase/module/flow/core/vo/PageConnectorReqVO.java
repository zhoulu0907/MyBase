package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageConnectorReqVO extends PageParam {

    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    private String connectorName;

    private String level1Code;

    private String level2Code;

    private String level3Code;

}
