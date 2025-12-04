package com.cmsr.onebase.module.infra.api.file.dto;

import com.cmsr.onebase.framework.common.validation.InEnum;
import com.cmsr.onebase.module.infra.api.file.enums.FileVisitModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Schema(description = "RPC 服务 - 文件创建 Request DTO")
@Data
public class FileCreateReqDTO {

    @Schema(description = "原文件名称", example = "xxx.png")
    private String name;

    @Schema(description = "文件目录", example = "xxx")
    private String directory;

    @Schema(description = "文件的 MIME 类型", example = "image/png")
    private String type;

    @Schema(description = "文件内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "文件内容不能为空")
    private byte[] content;

    @Schema(description = "文件保存标识",example = "public-公开访问，private-各runMode私有访问", requiredMode = Schema.RequiredMode.REQUIRED)
    @InEnum(value = FileVisitModeEnum.class, message = "访问标识 {value}")
    private String visitMode;

}
