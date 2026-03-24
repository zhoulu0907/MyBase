-- public.metadata_app_and_datasource definition

-- Drop table

-- DROP TABLE public.metadata_app_and_datasource;

CREATE TABLE public.metadata_app_and_datasource (
	id int8 NOT NULL, -- 主键ID
	application_id int8 NOT NULL, -- 应用ID
	datasource_id int8 NOT NULL, -- 数据源ID
	datasource_type varchar(64) NOT NULL, -- 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
	app_uid varchar(255) NOT NULL, -- 应用UID
	lock_version int8 DEFAULT 0 NOT NULL, -- 版本锁标识
	creator int8 DEFAULT 0 NOT NULL, -- 创建人ID
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NOT NULL, -- 更新人ID
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 软删除标识
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户ID
	CONSTRAINT pk_metadata_app_and_datasource PRIMARY KEY (id),
	CONSTRAINT uk_metadata_app_and_datasource UNIQUE (application_id, datasource_id, tenant_id)
);
CREATE INDEX idx_metadata_app_and_datasource_app_id ON public.metadata_app_and_datasource USING btree (application_id);
CREATE INDEX idx_metadata_app_and_datasource_app_uid ON public.metadata_app_and_datasource USING btree (app_uid);
CREATE INDEX idx_metadata_app_and_datasource_datasource_id ON public.metadata_app_and_datasource USING btree (datasource_id);
CREATE INDEX idx_metadata_app_and_datasource_tenant_id ON public.metadata_app_and_datasource USING btree (tenant_id);
CREATE INDEX idx_metadata_app_and_datasource_type ON public.metadata_app_and_datasource USING btree (datasource_type);
COMMENT ON TABLE public.metadata_app_and_datasource IS '应用与数据源关联表';

-- Column comments

COMMENT ON COLUMN public.metadata_app_and_datasource.id IS '主键ID';
COMMENT ON COLUMN public.metadata_app_and_datasource.application_id IS '应用ID';
COMMENT ON COLUMN public.metadata_app_and_datasource.datasource_id IS '数据源ID';
COMMENT ON COLUMN public.metadata_app_and_datasource.datasource_type IS '数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)';
COMMENT ON COLUMN public.metadata_app_and_datasource.app_uid IS '应用UID';
COMMENT ON COLUMN public.metadata_app_and_datasource.lock_version IS '版本锁标识';
COMMENT ON COLUMN public.metadata_app_and_datasource.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_app_and_datasource.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_app_and_datasource.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_app_and_datasource.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_app_and_datasource.deleted IS '软删除标识';
COMMENT ON COLUMN public.metadata_app_and_datasource.tenant_id IS '租户ID';


-- public.metadata_auto_number_config definition

-- Drop table

-- DROP TABLE public.metadata_auto_number_config;

