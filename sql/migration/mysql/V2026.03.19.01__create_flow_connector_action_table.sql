-- 统一动作表
-- 用于存储所有连接器类型的动作配置，通过 connector_type 区分类型
CREATE TABLE flow_connector_action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    application_id BIGINT NOT NULL COMMENT '应用ID',

    -- 关联字段
    connector_uuid VARCHAR(64) NOT NULL COMMENT '所属连接器UUID',
    connector_type VARCHAR(50) NOT NULL COMMENT '连接器类型(HTTP/SCRIPT/...)',

    -- 动作基本信息
    action_uuid VARCHAR(64) NOT NULL UNIQUE COMMENT '动作唯一标识',
    action_code VARCHAR(100) COMMENT '动作编码',
    action_name VARCHAR(100) NOT NULL COMMENT '动作名称',
    description VARCHAR(500) COMMENT '动作描述',

    -- 输入输出定义
    input_schema TEXT COMMENT '输入参数Schema(JSON)',
    output_schema TEXT COMMENT '输出参数Schema(JSON)',

    -- 扩展配置（混合模式：标准化字段 + 类型特有配置）
    action_config TEXT COMMENT '类型特有配置(JSON)',

    -- 状态
    active_status INTEGER DEFAULT 1 COMMENT '启用状态(0-禁用,1-启用)',
    sort_order INTEGER DEFAULT 0 COMMENT '排序',

    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator BIGINT COMMENT '创建人',
    updater BIGINT COMMENT '更新人',
    deleted INTEGER DEFAULT 0 COMMENT '删除标志',
    tenant_id BIGINT COMMENT '租户ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一动作配置表';

-- 索引
CREATE INDEX idx_fca_connector_uuid ON flow_connector_action(connector_uuid);
CREATE INDEX idx_fca_connector_type ON flow_connector_action(connector_type);
CREATE INDEX idx_fca_action_code ON flow_connector_action(action_code);
CREATE INDEX idx_fca_active_status ON flow_connector_action(active_status);
CREATE INDEX idx_fca_action_uuid ON flow_connector_action(action_uuid);