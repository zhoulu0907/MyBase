package com.cmsr.onebase.plugin.build.vo.req;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件分页查询请求 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class PluginInfoPageReqVO extends PageParam {

    @Schema(description = "插件名称（模糊匹配）", example = "示例")
    private String pluginName;

    @Schema(description = "状态（0停用，1启用）", example = "1")
    private Integer status;

}