CREATE TABLE public.metadata_auto_number_config (
	id int8 NOT NULL,
	field_id int8 NOT NULL,
	number_mode varchar(16) NOT NULL,
	digit_width int2 NULL,
	overflow_continue int2 DEFAULT 1 NOT NULL,
	initial_value int8 DEFAULT 1 NOT NULL,
	reset_cycle varchar(16) DEFAULT 'NONE'::character varying NOT NULL,
	is_enabled int4 DEFAULT 0 NOT NULL, -- 是否启用：1-启用，0-禁用
	run_mode int4 DEFAULT 0 NOT NULL,
	app_id int8 NOT NULL,
	tenant_id int8 NOT NULL,
	lock_version int4 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_metadata_auto_number_config PRIMARY KEY (id)
);
CREATE INDEX idx_auto_number_field ON public.metadata_auto_number_config USING btree (field_id, deleted);
CREATE UNIQUE INDEX uk_auto_number_field ON public.metadata_auto_number_config USING btree (field_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_auto_number_config IS '自动编号-字段配置';

-- Column comments

COMMENT ON COLUMN public.metadata_auto_number_config.is_enabled IS '是否启用：1-启用，0-禁用';


-- public.metadata_auto_number_reset_log definition

-- Drop table

-- DROP TABLE public.metadata_auto_number_reset_log;

CREATE TABLE public.metadata_auto_number_reset_log (
	id int8 NOT NULL,
	config_id int8 NOT NULL,
	period_key varchar(32) NOT NULL,
	prev_value int8 NULL,
	next_value int8 NOT NULL,
	reset_reason varchar(512) NULL,
	"operator" int8 DEFAULT 0 NOT NULL,
	reset_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	app_id int8 NOT NULL,
	tenant_id int8 NOT NULL,
	lock_version int4 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_metadata_auto_number_reset_log PRIMARY KEY (id)
);
CREATE INDEX idx_auto_number_reset_config ON public.metadata_auto_number_reset_log USING btree (config_id, period_key, deleted);
COMMENT ON TABLE public.metadata_auto_number_reset_log IS '自动编号-手动重置日志';


-- public.metadata_auto_number_rule_item definition

-- Drop table

-- DROP TABLE public.metadata_auto_number_rule_item;

CREATE TABLE public.metadata_auto_number_rule_item (
	id int8 NOT NULL,
	config_id int8 NOT NULL,
	item_type varchar(32) NOT NULL,
	item_order int4 DEFAULT 0 NOT NULL,
	format varchar(64) NULL,
	text_value varchar(10) NULL,
	ref_field_id int8 NULL,
	is_enabled int4 DEFAULT 0 NOT NULL, -- 是否启用：1-启用，0-禁用
	app_id int8 NOT NULL,
	tenant_id int8 NOT NULL,
	lock_version int4 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_metadata_auto_number_rule_item PRIMARY KEY (id)
);
CREATE INDEX idx_auto_number_rule_config ON public.metadata_auto_number_rule_item USING btree (config_id, deleted);
CREATE UNIQUE INDEX uk_auto_number_rule_order ON public.metadata_auto_number_rule_item USING btree (config_id, item_order) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_auto_number_rule_item IS '自动编号-规则项定义';

-- Column comments

COMMENT ON COLUMN public.metadata_auto_number_rule_item.is_enabled IS '是否启用：1-启用，0-禁用';


-- public.metadata_auto_number_state definition

-- Drop table

-- DROP TABLE public.metadata_auto_number_state;

CREATE TABLE public.metadata_auto_number_state (
	id int8 NOT NULL,
	config_id int8 NOT NULL,
	period_key varchar(32) NOT NULL,
	current_value int8 NOT NULL,
	last_reset_time timestamp(6) NULL,
	app_id int8 NOT NULL,
	tenant_id int8 NOT NULL,
	lock_version int4 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_metadata_auto_number_state PRIMARY KEY (id)
);
CREATE INDEX idx_auto_number_state_config ON public.metadata_auto_number_state USING btree (config_id, deleted);
CREATE UNIQUE INDEX uk_auto_number_state_period ON public.metadata_auto_number_state USING btree (config_id, period_key) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_auto_number_state IS '自动编号-周期计数状态';


-- public.metadata_business_entity definition

-- Drop table

-- DROP TABLE public.metadata_business_entity;

CREATE TABLE public.metadata_business_entity (
	id int8 NOT NULL, -- 主键ID
	display_name varchar(64) NOT NULL, -- 实体名称
	code varchar(32) NOT NULL, -- 实体编码
	entity_type int4 DEFAULT 1 NOT NULL, -- 实体类型(1:自建表 2:复用已有表 3中间表用做多对多关联用不给前端展示)
	description varchar(512) NULL, -- 实体描述
	datasource_id int8 NOT NULL, -- 数据源ID
	table_name varchar(128) NULL, -- 对应数据表名
	run_mode int4 DEFAULT 0 NOT NULL, -- 运行模式：0 编辑态，1 运行态
	app_id int8 NOT NULL, -- 应用ID
	deleted int8 DEFAULT 0 NOT NULL, -- 软删除标识
	creator int8 NOT NULL, -- 创建人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新人ID
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	lock_version int4 DEFAULT 0 NOT NULL, -- 版本锁标识
	tenant_id int8 NOT NULL, -- 租户ID
	display_config text NULL, -- 前端显示配置json
	status int2 DEFAULT 1 NOT NULL, -- 0 关闭，1 开启
	CONSTRAINT metadata_business_entity_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_entity_datasource ON public.metadata_business_entity USING btree (datasource_id, deleted);
CREATE INDEX idx_entity_tenant_app ON public.metadata_business_entity USING btree (app_id, tenant_id, deleted);
COMMENT ON TABLE public.metadata_business_entity IS '业务实体表';

-- Column comments

COMMENT ON COLUMN public.metadata_business_entity.id IS '主键ID';
COMMENT ON COLUMN public.metadata_business_entity.display_name IS '实体名称';
COMMENT ON COLUMN public.metadata_business_entity.code IS '实体编码';
COMMENT ON COLUMN public.metadata_business_entity.entity_type IS '实体类型(1:自建表 2:复用已有表 3中间表用做多对多关联用不给前端展示)';
COMMENT ON COLUMN public.metadata_business_entity.description IS '实体描述';
COMMENT ON COLUMN public.metadata_business_entity.datasource_id IS '数据源ID';
COMMENT ON COLUMN public.metadata_business_entity.table_name IS '对应数据表名';
COMMENT ON COLUMN public.metadata_business_entity.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN public.metadata_business_entity.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_business_entity.deleted IS '软删除标识';
COMMENT ON COLUMN public.metadata_business_entity.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_business_entity.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_business_entity.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_business_entity.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_business_entity.lock_version IS '版本锁标识';
COMMENT ON COLUMN public.metadata_business_entity.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_business_entity.display_config IS '前端显示配置json';
COMMENT ON COLUMN public.metadata_business_entity.status IS '0 关闭，1 开启';


-- public.metadata_component_field_type definition

-- Drop table

-- DROP TABLE public.metadata_component_field_type;

CREATE TABLE public.metadata_component_field_type (
	id bigserial NOT NULL,
	field_type_code varchar(50) NOT NULL, -- 字段类型编码
	field_type_name varchar(100) NOT NULL, -- 字段类型名称
	field_type_desc text NULL, -- 字段类型描述
	data_type varchar(50) NULL, -- jdbc数据类型
	sort_order int4 DEFAULT 0 NULL, -- 排序顺序
	status int2 DEFAULT 1 NULL, -- 状态：0-启用，1-禁用
	creator int8 NULL, -- 创建人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间
	updater int8 NULL, -- 更新人ID
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间
	deleted int2 DEFAULT 0 NULL, -- 删除标识：0-未删除，1-已删除
	"type" int2 NULL, -- 类型（应用过滤使用）
	CONSTRAINT metadata_component_field_type_field_type_code_key UNIQUE (field_type_code),
	CONSTRAINT metadata_component_field_type_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_field_type_code ON public.metadata_component_field_type USING btree (field_type_code);
CREATE INDEX idx_field_type_status ON public.metadata_component_field_type USING btree (status);
COMMENT ON TABLE public.metadata_component_field_type IS '元数据组件字段类型表';

-- Column comments

COMMENT ON COLUMN public.metadata_component_field_type.field_type_code IS '字段类型编码';
COMMENT ON COLUMN public.metadata_component_field_type.field_type_name IS '字段类型名称';
COMMENT ON COLUMN public.metadata_component_field_type.field_type_desc IS '字段类型描述';
COMMENT ON COLUMN public.metadata_component_field_type.data_type IS 'jdbc数据类型';
COMMENT ON COLUMN public.metadata_component_field_type.sort_order IS '排序顺序';
COMMENT ON COLUMN public.metadata_component_field_type.status IS '状态：0-启用，1-禁用';
COMMENT ON COLUMN public.metadata_component_field_type.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_component_field_type.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_component_field_type.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_component_field_type.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_component_field_type.deleted IS '删除标识：0-未删除，1-已删除';
COMMENT ON COLUMN public.metadata_component_field_type."type" IS '类型（应用过滤使用）';

-- public.metadata_data_system_method definition

-- Drop table

-- DROP TABLE public.metadata_data_system_method;

CREATE TABLE public.metadata_data_system_method (
	id bigserial NOT NULL, -- 主键ID
	method_code varchar(100) NOT NULL, -- 方法编码
	method_name varchar(200) NOT NULL, -- 方法名称
	method_type varchar(50) NOT NULL, -- 方法类型：CREATE-新增,READ-查询,UPDATE-更新,DELETE-删除,BATCH-批量操作,DRAFT-草稿
	method_url varchar(500) NULL, -- 方法URL地址
	method_description text NULL, -- 方法描述
	is_enabled int2 DEFAULT 0 NULL, -- 是否启用：1-启用，0-禁用
	request_method varchar(10) DEFAULT 'POST'::character varying NULL, -- HTTP请求方法：GET,POST,PUT,DELETE
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除：0-否，1-是
	creator int8 NOT NULL, -- 创建人ID
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新人ID
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	CONSTRAINT metadata_data_system_method_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_metadata_data_system_method_create_time ON public.metadata_data_system_method USING btree (create_time);
CREATE INDEX idx_metadata_data_system_method_deleted ON public.metadata_data_system_method USING btree (deleted);
CREATE INDEX idx_metadata_data_system_method_is_enabled ON public.metadata_data_system_method USING btree (is_enabled);
CREATE INDEX idx_metadata_data_system_method_method_code ON public.metadata_data_system_method USING btree (method_code);
CREATE INDEX idx_metadata_data_system_method_method_type ON public.metadata_data_system_method USING btree (method_type);
CREATE UNIQUE INDEX uk_metadata_data_system_method_code ON public.metadata_data_system_method USING btree (method_code) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_data_system_method IS '系统数据方法表';

-- Column comments

COMMENT ON COLUMN public.metadata_data_system_method.id IS '主键ID';
COMMENT ON COLUMN public.metadata_data_system_method.method_code IS '方法编码';
COMMENT ON COLUMN public.metadata_data_system_method.method_name IS '方法名称';
COMMENT ON COLUMN public.metadata_data_system_method.method_type IS '方法类型：CREATE-新增,READ-查询,UPDATE-更新,DELETE-删除,BATCH-批量操作,DRAFT-草稿';
COMMENT ON COLUMN public.metadata_data_system_method.method_url IS '方法URL地址';
COMMENT ON COLUMN public.metadata_data_system_method.method_description IS '方法描述';
COMMENT ON COLUMN public.metadata_data_system_method.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_data_system_method.request_method IS 'HTTP请求方法：GET,POST,PUT,DELETE';
COMMENT ON COLUMN public.metadata_data_system_method.deleted IS '是否删除：0-否，1-是';
COMMENT ON COLUMN public.metadata_data_system_method.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_data_system_method.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_data_system_method.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_data_system_method.update_time IS '更新时间';


-- public.metadata_datasource definition

-- Drop table

-- DROP TABLE public.metadata_datasource;

CREATE TABLE public.metadata_datasource (
	id int8 NOT NULL, -- 主键ID
	datasource_name varchar(256) NOT NULL, -- 数据源名称
	code varchar(128) NOT NULL, -- 数据源编码
	datasource_type varchar(64) NOT NULL, -- 数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)
	config text NOT NULL, -- 数据源配置信息(JSON格式存储所有连接参数)
	description text NULL, -- 描述
	run_mode int4 DEFAULT 0 NOT NULL, -- 运行模式：0 编辑态，1 运行态
	app_id int8 NOT NULL, -- 应用ID
	deleted int8 DEFAULT 0 NOT NULL, -- 软删除标识
	creator int8 NOT NULL, -- 创建人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新人ID
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	lock_version int4 DEFAULT 0 NOT NULL, -- 版本锁标识
	tenant_id int8 NOT NULL, -- 租户ID
	datasource_origin int4 DEFAULT 0 NOT NULL, -- 数据源来源 0.系统默认，1.自有数据源，2 外部数据源
	CONSTRAINT metadata_datasource_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_datasource_tenant_app ON public.metadata_datasource USING btree (app_id, tenant_id, deleted);
CREATE INDEX idx_datasource_type ON public.metadata_datasource USING btree (datasource_type, deleted);
CREATE UNIQUE INDEX uk_datasource_code ON public.metadata_datasource USING btree (code, app_id, tenant_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_datasource IS '数据源表';

-- Column comments

COMMENT ON COLUMN public.metadata_datasource.id IS '主键ID';
COMMENT ON COLUMN public.metadata_datasource.datasource_name IS '数据源名称';
COMMENT ON COLUMN public.metadata_datasource.code IS '数据源编码';
COMMENT ON COLUMN public.metadata_datasource.datasource_type IS '数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)';
COMMENT ON COLUMN public.metadata_datasource.config IS '数据源配置信息(JSON格式存储所有连接参数)';
COMMENT ON COLUMN public.metadata_datasource.description IS '描述';
COMMENT ON COLUMN public.metadata_datasource.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN public.metadata_datasource.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_datasource.deleted IS '软删除标识';
COMMENT ON COLUMN public.metadata_datasource.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_datasource.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_datasource.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_datasource.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_datasource.lock_version IS '版本锁标识';
COMMENT ON COLUMN public.metadata_datasource.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_datasource.datasource_origin IS '数据源来源 0.系统默认，1.自有数据源，2 外部数据源';


-- public.metadata_entity_field definition

-- Drop table

-- DROP TABLE public.metadata_entity_field;

CREATE TABLE public.metadata_entity_field (
	id int8 NOT NULL, -- 主键ID
	entity_id int8 NOT NULL, -- 实体ID
	field_name varchar(128) NOT NULL, -- 字段名称
	display_name varchar(128) NOT NULL, -- 显示名称
	field_type varchar(64) NOT NULL, -- 字段类型
	data_length int4 NULL, -- 数据长度
	decimal_places int4 NULL, -- 小数位数
	default_value text NULL, -- 默认值
	description varchar(256) NULL, -- 字段描述
	is_system_field int4 DEFAULT 0 NOT NULL, -- 是否系统字段：1-是，0-不是
	is_primary_key int4 DEFAULT 0 NOT NULL, -- 是否主键：1-是，0-不是
	is_required int4 DEFAULT 0 NOT NULL, -- 是否必填：1-是，0-不是
	is_unique int4 DEFAULT 0 NOT NULL, -- 是否唯一：1-是，0-不是
	allow_null int4 DEFAULT 1 NOT NULL, -- 是否允许空值：1-是，0-不是
	sort_order int4 DEFAULT 0 NOT NULL, -- 排序
	validation_rules text NULL, -- 校验规则表达式
	run_mode int4 DEFAULT 0 NOT NULL, -- 运行模式：0 编辑态，1 运行态
	app_id int8 NOT NULL, -- 应用ID
	deleted int8 DEFAULT 0 NOT NULL, -- 软删除标识
	creator int8 NOT NULL, -- 创建人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新人ID
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	lock_version int4 DEFAULT 0 NOT NULL, -- 版本锁标识
	tenant_id int8 NOT NULL, -- 租户ID
	status int4 DEFAULT 1 NULL, -- 字段状态：1-开启，0-关闭
	field_code varchar(255) NULL, -- 字段编码
	CONSTRAINT metadata_entity_field_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_field_entity ON public.metadata_entity_field USING btree (entity_id, deleted);
CREATE INDEX idx_field_tenant_app ON public.metadata_entity_field USING btree (tenant_id, app_id, deleted);
CREATE UNIQUE INDEX uk_field_name ON public.metadata_entity_field USING btree (entity_id, field_name) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_entity_field IS '实体字段表';

-- Column comments

COMMENT ON COLUMN public.metadata_entity_field.id IS '主键ID';
COMMENT ON COLUMN public.metadata_entity_field.entity_id IS '实体ID';
COMMENT ON COLUMN public.metadata_entity_field.field_name IS '字段名称';
COMMENT ON COLUMN public.metadata_entity_field.display_name IS '显示名称';
COMMENT ON COLUMN public.metadata_entity_field.field_type IS '字段类型';
COMMENT ON COLUMN public.metadata_entity_field.data_length IS '数据长度';
COMMENT ON COLUMN public.metadata_entity_field.decimal_places IS '小数位数';
COMMENT ON COLUMN public.metadata_entity_field.default_value IS '默认值';
COMMENT ON COLUMN public.metadata_entity_field.description IS '字段描述';
COMMENT ON COLUMN public.metadata_entity_field.is_system_field IS '是否系统字段：1-是，0-不是';
COMMENT ON COLUMN public.metadata_entity_field.is_primary_key IS '是否主键：1-是，0-不是';
COMMENT ON COLUMN public.metadata_entity_field.is_required IS '是否必填：1-是，0-不是';
COMMENT ON COLUMN public.metadata_entity_field.is_unique IS '是否唯一：1-是，0-不是';
COMMENT ON COLUMN public.metadata_entity_field.allow_null IS '是否允许空值：1-是，0-不是';
COMMENT ON COLUMN public.metadata_entity_field.sort_order IS '排序';
COMMENT ON COLUMN public.metadata_entity_field.validation_rules IS '校验规则表达式';
COMMENT ON COLUMN public.metadata_entity_field.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN public.metadata_entity_field.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_entity_field.deleted IS '软删除标识';
COMMENT ON COLUMN public.metadata_entity_field.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_entity_field.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_entity_field.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_entity_field.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_entity_field.lock_version IS '版本锁标识';
COMMENT ON COLUMN public.metadata_entity_field.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_entity_field.status IS '字段状态：1-开启，0-关闭';
COMMENT ON COLUMN public.metadata_entity_field.field_code IS '字段编码';


-- public.metadata_entity_field_constraint definition

-- Drop table

-- DROP TABLE public.metadata_entity_field_constraint;

CREATE TABLE public.metadata_entity_field_constraint (
	id int8 NOT NULL,
	field_id int8 NOT NULL,
	constraint_type varchar(32) NOT NULL, -- 约束类型：LENGTH_RANGE/REGEX
	min_length int4 NULL, -- 最小长度（LENGTH_RANGE有效）
	max_length int4 NULL, -- 最大长度（LENGTH_RANGE有效）
	regex_pattern varchar(512) NULL, -- 正则表达式（REGEX有效）
	prompt_message varchar(512) NULL, -- 提示信息
	is_enabled int4 DEFAULT 0 NOT NULL, -- 是否启用：1-启用，0-禁用
	run_mode int4 DEFAULT 0 NOT NULL,
	app_id int8 NOT NULL,
	tenant_id int8 NOT NULL,
	lock_version int4 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_metadata_entity_field_constraint PRIMARY KEY (id)
);
CREATE INDEX idx_field_constraint_field ON public.metadata_entity_field_constraint USING btree (field_id, deleted);
CREATE UNIQUE INDEX uk_field_constraint_type ON public.metadata_entity_field_constraint USING btree (field_id, constraint_type) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_entity_field_constraint IS '实体字段-约束定义（长度范围/正则校验）';

-- Column comments

COMMENT ON COLUMN public.metadata_entity_field_constraint.constraint_type IS '约束类型：LENGTH_RANGE/REGEX';
COMMENT ON COLUMN public.metadata_entity_field_constraint.min_length IS '最小长度（LENGTH_RANGE有效）';
COMMENT ON COLUMN public.metadata_entity_field_constraint.max_length IS '最大长度（LENGTH_RANGE有效）';
COMMENT ON COLUMN public.metadata_entity_field_constraint.regex_pattern IS '正则表达式（REGEX有效）';
COMMENT ON COLUMN public.metadata_entity_field_constraint.prompt_message IS '提示信息';
COMMENT ON COLUMN public.metadata_entity_field_constraint.is_enabled IS '是否启用：1-启用，0-禁用';


-- public.metadata_entity_field_option definition

-- Drop table

-- DROP TABLE public.metadata_entity_field_option;

CREATE TABLE public.metadata_entity_field_option (
	id int8 NOT NULL,
	field_id int8 NOT NULL, -- 关联字段ID（metadata_entity_field.id）
	option_label varchar(256) NOT NULL, -- 选项显示名称
	option_value varchar(256) NOT NULL, -- 选项值（字段内唯一）
	option_order int4 DEFAULT 0 NOT NULL, -- 选项排序
	is_enabled int4 DEFAULT 0 NOT NULL, -- 是否启用：1-启用，0-禁用
	description varchar(512) NULL,
	app_id int8 NOT NULL,
	tenant_id int8 NOT NULL,
	lock_version int4 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_metadata_entity_field_option PRIMARY KEY (id)
);
CREATE INDEX idx_field_option_field ON public.metadata_entity_field_option USING btree (field_id, deleted);
CREATE UNIQUE INDEX uk_field_option_value ON public.metadata_entity_field_option USING btree (field_id, option_value) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_entity_field_option IS '实体字段-选项值（用于单选/多选）';

-- Column comments

COMMENT ON COLUMN public.metadata_entity_field_option.field_id IS '关联字段ID（metadata_entity_field.id）';
COMMENT ON COLUMN public.metadata_entity_field_option.option_label IS '选项显示名称';
COMMENT ON COLUMN public.metadata_entity_field_option.option_value IS '选项值（字段内唯一）';
COMMENT ON COLUMN public.metadata_entity_field_option.option_order IS '选项排序';
COMMENT ON COLUMN public.metadata_entity_field_option.is_enabled IS '是否启用：1-启用，0-禁用';


-- public.metadata_entity_relationship definition

-- Drop table

-- DROP TABLE public.metadata_entity_relationship;

CREATE TABLE public.metadata_entity_relationship (
	id int8 NOT NULL, -- 主键ID
	relation_name varchar(128) NOT NULL, -- 关系名称
	source_entity_id int8 NOT NULL, -- 源实体ID
	target_entity_id int8 NOT NULL, -- 目标实体ID
	relationship_type varchar(32) NOT NULL, -- 关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)
	source_field_id varchar(128) NOT NULL, -- 源字段id
	target_field_id varchar(128) NOT NULL, -- 目标字段id
	cascade_type varchar(32) DEFAULT 'READ'::character varying NULL, -- 级联操作类型(read,all,delete,none)
	description varchar(256) NULL, -- 关系描述
	run_mode int4 DEFAULT 0 NOT NULL, -- 运行模式：0 编辑态，1 运行态
	app_id int8 NOT NULL, -- 应用ID
	deleted int8 DEFAULT 0 NOT NULL, -- 软删除标识
	creator int8 NOT NULL, -- 创建人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新人ID
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	lock_version int4 DEFAULT 0 NOT NULL, -- 版本锁标识
	tenant_id int8 NOT NULL, -- 租户ID
	CONSTRAINT metadata_entity_relationship_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_relationship_source ON public.metadata_entity_relationship USING btree (source_entity_id, deleted);
CREATE INDEX idx_relationship_target ON public.metadata_entity_relationship USING btree (target_entity_id, deleted);
CREATE INDEX idx_relationship_tenant_app ON public.metadata_entity_relationship USING btree (app_id, tenant_id, deleted);
COMMENT ON TABLE public.metadata_entity_relationship IS '实体关系表';

-- Column comments

COMMENT ON COLUMN public.metadata_entity_relationship.id IS '主键ID';
COMMENT ON COLUMN public.metadata_entity_relationship.relation_name IS '关系名称';
COMMENT ON COLUMN public.metadata_entity_relationship.source_entity_id IS '源实体ID';
COMMENT ON COLUMN public.metadata_entity_relationship.target_entity_id IS '目标实体ID';
COMMENT ON COLUMN public.metadata_entity_relationship.relationship_type IS '关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)';
COMMENT ON COLUMN public.metadata_entity_relationship.source_field_id IS '源字段id';
COMMENT ON COLUMN public.metadata_entity_relationship.target_field_id IS '目标字段id';
COMMENT ON COLUMN public.metadata_entity_relationship.cascade_type IS '级联操作类型(read,all,delete,none)';
COMMENT ON COLUMN public.metadata_entity_relationship.description IS '关系描述';
COMMENT ON COLUMN public.metadata_entity_relationship.run_mode IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN public.metadata_entity_relationship.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_entity_relationship.deleted IS '软删除标识';
COMMENT ON COLUMN public.metadata_entity_relationship.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_entity_relationship.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_entity_relationship.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_entity_relationship.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_entity_relationship.lock_version IS '版本锁标识';
COMMENT ON COLUMN public.metadata_entity_relationship.tenant_id IS '租户ID';


-- public.metadata_field_type_mapping definition

-- Drop table

-- DROP TABLE public.metadata_field_type_mapping;

CREATE TABLE public.metadata_field_type_mapping (
	id int8 DEFAULT nextval('field_type_mapping_id_seq'::regclass) NOT NULL, -- 主键ID
	business_field_type varchar(50) NOT NULL, -- 业务字段类型
	business_meaning text NOT NULL, -- 业务含义
	database_type varchar(20) NOT NULL, -- 数据库类型
	database_field varchar(100) NOT NULL, -- 数据库中对应的字段
	is_default int4 DEFAULT 0 NULL, -- 默认还是备选：0-备选，1-默认
	default_length int4 DEFAULT 255 NULL, -- 默认长度
	default_decimal_places int4 DEFAULT 0 NULL, -- 默认小数点后长度
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间
	CONSTRAINT field_type_mapping_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.metadata_field_type_mapping IS '字段类型映射表';

-- Column comments

COMMENT ON COLUMN public.metadata_field_type_mapping.id IS '主键ID';
COMMENT ON COLUMN public.metadata_field_type_mapping.business_field_type IS '业务字段类型';
COMMENT ON COLUMN public.metadata_field_type_mapping.business_meaning IS '业务含义';
COMMENT ON COLUMN public.metadata_field_type_mapping.database_type IS '数据库类型';
COMMENT ON COLUMN public.metadata_field_type_mapping.database_field IS '数据库中对应的字段';
COMMENT ON COLUMN public.metadata_field_type_mapping.is_default IS '默认还是备选：0-备选，1-默认';
COMMENT ON COLUMN public.metadata_field_type_mapping.default_length IS '默认长度';
COMMENT ON COLUMN public.metadata_field_type_mapping.default_decimal_places IS '默认小数点后长度';
COMMENT ON COLUMN public.metadata_field_type_mapping.created_at IS '创建时间';
COMMENT ON COLUMN public.metadata_field_type_mapping.updated_at IS '更新时间';


-- public.metadata_permit_ref_otft definition

-- Drop table

-- DROP TABLE public.metadata_permit_ref_otft;

CREATE TABLE public.metadata_permit_ref_otft (
	id int8 NOT NULL, -- 主键Id
	field_type_id int8 NOT NULL, -- 字段类型Id
	validation_type_id int8 NOT NULL, -- 操作符号Id
	sort_order int4 NOT NULL, -- 排序
	creator int8 NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updater int8 NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	deleted int8 DEFAULT 0 NULL
);
COMMENT ON TABLE public.metadata_permit_ref_otft IS '数据权限-操作符号与字段类型的关联表';

-- Column comments

COMMENT ON COLUMN public.metadata_permit_ref_otft.id IS '主键Id';
COMMENT ON COLUMN public.metadata_permit_ref_otft.field_type_id IS '字段类型Id';
COMMENT ON COLUMN public.metadata_permit_ref_otft.validation_type_id IS '操作符号Id';
COMMENT ON COLUMN public.metadata_permit_ref_otft.sort_order IS '排序';


-- public.metadata_system_fields definition

-- Drop table

-- DROP TABLE public.metadata_system_fields;

CREATE TABLE public.metadata_system_fields (
	id bigserial NOT NULL, -- 主键ID
	field_name varchar(50) NOT NULL, -- 字段名
	field_type varchar(20) NOT NULL, -- 字段类型
	is_snowflake_id int4 DEFAULT 0 NOT NULL, -- 是否为雪花ID：1-是，0-否
	is_required int4 DEFAULT 0 NOT NULL, -- 是否必填：1-是，0-否
	default_value varchar(100) NULL, -- 默认值
	description text NULL, -- 字段说明
	is_enabled int4 DEFAULT 0 NOT NULL, -- 是否启用：1-启用，0-禁用
	display_name varchar(255) NULL, -- 对外展示名称
	CONSTRAINT metadata_system_fields_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.metadata_system_fields IS '元数据系统字段维护表';

-- Column comments

COMMENT ON COLUMN public.metadata_system_fields.id IS '主键ID';
COMMENT ON COLUMN public.metadata_system_fields.field_name IS '字段名';
COMMENT ON COLUMN public.metadata_system_fields.field_type IS '字段类型';
COMMENT ON COLUMN public.metadata_system_fields.is_snowflake_id IS '是否为雪花ID：1-是，0-否';
COMMENT ON COLUMN public.metadata_system_fields.is_required IS '是否必填：1-是，0-否';
COMMENT ON COLUMN public.metadata_system_fields.default_value IS '默认值';
COMMENT ON COLUMN public.metadata_system_fields.description IS '字段说明';
COMMENT ON COLUMN public.metadata_system_fields.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_system_fields.display_name IS '对外展示名称';


-- public.metadata_validation_rule_group definition

-- Drop table

-- DROP TABLE public.metadata_validation_rule_group;

CREATE TABLE public.metadata_validation_rule_group (
	id int8 NOT NULL, -- 规则组唯一标识
	rg_name varchar(100) NOT NULL, -- 规则组名称，如"客户信用评级规则"
	rg_desc text NULL, -- 规则组描述
	lock_version int8 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	val_method varchar(255) NULL, -- 校验方式，如：满足条件时，不允许提交表单，并弹窗提示
	pop_prompt varchar(255) NULL, -- 弹窗提示内容
	pop_type varchar(255) NULL, -- 弹窗类型，如：短提示弹窗，长提示弹窗等
	rg_status int4 DEFAULT 1 NOT NULL, -- 状态：1-激活，0-非激活
	validation_type varchar(50) NULL, -- 校验类型：REQUIRED/UNIQUE/LENGTH/RANGE/FORMAT/CHILD_NOT_EMPTY/SELF_DEFINED
	entity_id int8 NULL, -- 实体 id
	CONSTRAINT metadata_validation_rule_group_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_metadata_validation_rule_group_tenant ON public.metadata_validation_rule_group USING btree (tenant_id);
COMMENT ON TABLE public.metadata_validation_rule_group IS '规则组表';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_rule_group.id IS '规则组唯一标识';
COMMENT ON COLUMN public.metadata_validation_rule_group.rg_name IS '规则组名称，如"客户信用评级规则"';
COMMENT ON COLUMN public.metadata_validation_rule_group.rg_desc IS '规则组描述';
COMMENT ON COLUMN public.metadata_validation_rule_group.val_method IS '校验方式，如：满足条件时，不允许提交表单，并弹窗提示';
COMMENT ON COLUMN public.metadata_validation_rule_group.pop_prompt IS '弹窗提示内容';
COMMENT ON COLUMN public.metadata_validation_rule_group.pop_type IS '弹窗类型，如：短提示弹窗，长提示弹窗等';
COMMENT ON COLUMN public.metadata_validation_rule_group.rg_status IS '状态：1-激活，0-非激活';
COMMENT ON COLUMN public.metadata_validation_rule_group.validation_type IS '校验类型：REQUIRED/UNIQUE/LENGTH/RANGE/FORMAT/CHILD_NOT_EMPTY/SELF_DEFINED';
COMMENT ON COLUMN public.metadata_validation_rule_group.entity_id IS '实体 id';


-- public.metadata_validation_type definition

-- Drop table

-- DROP TABLE public.metadata_validation_type;

CREATE TABLE public.metadata_validation_type (
	id bigserial NOT NULL,
	validation_code varchar(50) NOT NULL, -- 校验类型编码
	validation_name varchar(100) NOT NULL, -- 校验类型名称
	validation_desc text NULL, -- 校验类型描述
	sort_order int4 DEFAULT 0 NULL, -- 排序顺序
	status int2 DEFAULT 1 NULL, -- 状态：1-启用，0-禁用
	creator int8 NULL, -- 创建人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间
	updater int8 NULL, -- 更新人ID
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间
	deleted int2 DEFAULT 0 NULL, -- 删除标识：0-未删除，1-已删除
	"type" int2 NULL, -- 类型（给应用过滤用）
	CONSTRAINT metadata_validation_type_pkey PRIMARY KEY (id),
	CONSTRAINT metadata_validation_type_validation_code_key UNIQUE (validation_code)
);
CREATE INDEX idx_validation_type_code ON public.metadata_validation_type USING btree (validation_code);
CREATE INDEX idx_validation_type_status ON public.metadata_validation_type USING btree (status);
COMMENT ON TABLE public.metadata_validation_type IS '元数据校验类型表';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_type.validation_code IS '校验类型编码';
COMMENT ON COLUMN public.metadata_validation_type.validation_name IS '校验类型名称';
COMMENT ON COLUMN public.metadata_validation_type.validation_desc IS '校验类型描述';
COMMENT ON COLUMN public.metadata_validation_type.sort_order IS '排序顺序';
COMMENT ON COLUMN public.metadata_validation_type.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_type.creator IS '创建人ID';
COMMENT ON COLUMN public.metadata_validation_type.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_type.updater IS '更新人ID';
COMMENT ON COLUMN public.metadata_validation_type.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_type.deleted IS '删除标识：0-未删除，1-已删除';
COMMENT ON COLUMN public.metadata_validation_type."type" IS '类型（给应用过滤用）';


-- public.metadata_validation_child_not_empty definition

-- Drop table

-- DROP TABLE public.metadata_validation_child_not_empty;

CREATE TABLE public.metadata_validation_child_not_empty (
	id int8 NOT NULL, -- 主键（雪花/外部生成）
	group_id int8 NOT NULL, -- 规则组ID，关联metadata_validation_rule_group.id
	entity_id int8 NOT NULL, -- 父业务实体ID，关联metadata_business_entity.id
	field_id int8 NOT NULL, -- 父实体中指向子表的字段ID，关联metadata_entity_field.id
	child_entity_id int8 NOT NULL, -- 子业务实体ID，关联metadata_business_entity.id
	is_enabled int4 DEFAULT 1 NOT NULL, -- 是否启用：1-启用，0-禁用
	min_rows int4 DEFAULT 1 NOT NULL, -- 最少行数（默认1）
	prompt_message varchar(512) NULL, -- 提示信息
	run_mode int4 NULL, -- 运行模式：0-编辑态，1-运行态
	app_id int8 NULL, -- 应用ID
	tenant_id int8 NULL, -- 租户ID
	creator int8 NULL, -- 创建人
	updater int8 NULL, -- 更新人
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int4 DEFAULT 0 NOT NULL, -- 软删除标记：0-未删除，1-已删除
	CONSTRAINT metadata_validation_child_not_empty_pkey PRIMARY KEY (id),
	CONSTRAINT fk_mvcn_child FOREIGN KEY (child_entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvcn_entity FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvcn_field FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id),
	CONSTRAINT fk_mvcn_group FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_mvcn_field ON public.metadata_validation_child_not_empty USING btree (entity_id, field_id) WHERE (deleted = 0);
CREATE INDEX idx_mvcn_group ON public.metadata_validation_child_not_empty USING btree (group_id);
CREATE UNIQUE INDEX uq_mvcn_active ON public.metadata_validation_child_not_empty USING btree (tenant_id, entity_id, field_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_validation_child_not_empty IS '字段校验-子表非空规则（一对多/聚合子表最少行数）';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_child_not_empty.id IS '主键（雪花/外部生成）';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.group_id IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.entity_id IS '父业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.field_id IS '父实体中指向子表的字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.child_entity_id IS '子业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.min_rows IS '最少行数（默认1）';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.prompt_message IS '提示信息';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.run_mode IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.creator IS '创建人';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.updater IS '更新人';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_child_not_empty.deleted IS '软删除标记：0-未删除，1-已删除';


-- public.metadata_validation_format definition

-- Drop table

-- DROP TABLE public.metadata_validation_format;

CREATE TABLE public.metadata_validation_format (
	id int8 NOT NULL, -- 主键（雪花/外部生成）
	group_id int8 NOT NULL, -- 规则组ID，关联metadata_validation_rule_group.id
	entity_id int8 NOT NULL, -- 业务实体ID，关联metadata_business_entity.id
	field_id int8 NOT NULL, -- 实体字段ID，关联metadata_entity_field.id
	is_enabled int4 DEFAULT 1 NOT NULL, -- 是否启用：1-启用，0-禁用
	format_code varchar(64) NOT NULL, -- 格式类型：REGEX/EMAIL/MOBILE/ID_CARD/URL/IP/...
	regex_pattern text NULL, -- 当format_code=REGEX时的正则表达式
	flags varchar(64) NULL, -- 正则标志位：i/m/s等
	prompt_message varchar(512) NULL, -- 提示信息
	run_mode int4 NULL, -- 运行模式：0-编辑态，1-运行态
	app_id int8 NULL, -- 应用ID
	tenant_id int8 NULL, -- 租户ID
	creator int8 NULL, -- 创建人
	updater int8 NULL, -- 更新人
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int4 DEFAULT 0 NOT NULL, -- 软删除标记：0-未删除，1-已删除
	CONSTRAINT ck_mvfmt_regex CHECK ((((format_code)::text <> 'REGEX'::text) OR (regex_pattern IS NOT NULL))),
	CONSTRAINT metadata_validation_format_pkey PRIMARY KEY (id),
	CONSTRAINT fk_mvfmt_entity FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvfmt_field FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id),
	CONSTRAINT fk_mvfmt_group FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_mvfmt_field ON public.metadata_validation_format USING btree (entity_id, field_id) WHERE (deleted = 0);
CREATE INDEX idx_mvfmt_group ON public.metadata_validation_format USING btree (group_id);
CREATE UNIQUE INDEX uq_mvfmt_active ON public.metadata_validation_format USING btree (tenant_id, entity_id, field_id, format_code) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_validation_format IS '字段校验-格式规则（内置格式与自定义正则）';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_format.id IS '主键（雪花/外部生成）';
COMMENT ON COLUMN public.metadata_validation_format.group_id IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN public.metadata_validation_format.entity_id IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_format.field_id IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN public.metadata_validation_format.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_format.format_code IS '格式类型：REGEX/EMAIL/MOBILE/ID_CARD/URL/IP/...';
COMMENT ON COLUMN public.metadata_validation_format.regex_pattern IS '当format_code=REGEX时的正则表达式';
COMMENT ON COLUMN public.metadata_validation_format.flags IS '正则标志位：i/m/s等';
COMMENT ON COLUMN public.metadata_validation_format.prompt_message IS '提示信息';
COMMENT ON COLUMN public.metadata_validation_format.run_mode IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN public.metadata_validation_format.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_validation_format.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_validation_format.creator IS '创建人';
COMMENT ON COLUMN public.metadata_validation_format.updater IS '更新人';
COMMENT ON COLUMN public.metadata_validation_format.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_format.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_format.deleted IS '软删除标记：0-未删除，1-已删除';


-- public.metadata_validation_length definition

-- Drop table

-- DROP TABLE public.metadata_validation_length;

CREATE TABLE public.metadata_validation_length (
	id int8 NOT NULL, -- 主键（雪花/外部生成）
	group_id int8 NOT NULL, -- 规则组ID，关联metadata_validation_rule_group.id
	entity_id int8 NOT NULL, -- 业务实体ID，关联metadata_business_entity.id
	field_id int8 NOT NULL, -- 实体字段ID，关联metadata_entity_field.id
	is_enabled int4 DEFAULT 1 NOT NULL, -- 是否启用：1-启用，0-禁用
	min_length int4 NULL, -- 最小长度（为空则不校验下限）
	max_length int4 NULL, -- 最大长度（为空则不校验上限）
	trim_before int4 DEFAULT 1 NULL, -- 校验前是否trim：1-是，0-否
	prompt_message varchar(512) NULL, -- 提示信息
	run_mode int4 NULL, -- 运行模式：0-编辑态，1-运行态
	app_id int8 NULL, -- 应用ID
	tenant_id int8 NULL, -- 租户ID
	creator int8 NULL, -- 创建人
	updater int8 NULL, -- 更新人
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int4 DEFAULT 0 NOT NULL, -- 软删除标记：0-未删除，1-已删除
	CONSTRAINT ck_mvl_len CHECK (((min_length IS NULL) OR (max_length IS NULL) OR (min_length <= max_length))),
	CONSTRAINT metadata_validation_length_pkey PRIMARY KEY (id),
	CONSTRAINT fk_mvl_entity FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvl_field FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id),
	CONSTRAINT fk_mvl_group FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_mvl_field ON public.metadata_validation_length USING btree (entity_id, field_id) WHERE (deleted = 0);
CREATE INDEX idx_mvl_group ON public.metadata_validation_length USING btree (group_id);
CREATE UNIQUE INDEX uq_mvl_active ON public.metadata_validation_length USING btree (tenant_id, entity_id, field_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_validation_length IS '字段校验-长度规则';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_length.id IS '主键（雪花/外部生成）';
COMMENT ON COLUMN public.metadata_validation_length.group_id IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN public.metadata_validation_length.entity_id IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_length.field_id IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN public.metadata_validation_length.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_length.min_length IS '最小长度（为空则不校验下限）';
COMMENT ON COLUMN public.metadata_validation_length.max_length IS '最大长度（为空则不校验上限）';
COMMENT ON COLUMN public.metadata_validation_length.trim_before IS '校验前是否trim：1-是，0-否';
COMMENT ON COLUMN public.metadata_validation_length.prompt_message IS '提示信息';
COMMENT ON COLUMN public.metadata_validation_length.run_mode IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN public.metadata_validation_length.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_validation_length.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_validation_length.creator IS '创建人';
COMMENT ON COLUMN public.metadata_validation_length.updater IS '更新人';
COMMENT ON COLUMN public.metadata_validation_length.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_length.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_length.deleted IS '软删除标记：0-未删除，1-已删除';


-- public.metadata_validation_range definition

-- Drop table

-- DROP TABLE public.metadata_validation_range;

CREATE TABLE public.metadata_validation_range (
	id int8 NOT NULL, -- 主键（雪花/外部生成）
	group_id int8 NOT NULL, -- 规则组ID，关联metadata_validation_rule_group.id
	entity_id int8 NOT NULL, -- 业务实体ID，关联metadata_business_entity.id
	field_id int8 NOT NULL, -- 实体字段ID，关联metadata_entity_field.id
	is_enabled int4 DEFAULT 1 NOT NULL, -- 是否启用：1-启用，0-禁用
	range_type varchar(16) NOT NULL, -- 范围类型：NUMBER/DATE
	min_value numeric(38, 10) NULL, -- 最小值（Number类型）
	max_value numeric(38, 10) NULL, -- 最大值（Number类型）
	min_date timestamp NULL, -- 最小时间（Date类型）
	max_date timestamp NULL, -- 最大时间（Date类型）
	include_min int4 DEFAULT 1 NULL, -- 是否包含最小边界：1-包含，0-不包含
	include_max int4 DEFAULT 1 NULL, -- 是否包含最大边界：1-包含，0-不包含
	prompt_message varchar(512) NULL, -- 提示信息
	run_mode int4 NULL, -- 运行模式：0-编辑态，1-运行态
	app_id int8 NULL, -- 应用ID
	tenant_id int8 NULL, -- 租户ID
	creator int8 NULL, -- 创建人
	updater int8 NULL, -- 更新人
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int4 DEFAULT 0 NOT NULL, -- 软删除标记：0-未删除，1-已删除
	CONSTRAINT ck_mvr_type CHECK (((range_type)::text = ANY ((ARRAY['NUMBER'::character varying, 'DATE'::character varying])::text[]))),
	CONSTRAINT ck_mvr_value CHECK (((((range_type)::text = 'NUMBER'::text) AND (min_date IS NULL) AND (max_date IS NULL) AND ((min_value IS NULL) OR (max_value IS NULL) OR (min_value <= max_value))) OR (((range_type)::text = 'DATE'::text) AND (min_value IS NULL) AND (max_value IS NULL) AND ((min_date IS NULL) OR (max_date IS NULL) OR (min_date <= max_date))))),
	CONSTRAINT metadata_validation_range_pkey PRIMARY KEY (id),
	CONSTRAINT fk_mvrg_entity FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvrg_field FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id),
	CONSTRAINT fk_mvrg_group FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_mvrg_field ON public.metadata_validation_range USING btree (entity_id, field_id) WHERE (deleted = 0);
CREATE INDEX idx_mvrg_group ON public.metadata_validation_range USING btree (group_id);
CREATE UNIQUE INDEX uq_mvrg_active ON public.metadata_validation_range USING btree (tenant_id, entity_id, field_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_validation_range IS '字段校验-范围规则（数值/日期）';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_range.id IS '主键（雪花/外部生成）';
COMMENT ON COLUMN public.metadata_validation_range.group_id IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN public.metadata_validation_range.entity_id IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_range.field_id IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN public.metadata_validation_range.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_range.range_type IS '范围类型：NUMBER/DATE';
COMMENT ON COLUMN public.metadata_validation_range.min_value IS '最小值（Number类型）';
COMMENT ON COLUMN public.metadata_validation_range.max_value IS '最大值（Number类型）';
COMMENT ON COLUMN public.metadata_validation_range.min_date IS '最小时间（Date类型）';
COMMENT ON COLUMN public.metadata_validation_range.max_date IS '最大时间（Date类型）';
COMMENT ON COLUMN public.metadata_validation_range.include_min IS '是否包含最小边界：1-包含，0-不包含';
COMMENT ON COLUMN public.metadata_validation_range.include_max IS '是否包含最大边界：1-包含，0-不包含';
COMMENT ON COLUMN public.metadata_validation_range.prompt_message IS '提示信息';
COMMENT ON COLUMN public.metadata_validation_range.run_mode IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN public.metadata_validation_range.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_validation_range.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_validation_range.creator IS '创建人';
COMMENT ON COLUMN public.metadata_validation_range.updater IS '更新人';
COMMENT ON COLUMN public.metadata_validation_range.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_range.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_range.deleted IS '软删除标记：0-未删除，1-已删除';


-- public.metadata_validation_required definition

-- Drop table

-- DROP TABLE public.metadata_validation_required;

CREATE TABLE public.metadata_validation_required (
	id int8 NOT NULL, -- 主键（雪花/外部生成）
	group_id int8 NOT NULL, -- 规则组ID，关联metadata_validation_rule_group.id
	entity_id int8 NOT NULL, -- 业务实体ID，关联metadata_business_entity.id
	field_id int8 NOT NULL, -- 实体字段ID，关联metadata_entity_field.id
	is_enabled int4 DEFAULT 1 NOT NULL, -- 是否启用：1-启用，0-禁用
	prompt_message varchar(512) NULL, -- 提示信息（触发校验时给用户的提示）
	run_mode int4 NULL, -- 运行模式：0-编辑态，1-运行态
	app_id int8 NULL, -- 应用ID
	tenant_id int8 NULL, -- 租户ID
	creator int8 NULL, -- 创建人
	updater int8 NULL, -- 更新人
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int4 DEFAULT 0 NOT NULL, -- 软删除标记：0-未删除，1-已删除
	CONSTRAINT metadata_validation_required_pkey PRIMARY KEY (id),
	CONSTRAINT fk_mvr_entity FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvr_field FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id),
	CONSTRAINT fk_mvr_group FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_mvr_field ON public.metadata_validation_required USING btree (entity_id, field_id) WHERE (deleted = 0);
CREATE INDEX idx_mvr_group ON public.metadata_validation_required USING btree (group_id);
CREATE UNIQUE INDEX uq_mvr_active ON public.metadata_validation_required USING btree (tenant_id, entity_id, field_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_validation_required IS '字段校验-必填规则';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_required.id IS '主键（雪花/外部生成）';
COMMENT ON COLUMN public.metadata_validation_required.group_id IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN public.metadata_validation_required.entity_id IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_required.field_id IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN public.metadata_validation_required.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_required.prompt_message IS '提示信息（触发校验时给用户的提示）';
COMMENT ON COLUMN public.metadata_validation_required.run_mode IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN public.metadata_validation_required.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_validation_required.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_validation_required.creator IS '创建人';
COMMENT ON COLUMN public.metadata_validation_required.updater IS '更新人';
COMMENT ON COLUMN public.metadata_validation_required.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_required.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_required.deleted IS '软删除标记：0-未删除，1-已删除';


-- public.metadata_validation_rule_definition definition

-- Drop table

-- DROP TABLE public.metadata_validation_rule_definition;

CREATE TABLE public.metadata_validation_rule_definition (
	id int8 NOT NULL, -- 规则唯一标识
	group_id int8 NOT NULL, -- 所属规则组 ID，关联 metadata_validation_rule_group 表的 id
	parent_rule_id int8 NULL, -- 父规则 ID，用于层级关系；顶级规则为 NULL
	entity_id int8 NULL, -- 关联的业务实体ID，关联 metadata_business_entity 表的 id
	field_id int8 NULL, -- 关联的实体字段ID，关联 metadata_entity_field 表的 id
	logic_type varchar(50) NOT NULL, -- 逻辑类型："LOGIC"（逻辑操作符）/"CONDITION"（条件判断）
	"operator" varchar(50) DEFAULT NULL::character varying NULL, -- logic_type="CONDITION" 时，取值为 ">"/"<"/"="/"IN"/"BETWEEN" 等
	logic_operator varchar(50) DEFAULT NULL::character varying NULL, -- 当 logic_type="LOGIC" 时，取值为 "AND"/"OR"
	field_code varchar(50) DEFAULT NULL::character varying NULL, -- 条件字段编码，如 "AGE"、"INCOME"、"CUSTOMER_LEVEL"（仅 logic_type="CONDITION" 时有效）
	field_value int8 NULL, -- 条件值引用（单值条件或范围表达式的第一个）
	field_value2 int8 NULL, -- 条件值引用2（单值条件或范围表达式的第二个）
	lock_version int8 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	status int4 DEFAULT 1 NOT NULL, -- 状态：1-激活，0-非激活
	CONSTRAINT metadata_validation_rule_definition_pkey PRIMARY KEY (id),
	CONSTRAINT metadata_validation_rule_definition_entity_id_fkey FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id) ON DELETE SET NULL ON UPDATE CASCADE,
	CONSTRAINT metadata_validation_rule_definition_field_id_fkey FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id) ON DELETE SET NULL ON UPDATE CASCADE,
	CONSTRAINT metadata_validation_rule_definition_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT metadata_validation_rule_definition_parent_rule_id_fkey FOREIGN KEY (parent_rule_id) REFERENCES public.metadata_validation_rule_definition(id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX idx_metadata_validation_rule_definition_entity_id ON public.metadata_validation_rule_definition USING btree (entity_id);
CREATE INDEX idx_metadata_validation_rule_definition_field_id ON public.metadata_validation_rule_definition USING btree (field_id);
CREATE INDEX idx_metadata_validation_rule_definition_group_id ON public.metadata_validation_rule_definition USING btree (group_id);
CREATE INDEX idx_metadata_validation_rule_definition_logic_type ON public.metadata_validation_rule_definition USING btree (logic_type);
CREATE INDEX idx_metadata_validation_rule_definition_parent_rule_id ON public.metadata_validation_rule_definition USING btree (parent_rule_id);
CREATE INDEX idx_metadata_validation_rule_definition_tenant ON public.metadata_validation_rule_definition USING btree (tenant_id);
COMMENT ON TABLE public.metadata_validation_rule_definition IS '规则定义表';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_rule_definition.id IS '规则唯一标识';
COMMENT ON COLUMN public.metadata_validation_rule_definition.group_id IS '所属规则组 ID，关联 metadata_validation_rule_group 表的 id';
COMMENT ON COLUMN public.metadata_validation_rule_definition.parent_rule_id IS '父规则 ID，用于层级关系；顶级规则为 NULL';
COMMENT ON COLUMN public.metadata_validation_rule_definition.entity_id IS '关联的业务实体ID，关联 metadata_business_entity 表的 id';
COMMENT ON COLUMN public.metadata_validation_rule_definition.field_id IS '关联的实体字段ID，关联 metadata_entity_field 表的 id';
COMMENT ON COLUMN public.metadata_validation_rule_definition.logic_type IS '逻辑类型："LOGIC"（逻辑操作符）/"CONDITION"（条件判断）';
COMMENT ON COLUMN public.metadata_validation_rule_definition."operator" IS 'logic_type="CONDITION" 时，取值为 ">"/"<"/"="/"IN"/"BETWEEN" 等';
COMMENT ON COLUMN public.metadata_validation_rule_definition.logic_operator IS '当 logic_type="LOGIC" 时，取值为 "AND"/"OR"';
COMMENT ON COLUMN public.metadata_validation_rule_definition.field_code IS '条件字段编码，如 "AGE"、"INCOME"、"CUSTOMER_LEVEL"（仅 logic_type="CONDITION" 时有效）';
COMMENT ON COLUMN public.metadata_validation_rule_definition.field_value IS '条件值引用（单值条件或范围表达式的第一个）';
COMMENT ON COLUMN public.metadata_validation_rule_definition.field_value2 IS '条件值引用2（单值条件或范围表达式的第二个）';
COMMENT ON COLUMN public.metadata_validation_rule_definition.status IS '状态：1-激活，0-非激活';


-- public.metadata_validation_unique definition

-- Drop table

-- DROP TABLE public.metadata_validation_unique;

CREATE TABLE public.metadata_validation_unique (
	id int8 NOT NULL, -- 主键（雪花/外部生成）
	group_id int8 NOT NULL, -- 规则组ID，关联metadata_validation_rule_group.id
	entity_id int8 NOT NULL, -- 业务实体ID，关联metadata_business_entity.id
	field_id int8 NOT NULL, -- 实体字段ID，关联metadata_entity_field.id
	is_enabled int4 DEFAULT 1 NOT NULL, -- 是否启用：1-启用，0-禁用
	prompt_message varchar(512) NULL, -- 提示信息
	run_mode int4 NULL, -- 运行模式：0-编辑态，1-运行态
	app_id int8 NULL, -- 应用ID
	tenant_id int8 NULL, -- 租户ID
	creator int8 NULL, -- 创建人
	updater int8 NULL, -- 更新人
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int4 DEFAULT 0 NOT NULL, -- 软删除标记：0-未删除，1-已删除
	CONSTRAINT metadata_validation_unique_pkey PRIMARY KEY (id),
	CONSTRAINT fk_mvu_entity FOREIGN KEY (entity_id) REFERENCES public.metadata_business_entity(id),
	CONSTRAINT fk_mvu_field FOREIGN KEY (field_id) REFERENCES public.metadata_entity_field(id),
	CONSTRAINT fk_mvu_group FOREIGN KEY (group_id) REFERENCES public.metadata_validation_rule_group(id) ON DELETE CASCADE
);
CREATE INDEX idx_mvu_field ON public.metadata_validation_unique USING btree (entity_id, field_id) WHERE (deleted = 0);
CREATE INDEX idx_mvu_group ON public.metadata_validation_unique USING btree (group_id);
CREATE UNIQUE INDEX uq_mvu_active ON public.metadata_validation_unique USING btree (tenant_id, entity_id, field_id) WHERE (deleted = 0);
COMMENT ON TABLE public.metadata_validation_unique IS '字段校验-唯一规则';

-- Column comments

COMMENT ON COLUMN public.metadata_validation_unique.id IS '主键（雪花/外部生成）';
COMMENT ON COLUMN public.metadata_validation_unique.group_id IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN public.metadata_validation_unique.entity_id IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN public.metadata_validation_unique.field_id IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN public.metadata_validation_unique.is_enabled IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN public.metadata_validation_unique.prompt_message IS '提示信息';
COMMENT ON COLUMN public.metadata_validation_unique.run_mode IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN public.metadata_validation_unique.app_id IS '应用ID';
COMMENT ON COLUMN public.metadata_validation_unique.tenant_id IS '租户ID';
COMMENT ON COLUMN public.metadata_validation_unique.creator IS '创建人';
COMMENT ON COLUMN public.metadata_validation_unique.updater IS '更新人';
COMMENT ON COLUMN public.metadata_validation_unique.create_time IS '创建时间';
COMMENT ON COLUMN public.metadata_validation_unique.update_time IS '更新时间';
COMMENT ON COLUMN public.metadata_validation_unique.deleted IS '软删除标记：0-未删除，1-已删除';
