-- 数据源表
CREATE TABLE IF NOT EXISTS metadata_datasource (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    datasource_name VARCHAR(100) NOT NULL COMMENT '数据源名称',
    code VARCHAR(100) NOT NULL COMMENT '数据源编码',
    datasource_type VARCHAR(50) NOT NULL COMMENT '数据源类型',
    config TEXT COMMENT '连接配置',
    description VARCHAR(500) COMMENT '描述信息',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code, deleted, tenant_id)
) COMMENT = '元数据数据源表';

-- 业务实体表
CREATE TABLE IF NOT EXISTS metadata_business_entity (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    display_name VARCHAR(100) NOT NULL COMMENT '显示名称',
    code VARCHAR(100) NOT NULL COMMENT '实体编码',
    entity_type INT NOT NULL COMMENT '实体类型',
    description VARCHAR(500) COMMENT '描述信息',
    datasource_id BIGINT NOT NULL COMMENT '数据源ID',
    table_name VARCHAR(100) COMMENT '对应表名',
    run_mode INT NOT NULL DEFAULT 0 COMMENT '运行模式：0 编辑态，1 运行态',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    display_config TEXT COMMENT '前端显示配置json',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code, deleted, tenant_id)
) COMMENT = '元数据业务实体表';

-- 实体字段表
CREATE TABLE IF NOT EXISTS metadata_entity_field (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    entity_id BIGINT NOT NULL COMMENT '实体ID',
    field_name VARCHAR(100) NOT NULL COMMENT '字段名',
    display_name VARCHAR(100) NOT NULL COMMENT '显示名称',
    field_type VARCHAR(50) NOT NULL COMMENT '字段类型',
    data_length INT COMMENT '数据长度',
    decimal_places INT COMMENT '小数位数',
    default_value VARCHAR(200) COMMENT '默认值',
    description VARCHAR(500) COMMENT '描述信息',
    is_required BIT NOT NULL DEFAULT FALSE COMMENT '是否必填',
    is_unique BIT NOT NULL DEFAULT FALSE COMMENT '是否唯一',
    allow_null BIT NOT NULL DEFAULT TRUE COMMENT '允许空值',
    is_system_field BIT NOT NULL DEFAULT FALSE COMMENT '是否系统字段',
    is_primary_key BIT NOT NULL DEFAULT FALSE COMMENT '是否主键',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    KEY idx_entity_id (entity_id)
) COMMENT = '元数据实体字段表';

-- 实体关系表
CREATE TABLE IF NOT EXISTS metadata_entity_relationship (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    relation_name VARCHAR(100) NOT NULL COMMENT '关系名称',
    source_entity_id BIGINT NOT NULL COMMENT '源实体ID',
    target_entity_id BIGINT NOT NULL COMMENT '目标实体ID',
    relationship_type VARCHAR(50) NOT NULL COMMENT '关系类型',
    source_field_id BIGINT NOT NULL COMMENT '源字段ID',
    target_field_id BIGINT NOT NULL COMMENT '目标字段ID',
    cascade_type VARCHAR(50) COMMENT '级联类型',
    description VARCHAR(500) COMMENT '描述信息',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id)
) COMMENT = '元数据实体关系表';

-- 校验规则表
CREATE TABLE IF NOT EXISTS metadata_validation_rule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    validation_name VARCHAR(100) NOT NULL COMMENT '校验名称',
    validation_code VARCHAR(100) NOT NULL COMMENT '校验编码',
    entity_id BIGINT NOT NULL COMMENT '实体ID',
    field_id BIGINT COMMENT '字段ID',
    validation_condition VARCHAR(100) NOT NULL COMMENT '校验条件',
    validation_type VARCHAR(50) NOT NULL COMMENT '校验类型',
    validation_target_object VARCHAR(50) NOT NULL COMMENT '校验目标对象',
    validation_expression VARCHAR(500) COMMENT '校验表达式',
    error_message VARCHAR(200) NOT NULL COMMENT '错误信息',
    validation_timing VARCHAR(100) COMMENT '校验时机',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    app_id BIGINT NOT NULL COMMENT '应用ID',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    KEY idx_entity_id (entity_id),
    KEY idx_field_id (field_id)
) COMMENT = '元数据校验规则表'; 