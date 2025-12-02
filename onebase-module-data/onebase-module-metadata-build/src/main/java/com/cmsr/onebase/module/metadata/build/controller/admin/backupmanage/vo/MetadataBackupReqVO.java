package com.cmsr.onebase.module.metadata.build.controller.admin.backupmanage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 元数据备份请求 VO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "管理后台 - 元数据备份请求")
@Data
public class MetadataBackupReqVO {

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

}
