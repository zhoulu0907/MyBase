package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 统一动作配置数据对象
 * <p>
 * 对应表: flow_connector_action
 * 存储所有连接器类型的动作配置，通过 connector_type 区分类型
 * <p>
 * 动作就像方法一样，有自己的 code、名称、输入、输出定义
 *
 * @author onebase
 * @since 2026-03-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "统一动作配置")
@Table(value = "flow_connector_action")
public class FlowConnectorActionDO extends BaseAppEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属连接器UUID
     */
    @Schema(description = "所属连接器UUID")
    @Column(value = "connector_uuid")
    private String connectorUuid;

    /**
     * 连接器类型
     * <p>
     * HTTP/SCRIPT/DATABASE/EMAIL 等
     */
    @Schema(description = "连接器类型(HTTP/SCRIPT/...)")
    @Column(value = "connector_type")
    private String connectorType;

    /**
     * 动作唯一标识
     */
    @Schema(description = "动作唯一标识")
    @Column(value = "action_uuid")
    private String actionUuid;

    /**
     * 动作编码
     * <p>
     * 如: "GET_USER_INFO"、"CREATE_ORDER"
     */
    @Schema(description = "动作编码")
    @Column(value = "action_code")
    private String actionCode;

    /**
     * 动作名称
     * <p>
     * 如: "获取用户信息"、"创建订单"
     */
    @Schema(description = "动作名称")
    @Column(value = "action_name")
    private String actionName;

    /**
     * 动作描述
     */
    @Schema(description = "动作描述")
    @Column(value = "description")
    private String description;

    /**
     * 输入参数Schema
     * <p>
     * JSON格式，供前端生成表单
     */
    @Schema(description = "输入参数Schema(JSON)")
    @Column(value = "input_schema")
    private String inputSchema;

    /**
     * 输出参数Schema
     * <p>
     * JSON格式
     */
    @Schema(description = "输出参数Schema(JSON)")
    @Column(value = "output_schema")
    private String outputSchema;

    /**
     * 扩展配置
     * <p>
     * JSON格式，混合模式：
     * - 标准化部分：timeout、retryCount、mockResponse
     * - 类型特有部分：http、script 等类型特有配置
     */
    @Schema(description = "扩展配置(JSON)")
    @Column(value = "action_config")
    private String actionConfig;

    /**
     * 启用状态
     * <p>
     * 0-禁用, 1-启用
     */
    @Schema(description = "启用状态(0-禁用,1-启用)")
    @Column(value = "active_status")
    private Integer activeStatus;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @Column(value = "sort_order")
    private Integer sortOrder;
}