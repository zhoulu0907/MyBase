-- 项目信息表
CREATE TABLE IF NOT EXISTS project_info (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    project_code VARCHAR(64) NOT NULL,
    project_name VARCHAR(128),
    source_platform VARCHAR(32) DEFAULT 'internal',
    external_project_id VARCHAR(128),
    status INT DEFAULT 1,
    description VARCHAR(512),
    creator BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BIGINT DEFAULT 0,
    CONSTRAINT uk_tenant_project_code UNIQUE (tenant_id, project_code)
);

COMMENT ON TABLE project_info IS '项目信息表';
COMMENT ON COLUMN project_info.id IS '主键';
COMMENT ON COLUMN project_info.tenant_id IS '租户ID';
COMMENT ON COLUMN project_info.project_code IS '项目编码';
COMMENT ON COLUMN project_info.project_name IS '项目名称';
COMMENT ON COLUMN project_info.source_platform IS '来源平台';
COMMENT ON COLUMN project_info.external_project_id IS '外部平台项目ID';
COMMENT ON COLUMN project_info.status IS '状态：0禁用，1启用';
COMMENT ON COLUMN project_info.description IS '描述';
COMMENT ON COLUMN project_info.creator IS '创建人';
COMMENT ON COLUMN project_info.create_time IS '创建时间';
COMMENT ON COLUMN project_info.updater IS '更新人';
COMMENT ON COLUMN project_info.update_time IS '更新时间';
COMMENT ON COLUMN project_info.deleted IS '删除标记';