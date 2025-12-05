package com.cmsr.onebase.module.infra.dal.vo.security;

import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 安全配置分类响应VO
 *
 * @author ggq
 * @date 2025-12-04
 */
@Schema(description = "管理后台 - 安全配置分类 Response VO")
@Data
public class SecurityConfigReqVO {

    @Schema(description = "空间ID", example = "1")
    @NotEmpty(message = "空间Id不能未空")
    private Long tenantId;

    @Schema(description = "场景编码")
   // @InEnum(value = SecurityConfigKey.EnableScenariosOption.class, message = "场景类型必须是 {value}")
    private String scenariosCode;

}
