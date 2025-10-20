// 3. 创建 enterprise 模块的 VO 对象
package com.cmsr.onebase.module.system.api.enterprise.dto;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 企业保存请求参数
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class EnterpriseSaveReqVO extends PageParam {

    @Schema(description = "企业主键ID", example = "1")
    private Long id;

    @NotBlank(message = "企业名称不能为空")
    @Schema(description = "企业名称", example = "")
    private String enterpriseName;

    @NotBlank(message = "企业编号不能为空")
    @Schema(description = "企业编号", example = "")
    private String enterpriseCode;

    @NotNull(message = "行业类型不能为空")
    @Schema(description = "行业类型", example = "")
    private Integer industryType;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态", example = "1")
    private Integer status;


    @Schema(description = "地址", example = "")
    private String address;

    @NotBlank(message = "管理员ID不能为空")
    @Schema(description = "管理员ID", example = "admin123")
    private String adminId;

    @NotBlank(message = "电话不能为空")
    @Schema(description = "电话", example = "13800138000")
    private String phone;


    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "授权应用", example = "APP1,APP2")
    private String authorizedApps;

    @NotNull(message = "用户数量不能为空")
    @Schema(description = "用户数量", example = "100")
    private Integer userCount;

    @Schema(description = "备注", example = "")
    private String remark;

    @NotNull(message = "企业id")
    @Schema(description = "企业id", example = "1")
    private Integer tenantId;
}
