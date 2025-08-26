package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 动态数据分页查询请求VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 动态数据分页查询请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicDataPageReqVO extends PageParam {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private String entityId;

    @Schema(description = "查询条件，key为字段名，value为字段值", example = "{\"name\": \"张三\", \"status\": 1}")
    private Map<String, Object> filters;

    @Schema(description = "排序字段名", example = "createTime")
    private String sortField;

    @Schema(description = "排序方向", example = "desc")
    private String sortDirection;

    @Schema(description = "方法编码(可选). 若提供, 将用于匹配执行计划", example = "metadata.user.page")
    private String methodCode;

}
