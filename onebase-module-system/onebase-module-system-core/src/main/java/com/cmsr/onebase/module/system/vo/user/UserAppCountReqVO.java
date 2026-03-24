package com.cmsr.onebase.module.system.vo.user;

import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.system.enums.app.PlatformTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 对外查询接口 - 获取同一租户下用户创建的应用数量 Request VO
 *
 * @author yuxin
 * @date 2026-03-07
 */
@Schema(description = "对外查询接口 - 获取同一租户下用户创建的应用数量 Request VO")
@Data
public class UserAppCountReqVO {


    @Schema(description = "平台类型", example = "onebase")
    @InEnum(PlatformTypeEnum.class)
    @NotNull(message = "平台类型不能为空")
    private String platformType;

    @Schema(description = "平台租户id", example = "onebase")
    @NotBlank(message = "租户id不能为空")
    private String tenantId;

    @Schema(description = "平台用户id", example = "onebase")
    @NotBlank(message = "用户id不能为空")
    private String userId;

}
