-- 元数据管理平台 - 数据库表结构创建脚本
-- 生成时间: 2025年7月24日
-- 数据库类型: PostgreSQL

-- ====================================================================
-- 1. 数据源管理模块
-- ====================================================================

-- 数据源表
CREATE TABLE metadata_datasource (
    id BIGINT PRIMARY KEY,
    datasource_name VARCHAR(256) NOT NULL,
    code VARCHAR(128) NOT NULL,
    datasource_type VARCHAR(64) NOT NULL,
    config JSONB NOT NULL,
    description TEXT,
    run_mode INTEGER NOT NULL DEFAULT 0,
    app_id BIGINT NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    creator BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT NOT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lock_version INTEGER NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL
);

-- 数据源表注释
COMMENT ON TABLE metadata_datasource IS '数据源表';
COMMENT ON COLUMN metadata_datasource.id IS '主键ID';
COMMENT ON COLUMN metadata_datasource.datasource_name IS '数据源名称';
COMMENT ON COLUMN metadata_datasource.code IS '数据源编码';
COMMENT ON COLUMN metadata_datasource.datasource_type IS '数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)';
COMMENT ON COLUMN metadata_datasource.config IS '数据源配置信息(JSON格式存储所有连接参数)';
COMMENT ON COLUMN metadata_datasource.description IS '描述';
COMMENT ON COLUMN metadata_datasource.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN metadata_datasource.app_id IS '应用ID';
COMMENT ON COLUMN metadata_datasource.deleted IS '软删除标识';
COMMENT ON COLUMN metadata_datasource.creator IS '创建人ID';
COMMENT ON COLUMN metadata_datasource.create_time IS '创建时间';
COMMENT ON COLUMN metadata_datasource.updater IS '更新人ID';
COMMENT ON COLUMN metadata_datasource.update_time IS '更新时间';
COMMENT ON COLUMN metadata_datasource.lock_version IS '版本锁标识';
COMMENT ON COLUMN metadata_datasource.tenant_id IS '租户ID';

