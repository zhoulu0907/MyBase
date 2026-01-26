package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 连接器环境配置分页查询请求VO
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "连接器环境配置分页查询请求")
public class PageConnectorEnvReqVO extends PageParam {

    @Schema(description = "连接器类型编号", example = "DATABASE_MYSQL")
    private String typeCode;

    @Schema(description = "环境编码", example = "DEV")
    private String envCode;

    @Schema(description = "环境名称（模糊查询）", example = "开发")
    private String envName;

    @Schema(description = "启用状态", example = "1")
    private Integer activeStatus;
}
