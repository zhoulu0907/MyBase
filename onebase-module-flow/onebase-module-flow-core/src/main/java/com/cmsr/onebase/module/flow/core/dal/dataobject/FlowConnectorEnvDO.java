package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 连接器环境配置数据对象
 * <p>
 * 对应表: flow_connector_env
 * <p>存储连接器类型的环境配置信息（URL、认证等）
 * <p>一个连接器类型可以对应多个环境配置
 * <p>连接器实例通过 env_uuid 引用环境配置
 * <p>继承BaseEntity，支持租户级共享（同一租户下的所有应用可共享环境配置）
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "连接器环境配置")
@Table(value = "flow_connector_env")
public class FlowConnectorEnvDO extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 环境配置UUID（全局唯一）
     */
    @Schema(description = "环境配置UUID（全局唯一）")
    @Column(value = "env_uuid")
    private String envUuid;

    /**
     * 环境名称
     */
    @Schema(description = "环境名称")
    @Column(value = "env_name")
    private String envName;

    /**
     * 环境编码（DEV/TEST/PROD等）
     */
    @Schema(description = "环境编码（DEV/TEST/PROD等）")
    @Column(value = "env_code")
    private String envCode;

    /**
     * 关联的连接器类型编号（对应flow_node_config.node_code）
     */
    @Schema(description = "关联的连接器类型编号（对应flow_node_config.node_code）")
    @Column(value = "type_code")
    private String typeCode;

    /**
     * 环境URL（数据库地址、API域名等）
     */
    @Schema(description = "环境URL（数据库地址、API域名等）")
    @Column(value = "env_url")
    private String envUrl;

    /**
     * 认证方式
     */
    @Schema(description = "认证方式")
    @Column(value = "auth_type")
    private String authType;

    /**
     * 认证配置（JSON格式）
     */
    @Schema(description = "认证配置（JSON格式）")
    @Column(value = "auth_config")
    private String authConfig;

    /**
     * 环境描述
     */
    @Schema(description = "环境描述")
    @Column(value = "description")
    private String description;

    /**
     * 扩展配置（JSON格式）
     */
    @Schema(description = "扩展配置（JSON格式）")
    @Column(value = "extra_config")
    private String extraConfig;

    /**
     * 启用状态（0-禁用，1-启用）
     */
    @Schema(description = "启用状态（0-禁用，1-启用）")
    @Column(value = "active_status")
    private Integer activeStatus;

    /**
     * 排序序号
     */
    @Schema(description = "排序序号")
    @Column(value = "sort_order")
    private Integer sortOrder;

    /**
     * 乐观锁版本号
     */
    @Schema(description = "乐观锁版本号")
    @Column(value = "lock_version")
    private Long lockVersion;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    @Column(value = "tenant_id", tenantId = true)
    private Long tenantId;
}
