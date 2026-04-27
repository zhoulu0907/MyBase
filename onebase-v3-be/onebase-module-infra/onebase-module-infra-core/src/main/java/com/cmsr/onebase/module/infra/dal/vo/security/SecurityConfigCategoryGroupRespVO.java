package com.cmsr.onebase.module.infra.dal.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 安全配置响应数据 VO")
@Data
public class SecurityConfigCategoryGroupRespVO {
    @Schema(description = "分类编码", example = "PASSWORD_POLICY")
    private String categoryCode;

    @Schema(description = "安全配置项内容")
    private List<SecurityConfigItemRespVO> securityConfigItemRespVO;
}
