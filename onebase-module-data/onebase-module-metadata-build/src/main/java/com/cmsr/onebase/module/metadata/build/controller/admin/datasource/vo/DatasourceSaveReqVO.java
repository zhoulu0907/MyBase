package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 数据源创建/修改 Request VO")
@Data
public class DatasourceSaveReqVO {

    @Schema(description = "数据源编号", example = "1024")
    private String id;

    @Schema(description = "数据源名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户数据库")
    @NotBlank(message = "数据源名称不能为空")
    @Size(max = 256, message = "数据源名称长度不能超过256个字符")
    private String datasourceName;

    @Schema(description = "数据源编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_db")
    @NotBlank(message = "数据源编码不能为空")
    @Size(max = 128, message = "数据源编码长度不能超过128个字符")
    private String code;

    @Schema(description = "数据源类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "POSTGRESQL")
    @NotBlank(message = "数据源类型不能为空")
    @Size(max = 64, message = "数据源类型长度不能超过64个字符")
    private String datasourceType;

    @Schema(description = "数据源配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源配置信息不能为空")
    private Map<String, Object> config;

    @Schema(description = "描述", example = "用户相关数据的数据源")
    private String description;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "应用唯一UID（用于与数据源建立关联时的外部标识，可选）", example = "app_8df0f2a6")
    private String appUid;

    @Schema(description = "数据源来源，0：系统默认，1：自有数据源，2：外部数据源", example = "1")
    private Integer datasourceOrigin;

    @Schema(description = "版本锁标识", example = "0")
    private Integer lockVersion;

}
