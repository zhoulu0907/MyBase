package com.cmsr.onebase.module.system.api.applicationauthtenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Schema(description = "应用授权企业表创建/修改 Request VO")
@Data
public class ApplicationAuthEnterpriseSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer applicationId;

    @Schema(description = "企业id", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "企业id不能为空")
    private Integer enterpriseId;

    @Schema(description = "空间id", example = "200")
    private Integer tenantId;

    @Schema(description = "锁标识", example = "1")
    private Long lockVersion;
    
    // 基础字段
    @Schema(description = "创建者", example = "1")
    private Long creator;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新者", example = "1")
    private Long updater;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "是否删除", example = "0")
    private Long deleted;

    @Schema(description = "应用id")
    @NotNull(message = "企业id不能为空")
    private  List<Integer> applicationIdList;
}