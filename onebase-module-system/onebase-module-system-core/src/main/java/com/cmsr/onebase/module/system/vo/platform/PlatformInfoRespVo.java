package com.cmsr.onebase.module.system.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 平台信息响应VO
 */
@Data
public class PlatformInfoRespVo {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "企业名称")
    private String enterpriseName;

    @Schema(description = "企业编号")
    private String enterpriseCode;

    @Schema(description = "企业地址")
    private String enterpriseAddress;

    @Schema(description = "平台类型")
    private String platformType;

    @Schema(description = "租户数量限制")
    private Integer tenantLimit;

    @Schema(description = "用户数量限制")
    private Integer userLimit;

    @Schema(description = "创建时间")
    private LocalDateTime expireTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否为试用License")
    private Boolean isTrial;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "实际租户数量")
    private Integer actualTenantCount;

    @Schema(description = "实际用户数量")
    private Integer actualUserCount;

    @Schema(description = "创建者")
    private Long creator;

    @Schema(description = "管理员")
    private String adminUser;

}