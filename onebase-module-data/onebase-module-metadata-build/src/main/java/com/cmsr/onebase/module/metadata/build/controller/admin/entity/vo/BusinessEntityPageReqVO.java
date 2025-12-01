package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 业务实体分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessEntityPageReqVO extends PageParam {

    @Schema(description = "实体名称", example = "用户实体")
    private String displayName;

    @Schema(description = "实体编码", example = "user_entity")
    private String code;

    @Schema(description = "实体类型(1:自建表，2:复用已有表，3:中间表)", example = "1")
    private Integer entityType;

    @Schema(description = "数据源ID", example = "1")
    private String datasourceId;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "状态：0 关闭，1 开启", example = "1")
    private Integer status;

}
