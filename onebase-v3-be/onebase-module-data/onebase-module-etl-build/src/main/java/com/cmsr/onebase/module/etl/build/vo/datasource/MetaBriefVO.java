package com.cmsr.onebase.module.etl.build.vo.datasource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据工厂 - 数据源 - 元数据信息预览通用VO")
@Data
public class MetaBriefVO {

    @Schema(description = "元数据ID")
    private String id;

    @Schema(description = "元数据UUID")
    private String uuid;

    @Schema(description = "元数据名称")
    private String name;

    @Schema(description = "元数据展示名称")
    private String displayName;

}
