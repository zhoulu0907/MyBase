package com.cmsr.onebase.module.system.vo.corpapprelation;

import com.cmsr.onebase.framework.common.enums.CorpAppReationStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "企业应用关联表分页")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CorpAppPageReqVO extends PageParam {

    @Schema(description = "企业ID")
    @NotNull(message = "企业ID不能为空")
    private Long corpId;

    @Schema(description = "状态")
    @InEnum(value = CorpAppReationStatusEnum.class, message = "状态必须是 {value}")
    private Integer status = 0;

    @Schema(description = "应用名称")
    private String appName;
}