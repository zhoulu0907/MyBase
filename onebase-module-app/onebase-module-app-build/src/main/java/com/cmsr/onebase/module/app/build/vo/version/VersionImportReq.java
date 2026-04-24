package com.cmsr.onebase.module.app.build.vo.version;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 应用版本导入请求体
 *
 * @author zhoumingji
 * @date 2026/01/13
 */
@Data
public class VersionImportReq {
    /**
     * 导出的ZIP压缩包
     */
    @Schema(description = "版本压缩包", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "导入文件不能为空")
    private MultipartFile file;

    /**
     * 应用ID（可选）
     * 如果传了应用ID，则覆盖当前应用的开发版本（version_tag 为0）
     * 如果没传，就创建新的应用
     */
    @Schema(description = "应用ID（可选），如果传了则覆盖当前应用的开发版本，没传则创建新应用")
    private Long applicationId;

    /**
     * 项目ID（可选）
     */
    @Schema(description = "项目ID")
    private String projectId;

}
