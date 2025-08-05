package com.cmsr.onebase.module.system.dal.dataobject.platform;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlatformInfoDo {

    @Schema(description = "企业名称")
    private String enterpriseName;

    @Schema(description = "企业编号")
    private String enterpriseCode;

    @Schema(description = "企业地址")
    private String enterpriseAddress;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "超级管理员")
    private String superAdmin;

    @Schema(description = "平台类型")
    private String platformType;

    @Schema(description = "认证状态")
    private String authStatus;

    @Schema(description = "到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    @Schema(description = "系统版本")
    private String systemVersion;

    @Schema(description = "租户数量")
    private Integer tenantCount;

}
