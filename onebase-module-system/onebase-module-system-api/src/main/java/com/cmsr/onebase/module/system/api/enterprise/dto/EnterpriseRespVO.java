package com.cmsr.onebase.module.system.api.enterprise.dto;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业响应对象
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class EnterpriseRespVO  extends PageParam {

    @Schema(description = "企业主键ID", example = "1")
    private Long id;

    @Schema(description = "企业名称", example = "阿里巴巴")
    private String enterpriseName;

    @Schema(description = "企业编号", example = "ALIBABA")
    private String enterpriseCode;

    @Schema(description = "行业类型", example = "1")
    private Integer industryType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地址", example = "杭州市余杭区未来科技城")
    private String address;

    @Schema(description = "管理员ID", example = "admin123")
    private String adminId;

    @Schema(description = "电话", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "授权应用", example = "APP1,APP2")
    private String authorizedApps;

    @Schema(description = "用户数量", example = "100")
    private Integer userCount;

    @Schema(description = "锁版本", example = "0")
    private Long lockVersion;

    @Schema(description = "创建人", example = "admin")
    private String creator;

    @Schema(description = "创建时间")
    private java.time.LocalDateTime createTime;

    @Schema(description = "更新人", example = "admin")
    private String updater;

    @Schema(description = "更新时间")
    private java.time.LocalDateTime updateTime;

    @Schema(description = "是否已删除", example = "0")
    private Long deleted;

    @Schema(description = "企业id", example = "1")
    private Integer tenantId;
}
