package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询HTTP动作请求VO
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
@Schema(description = "分页查询HTTP动作请求VO")
public class PageConnectorHttpReqVO extends PageParam {

    /**
     * 连接器UUID
     */
    @Schema(description = "连接器UUID", example = "uuid-http-001")
    private String connectorUuid;

    /**
     * HTTP动作名称（模糊查询）
     */
    @Schema(description = "HTTP动作名称（模糊查询）", example = "获取用户信息")
    private String httpName;

    /**
     * HTTP动作编码（模糊查询）
     */
    @Schema(description = "HTTP动作编码（模糊查询）", example = "GET_USER_INFO")
    private String httpCode;

    /**
     * 请求方法
     */
    @Schema(description = "请求方法", example = "GET")
    private String requestMethod;

    /**
     * 启用状态
     */
    @Schema(description = "启用状态", example = "1")
    private Integer activeStatus;
}
