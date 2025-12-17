package com.cmsr.onebase.module.system.vo.config;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class SystemConfigPageReqVO {
    @Schema(description = "数据源名称，模糊匹配", example = "名称")
    private String name;
    @Schema(description = "状态", example = "")
    @InEnum( value = CommonStatusEnum.class)
    private Long status;
}
