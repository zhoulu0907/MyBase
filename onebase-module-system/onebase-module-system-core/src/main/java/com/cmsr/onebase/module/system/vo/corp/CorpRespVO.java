package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业响应对象
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class CorpRespVO {
    @Schema(description = "企业Id", example = "")
    private Long id;

    @Schema(description = "企业Logo", example = "")
    private String corpLogo;

    @Schema(description = "企业名称", example = "")
    private String corpName;

    @Schema(description = "企业编码", example = "ALIBABA")
    private String corpCode;

    @Schema(description = "行业类型", example = "1")
    private Long industryType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地址", example = "")
    private String address;

    @Schema(description = "用户上限", example = "100")
    private Integer userLimit;

    @Schema(description = "用户个数", example = "100")
    private Integer userCount;

    @Schema(description = "应用个数", example = "100")
    private Integer appCount;

    @Schema(description = "联系人邮箱")
    private String email;

    @Schema(description = "联系人电话")
    private String mobile;

    @Schema(description = "管理员")
    private String adminName;

    @Schema(description = "授权应用")
    private List<CorpAppVo> corpApplicationList;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "行业类型名称", example = "1")
    private String industryTypeName;
}
