package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.cmsr.onebase.framework.common.validation.ValidTableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 业务实体创建/修改 Request VO")
@Data
public class BusinessEntitySaveReqVO {

    @Schema(description = "实体编号", example = "1024")
    private String id;

    @Schema(description = "实体UUID（更新时可用于定位记录）", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户实体")
    @NotBlank(message = "实体名称不能为空")
    @Size(max = 64, message = "实体名称长度不能超过64个字符")
    private String displayName;

    @Schema(description = "实体编码", example = "user_entity")
    @Size(max = 32, message = "实体编码长度不能超过32个字符")
    private String code;

    @Schema(description = "实体类型(1:自建表，2:复用已有表，3:中间表)", example = "1")
    private Integer entityType;

    @Schema(description = "实体描述", example = "用户相关的业务实体")
    @Size(max = 512, message = "实体描述长度不能超过512个字符")
    private String description;

    @Schema(description = "数据源UUID", example = "01onal1s-0000-0000-0000-000000000001")
    private String datasourceUuid;

    @Schema(description = "数据源ID（兼容旧版，与datasourceUuid二选一）", example = "164329365983232001")
    private String datasourceId;

    @Schema(description = "对应数据表名", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_user")
    @ValidTableName
    @Size(max = 128, message = "数据表名长度不能超过128个字符")
    private String tableName;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "版本锁标识", example = "0")
    private Integer lockVersion;

    @Schema(description = "前端显示配置json", example = "{\"showFields\":[\"name\",\"code\"]}")
    @Size(max = 2000, message = "前端显示配置长度不能超过2000个字符")
    private String displayConfig;

    @Schema(description = "状态：0 关闭，1 开启", example = "1")
    private Integer status;

}
