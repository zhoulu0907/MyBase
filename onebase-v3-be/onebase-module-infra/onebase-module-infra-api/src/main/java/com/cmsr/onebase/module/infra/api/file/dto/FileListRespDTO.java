package com.cmsr.onebase.module.infra.api.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文件 Response VO,不返回 content 字段，太大")
@Data
public class FileListRespDTO {

    @Schema(description = "文件编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase.jpg")
    private String path;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase.jpg")
    private String name;

    @Schema(description = "文件MIME类型", example = "application/octet-stream")
    private String type;

    @Schema(description = "文件大小(单位：B)", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "创建者", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long creator;

    @Schema(description = "修改时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "修改者", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long updater;

}