-- 数据源表索引
CREATE UNIQUE INDEX uk_datasource_code ON metadata_datasource(code, app_id, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_datasource_tenant_app ON metadata_datasource(app_id, tenant_id, deleted);
CREATE INDEX idx_datasource_type ON metadata_datasource(datasource_type, deleted);
CREATE INDEX idx_datasource_config ON metadata_datasource USING GIN(config);

-- ====================================================================
-- 2. 业务实体管理模块
-- ====================================================================

-- 业务实体表
CREATE TABLE metadata_business_entity (
    id BIGINT PRIMARY KEY,
    display_name VARCHAR(64) NOT NULL,
    code VARCHAR(32) NOT NULL,
    entity_type INTEGER NOT NULL DEFAULT 1,
    description VARCHAR(512),
    datasource_id BIGINT NOT NULL,
    table_name VARCHAR(128),
    run_mode INTEGER NOT NULL DEFAULT 0,
    app_id BIGINT NOT NULL,
    display_config TEXT,
    deleted INTEGER NOT NULL DEFAULT 0,
    creator BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT NOT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lock_version INTEGER NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL
);

-- 业务实体表注释
COMMENT ON TABLE metadata_business_entity IS '业务实体表';
COMMENT ON COLUMN metadata_business_entity.id IS '主键ID';
COMMENT ON COLUMN metadata_business_entity.display_name IS '实体名称';
COMMENT ON COLUMN metadata_business_entity.code IS '实体编码';
COMMENT ON COLUMN metadata_business_entity.entity_type IS '实体类型(1:实体业务模型 2:虚拟业务模型)';
COMMENT ON COLUMN metadata_business_entity.description IS '实体描述';
COMMENT ON COLUMN metadata_business_entity.datasource_id IS '数据源ID';
COMMENT ON COLUMN metadata_business_entity.table_name IS '对应数据表名';
COMMENT ON COLUMN metadata_business_entity.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN metadata_business_entity.app_id IS '应用ID';
COMMENT ON COLUMN metadata_business_entity.display_config IS '前端显示配置json';
COMMENT ON COLUMN metadata_business_entity.deleted IS '软删除标识';
COMMENT ON COLUMN metadata_business_entity.creator IS '创建人ID';
COMMENT ON COLUMN metadata_business_entity.create_time IS '创建时间';
COMMENT ON COLUMN metadata_business_entity.updater IS '更新人ID';
COMMENT ON COLUMN metadata_business_entity.update_time IS '更新时间';
COMMENT ON COLUMN metadata_business_entity.lock_version IS '版本锁标识';
COMMENT ON COLUMN metadata_business_entity.tenant_id IS '租户ID';

-- 业务实体表索引
CREATE UNIQUE INDEX uk_entity_code ON metadata_business_entity(code, app_id, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_entity_datasource ON metadata_business_entity(datasource_id, deleted);
CREATE INDEX idx_entity_tenant_app ON metadata_business_entity(app_id, tenant_id, deleted);

-- 实体字段表
CREATE TABLE metadata_entity_field (
    id BIGINT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(128) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    field_type VARCHAR(64) NOT NULL,
    data_length INTEGER,
    decimal_places INTEGER,
    default_value TEXT,
    description VARCHAR(256),
    is_system_field BOOLEAN NOT NULL DEFAULT FALSE,
    is_primary_key BOOLEAN NOT NULL DEFAULT FALSE,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_unique BOOLEAN NOT NULL DEFAULT FALSE,
    allow_null BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    validation_rules_id BIGINT,
    run_mode INTEGER NOT NULL DEFAULT 0,
    app_id BIGINT NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    creator BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT NOT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lock_version INTEGER NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL
);

-- 实体字段表注释
COMMENT ON TABLE metadata_entity_field IS '实体字段表';
COMMENT ON COLUMN metadata_entity_field.id IS '主键ID';
COMMENT ON COLUMN metadata_entity_field.entity_id IS '实体ID';
COMMENT ON COLUMN metadata_entity_field.field_name IS '字段名称';
COMMENT ON COLUMN metadata_entity_field.display_name IS '显示名称';
COMMENT ON COLUMN metadata_entity_field.field_type IS '字段类型';
COMMENT ON COLUMN metadata_entity_field.data_length IS '数据长度';
COMMENT ON COLUMN metadata_entity_field.decimal_places IS '小数位数';
COMMENT ON COLUMN metadata_entity_field.default_value IS '默认值';
COMMENT ON COLUMN metadata_entity_field.description IS '字段描述';
COMMENT ON COLUMN metadata_entity_field.is_system_field IS '是否系统字段';
COMMENT ON COLUMN metadata_entity_field.is_primary_key IS '是否主键';
COMMENT ON COLUMN metadata_entity_field.is_required IS '是否必填';
COMMENT ON COLUMN metadata_entity_field.is_unique IS '是否唯一';
COMMENT ON COLUMN metadata_entity_field.allow_null IS '是否允许空值';
COMMENT ON COLUMN metadata_entity_field.sort_order IS '排序';
COMMENT ON COLUMN metadata_entity_field.validation_rules_id IS '校验规则配置';
COMMENT ON COLUMN metadata_entity_field.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN metadata_entity_field.app_id IS '应用ID';
COMMENT ON COLUMN metadata_entity_field.deleted IS '软删除标识';
COMMENT ON COLUMN metadata_entity_field.creator IS '创建人ID';
COMMENT ON COLUMN metadata_entity_field.create_time IS '创建时间';
COMMENT ON COLUMN metadata_entity_field.updater IS '更新人ID';
COMMENT ON COLUMN metadata_entity_field.update_time IS '更新时间';
COMMENT ON COLUMN metadata_entity_field.lock_version IS '版本锁标识';
COMMENT ON COLUMN metadata_entity_field.tenant_id IS '租户ID';

-- 实体字段表索引
CREATE UNIQUE INDEX uk_field_name ON metadata_entity_field(entity_id, field_name) WHERE deleted = 0;
CREATE INDEX idx_field_entity ON metadata_entity_field(entity_id, deleted);
CREATE INDEX idx_field_tenant_app ON metadata_entity_field(tenant_id, app_id, deleted);

-- ====================================================================
-- 3. 关联关系管理模块
-- ====================================================================

-- 实体关系表
CREATE TABLE metadata_entity_relationship (
    id BIGINT PRIMARY KEY,
    relation_name VARCHAR(128) NOT NULL,
    source_entity_id BIGINT NOT NULL,
    target_entity_id BIGINT NOT NULL,
    relationship_type VARCHAR(32) NOT NULL,
    source_field_id VARCHAR(128) NOT NULL,
    target_field_id VARCHAR(128) NOT NULL,
    cascade_type VARCHAR(32) DEFAULT 'READ',
    description VARCHAR(256),
    run_mode INTEGER NOT NULL DEFAULT 0,
    app_id BIGINT NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    creator BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT NOT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lock_version INTEGER NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL
);

-- 实体关系表注释
COMMENT ON TABLE metadata_entity_relationship IS '实体关系表';
COMMENT ON COLUMN metadata_entity_relationship.id IS '主键ID';
COMMENT ON COLUMN metadata_entity_relationship.relation_name IS '关系名称';
COMMENT ON COLUMN metadata_entity_relationship.source_entity_id IS '源实体ID';
COMMENT ON COLUMN metadata_entity_relationship.target_entity_id IS '目标实体ID';
COMMENT ON COLUMN metadata_entity_relationship.relationship_type IS '关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)';
COMMENT ON COLUMN metadata_entity_relationship.source_field_id IS '源字段id';
COMMENT ON COLUMN metadata_entity_relationship.target_field_id IS '目标字段id';
COMMENT ON COLUMN metadata_entity_relationship.cascade_type IS '级联操作类型(read,all,delete,none)';
COMMENT ON COLUMN metadata_entity_relationship.description IS '关系描述';
COMMENT ON COLUMN metadata_entity_relationship.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN metadata_entity_relationship.app_id IS '应用ID';
COMMENT ON COLUMN metadata_entity_relationship.deleted IS '软删除标识';
COMMENT ON COLUMN metadata_entity_relationship.creator IS '创建人ID';
COMMENT ON COLUMN metadata_entity_relationship.create_time IS '创建时间';
COMMENT ON COLUMN metadata_entity_relationship.updater IS '更新人ID';
COMMENT ON COLUMN metadata_entity_relationship.update_time IS '更新时间';
COMMENT ON COLUMN metadata_entity_relationship.lock_version IS '版本锁标识';
COMMENT ON COLUMN metadata_entity_relationship.tenant_id IS '租户ID';

-- 实体关系表索引
CREATE INDEX idx_relationship_source ON metadata_entity_relationship(source_entity_id, deleted);
CREATE INDEX idx_relationship_target ON metadata_entity_relationship(target_entity_id, deleted);
CREATE INDEX idx_relationship_tenant_app ON metadata_entity_relationship(app_id, tenant_id, deleted);

-- ====================================================================
-- 4. 数据校验管理模块
-- ====================================================================

-- 校验规则表
CREATE TABLE metadata_validation_rule (
    id BIGINT PRIMARY KEY,
    validation_name VARCHAR(128) NOT NULL,
    validation_code VARCHAR(128) NOT NULL,
    entity_id BIGINT,
    field_id BIGINT,
    validation_condition VARCHAR(64) NOT NULL,
    validation_type VARCHAR(64) NOT NULL,
    validation_target_object VARCHAR(64) NOT NULL,
    validation_expression TEXT,
    error_message VARCHAR(512) NOT NULL,
    validation_timing VARCHAR(64) NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    run_mode INTEGER NOT NULL DEFAULT 0,
    app_id BIGINT NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    creator BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater BIGINT NOT NULL,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lock_version INTEGER NOT NULL DEFAULT 0,
    tenant_id BIGINT NOT NULL
);

-- 校验规则表注释
COMMENT ON TABLE metadata_validation_rule IS '校验规则表';
COMMENT ON COLUMN metadata_validation_rule.id IS '主键ID';
COMMENT ON COLUMN metadata_validation_rule.validation_name IS '规则名称';
COMMENT ON COLUMN metadata_validation_rule.validation_code IS '规则编码';
COMMENT ON COLUMN metadata_validation_rule.entity_id IS '关联实体ID';
COMMENT ON COLUMN metadata_validation_rule.field_id IS '关联字段ID';
COMMENT ON COLUMN metadata_validation_rule.validation_condition IS '校验条件(=,<,>,<=,>=,LIKE,IN,NOT IN,BETWEEN,NOT BETWEEN,IS NULL,IS NOT NULL,etc)';
COMMENT ON COLUMN metadata_validation_rule.validation_type IS '校验类型(字段，变量等)';
COMMENT ON COLUMN metadata_validation_rule.validation_target_object IS '校验比较对象';
COMMENT ON COLUMN metadata_validation_rule.validation_expression IS '校验表达式';
COMMENT ON COLUMN metadata_validation_rule.error_message IS '错误提示信息';
COMMENT ON COLUMN metadata_validation_rule.validation_timing IS '校验时机(更新时,新增时)';
COMMENT ON COLUMN metadata_validation_rule.sort_order IS '执行顺序';
COMMENT ON COLUMN metadata_validation_rule.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN metadata_validation_rule.app_id IS '应用ID';
COMMENT ON COLUMN metadata_validation_rule.deleted IS '软删除标识';
COMMENT ON COLUMN metadata_validation_rule.creator IS '创建人ID';
COMMENT ON COLUMN metadata_validation_rule.create_time IS '创建时间';
COMMENT ON COLUMN metadata_validation_rule.updater IS '更新人ID';
COMMENT ON COLUMN metadata_validation_rule.update_time IS '更新时间';
COMMENT ON COLUMN metadata_validation_rule.lock_version IS '版本锁标识';
COMMENT ON COLUMN metadata_validation_rule.tenant_id IS '租户ID';

-- 校验规则表索引
CREATE UNIQUE INDEX uk_validation_code ON metadata_validation_rule(validation_code, app_id, tenant_id) WHERE deleted = 0;
CREATE INDEX idx_validation_entity ON metadata_validation_rule(entity_id, deleted);
CREATE INDEX idx_validation_tenant_app ON metadata_validation_rule(app_id, tenant_id, deleted);

-- ====================================================================
-- 外键约束(可选，根据实际需要添加)
-- ====================================================================

-- 业务实体表的数据源外键
-- ALTER TABLE metadata_business_entity ADD CONSTRAINT fk_entity_datasource 
--     FOREIGN KEY (datasource_id) REFERENCES metadata_datasource(id);

-- 实体字段表的实体外键
-- ALTER TABLE metadata_entity_field ADD CONSTRAINT fk_field_entity 
--     FOREIGN KEY (entity_id) REFERENCES metadata_business_entity(id);

-- 实体关系表的外键
-- ALTER TABLE metadata_entity_relationship ADD CONSTRAINT fk_relationship_source_entity 
--     FOREIGN KEY (source_entity_id) REFERENCES metadata_business_entity(id);
-- ALTER TABLE metadata_entity_relationship ADD CONSTRAINT fk_relationship_target_entity 
--     FOREIGN KEY (target_entity_id) REFERENCES metadata_business_entity(id);

-- 校验规则表的外键
-- ALTER TABLE metadata_validation_rule ADD CONSTRAINT fk_validation_entity 
--     FOREIGN KEY (entity_id) REFERENCES metadata_business_entity(id);
-- ALTER TABLE metadata_validation_rule ADD CONSTRAINT fk_validation_field 
--     FOREIGN KEY (field_id) REFERENCES metadata_entity_field(id);
