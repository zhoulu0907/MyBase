package com.cmsr.onebase.module.system.vo.config;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.ReturnTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.config.ConfigTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
public class SystemConfigSearchReqVO {
    @Schema(description = "批量查询keys", example = "配置项key")
    @NotNull(message = "配置项不能为空")
    private Set<String> configKeys;

    @Schema(description = "AppId称不能为空", example = "名称")
    @NotNull(message = "AppId称不能为空")
    private Long appId;

    @InEnum(value = ConfigTypeEnum.class, message = "配置类型必须是 {value}")
    private String configType;


}
