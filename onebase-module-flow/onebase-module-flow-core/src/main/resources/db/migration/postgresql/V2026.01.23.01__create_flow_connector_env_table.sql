-- 创建连接器环境配置表
-- 用于存储连接器类型的环境配置信息（URL、认证等）
-- 一个连接器类型可以对应多个环境配置
-- 连接器实例通过 env_uuid 引用环境配置

-- 创建 flow_connector_env 表
CREATE TABLE IF NOT EXISTS flow_connector_env (
    -- 主键
    id BIGINT PRIMARY KEY,

    -- 基础标识
    env_uuid VARCHAR(64) NOT NULL,
    env_name VARCHAR(128) NOT NULL,
    env_code VARCHAR(64) NOT NULL,

    -- 关联连接器类型（对应 flow_node_config.node_code）
    type_code VARCHAR(64) NOT NULL,

    -- 连接配置（环境通用配置）
    env_url VARCHAR(512),
    auth_type VARCHAR(32),
    auth_config TEXT,

    -- 扩展配置
    description VARCHAR(512),
    extra_config TEXT,

    -- 状态控制
    active_status INT DEFAULT 1,
    sort_order INT DEFAULT 0,

    -- 租户与应用隔离
    tenant_id BIGINT NOT NULL,
    application_id BIGINT,

    -- 审计字段
    creator BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BIGINT DEFAULT 0,

    -- 乐观锁
    lock_version BIGINT DEFAULT 0
);

-- 添加表注释
COMMENT ON TABLE flow_connector_env IS '连接器环境配置表';
COMMENT ON COLUMN flow_connector_env.id IS '主键ID';
COMMENT ON COLUMN flow_connector_env.env_uuid IS '环境配置UUID（全局唯一）';
COMMENT ON COLUMN flow_connector_env.env_name IS '环境名称';
COMMENT ON COLUMN flow_connector_env.env_code IS '环境编码（DEV/TEST/PROD）';
COMMENT ON COLUMN flow_connector_env.type_code IS '关联的连接器类型编号（对应flow_node_config.node_code）';
COMMENT ON COLUMN flow_connector_env.env_url IS '环境URL（数据库地址、API域名等）';
COMMENT ON COLUMN flow_connector_env.auth_type IS '认证方式';
COMMENT ON COLUMN flow_connector_env.auth_config IS '认证配置（JSON格式）';
COMMENT ON COLUMN flow_connector_env.description IS '环境描述';
COMMENT ON COLUMN flow_connector_env.extra_config IS '扩展配置（JSON格式）';
COMMENT ON COLUMN flow_connector_env.active_status IS '启用状态（0-禁用，1-启用）';
COMMENT ON COLUMN flow_connector_env.sort_order IS '排序序号';
COMMENT ON COLUMN flow_connector_env.tenant_id IS '租户ID';
COMMENT ON COLUMN flow_connector_env.application_id IS '应用ID';
COMMENT ON COLUMN flow_connector_env.creator IS '创建人ID';
COMMENT ON COLUMN flow_connector_env.create_time IS '创建时间';
COMMENT ON COLUMN flow_connector_env.updater IS '更新人ID';
COMMENT ON COLUMN flow_connector_env.update_time IS '更新时间';
COMMENT ON COLUMN flow_connector_env.deleted IS '删除标识（0-正常，1-已删除）';
COMMENT ON COLUMN flow_connector_env.lock_version IS '乐观锁版本号';

-- 创建索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_env_uuid ON flow_connector_env(env_uuid);
CREATE INDEX IF NOT EXISTS idx_type_code ON flow_connector_env(type_code);
CREATE INDEX IF NOT EXISTS idx_tenant_app ON flow_connector_env(tenant_id, application_id);
CREATE INDEX IF NOT EXISTS idx_type_env ON flow_connector_env(type_code, env_code);
CREATE INDEX IF NOT EXISTS idx_active ON flow_connector_env(active_status, deleted);

-- 修改 flow_connector 表，增加环境配置引用
ALTER TABLE flow_connector
ADD COLUMN IF NOT EXISTS env_uuid VARCHAR(64);

COMMENT ON COLUMN flow_connector.env_uuid IS '引用的环境配置UUID（关联flow_connector_env）';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_env_uuid ON flow_connector(env_uuid);
