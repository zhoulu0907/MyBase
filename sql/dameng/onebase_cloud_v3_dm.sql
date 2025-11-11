-- -------------------------------------------------------------
-- 达梦数据库(DM) DDL - 由PostgreSQL转换
-- 转换日期: 2025-11-04
-- 数据库: onebase_cloud_v3
-- 
-- 关键修改:
-- 1. 整型数据类型: int8→BIGINT, int4→INTEGER, int2→SMALLINT (988处)
-- 2. 去除所有PostgreSQL类型转换语法(::character varying, ::bigint等) (150处)
-- 3. 移除schema前缀（达梦直接使用表名）
-- 4. 序列自增:使用IDENTITY(1,1)自增列,应用层无需修改,自动生成ID (8处)
-- 5. 简化CHECK约束: ARRAY/ANY语法 → IN语法，列名加双引号 (2处)
-- 6. 移除所有索引的USING btree语法（达梦不支持该语法） (204处)
-- 7. 索引定义中表名加双引号（保持与表定义一致，避免大小写不匹配） (204处)
-- 8. 索引定义中列名加双引号（保持与列定义一致，避免大小写不匹配） (204处,共约1200个列)
-- 9. bytea类型已转换为BLOB
-- 10. bpchar类型已转换为CHAR
--
-- 累计修复：1,762处兼容性问题
-- 
-- 执行前准备:
-- CREATE DATABASE onebase_cloud_v3 CHARSET 'UTF-8';
-- -- 以SYSDBA或有创建权限的用户连接到onebase_cloud_v3数据库后执行本脚本
-- -------------------------------------------------------------

-- -------------------------------------------------------------
-- TablePlus 6.7.1(636)
--
-- https://tableplus.com/
--
-- Database: onebase_cloud_v3
-- Generation Time: 2025-11-04 18:02:04.4340
-- -------------------------------------------------------------


-- Table Definition
CREATE TABLE "app_application_tag" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "tag_id" BIGINT NOT NULL,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_application_tag"."id" IS '主键Id';
COMMENT ON COLUMN "app_application_tag"."application_id" IS '应用Id';
COMMENT ON COLUMN "app_application_tag"."tag_id" IS '标签Id';

-- Table Definition
CREATE TABLE "app_auth_field" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "menu_id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "is_can_read" SMALLINT DEFAULT 0,
    "is_can_edit" SMALLINT DEFAULT 0,
    "is_can_download" SMALLINT DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_field"."id" IS '主键Id';
COMMENT ON COLUMN "app_auth_field"."application_id" IS '应用id';
COMMENT ON COLUMN "app_auth_field"."role_id" IS '角色id';
COMMENT ON COLUMN "app_auth_field"."menu_id" IS '菜单id';
COMMENT ON COLUMN "app_auth_field"."field_id" IS '字段id';
COMMENT ON COLUMN "app_auth_field"."is_can_read" IS '是否可阅读';
COMMENT ON COLUMN "app_auth_field"."is_can_edit" IS '是否可编辑';
COMMENT ON COLUMN "app_auth_field"."is_can_download" IS '是否可下载';

-- Table Definition
CREATE TABLE "metadata_datasource" (
    "id" BIGINT NOT NULL,
    "datasource_name" varchar(256) NOT NULL,
    "code" varchar(128) NOT NULL,
    "datasource_type" varchar(64) NOT NULL,
    "config" text NOT NULL,
    "description" text,
    "run_mode" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    "datasource_origin" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_datasource"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_datasource"."datasource_name" IS '数据源名称';
COMMENT ON COLUMN "metadata_datasource"."code" IS '数据源编码';
COMMENT ON COLUMN "metadata_datasource"."datasource_type" IS '数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)';
COMMENT ON COLUMN "metadata_datasource"."config" IS '数据源配置信息(JSON格式存储所有连接参数)';
COMMENT ON COLUMN "metadata_datasource"."description" IS '描述';
COMMENT ON COLUMN "metadata_datasource"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "metadata_datasource"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_datasource"."deleted" IS '软删除标识';
COMMENT ON COLUMN "metadata_datasource"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_datasource"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_datasource"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_datasource"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_datasource"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "metadata_datasource"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_datasource"."datasource_origin" IS '数据源来源 0.系统默认，1.自有数据源，2 外部数据源';

-- Table Definition
CREATE TABLE "infra_security_config" (
    "id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "config_key" varchar(100) NOT NULL,
    "config_value" text,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "app_auth_role" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "role_code" varchar(64) NOT NULL,
    "role_name" varchar(64) NOT NULL,
    "role_type" SMALLINT NOT NULL DEFAULT 3,
    "description" varchar(256) DEFAULT NULL,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_role"."id" IS '主键Id';
COMMENT ON COLUMN "app_auth_role"."application_id" IS '应用Id';
COMMENT ON COLUMN "app_auth_role"."role_code" IS '角色编码';
COMMENT ON COLUMN "app_auth_role"."role_name" IS '角色名称';
COMMENT ON COLUMN "app_auth_role"."role_type" IS '角色类型，1系统管理员2系统默认用户3用户定义';
COMMENT ON COLUMN "app_auth_role"."description" IS '描述';

-- Table Definition
CREATE TABLE "app_auth_role_user" (
    "id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_role_user"."id" IS '主键Id';
COMMENT ON COLUMN "app_auth_role_user"."role_id" IS '角色id';
COMMENT ON COLUMN "app_auth_role_user"."user_id" IS '用户Id';

-- Table Definition
CREATE TABLE "app_menu" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "entity_id" BIGINT,
    "parent_id" BIGINT NOT NULL,
    "menu_code" varchar(64) NOT NULL,
    "menu_sort" INTEGER NOT NULL DEFAULT 0,
    "menu_type" SMALLINT NOT NULL,
    "menu_name" varchar(64) NOT NULL,
    "menu_icon" varchar(64) NOT NULL,
    "action_target" varchar(256),
    "is_visible" SMALLINT NOT NULL DEFAULT 1,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_menu"."id" IS '主键Id';
COMMENT ON COLUMN "app_menu"."application_id" IS '应用Id';
COMMENT ON COLUMN "app_menu"."entity_id" IS '实体Id';
COMMENT ON COLUMN "app_menu"."parent_id" IS '父菜单id';
COMMENT ON COLUMN "app_menu"."menu_code" IS '菜单编码';
COMMENT ON COLUMN "app_menu"."menu_sort" IS '菜单排序';
COMMENT ON COLUMN "app_menu"."menu_type" IS '菜单类型 1 页面 2 目录';
COMMENT ON COLUMN "app_menu"."menu_name" IS '菜单名称';
COMMENT ON COLUMN "app_menu"."menu_icon" IS '菜单图标';
COMMENT ON COLUMN "app_menu"."action_target" IS '菜单动作';
COMMENT ON COLUMN "app_menu"."is_visible" IS '是否可见';

-- Table Definition
CREATE TABLE "app_version_resource" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "version_id" BIGINT NOT NULL,
    "res_type" varchar(64) NOT NULL,
    "res_data" text NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_version_resource"."id" IS '主键ID';
COMMENT ON COLUMN "app_version_resource"."application_id" IS '应用ID';
COMMENT ON COLUMN "app_version_resource"."version_id" IS '版本ID';
COMMENT ON COLUMN "app_version_resource"."res_type" IS '协议类型';
COMMENT ON COLUMN "app_version_resource"."res_data" IS '资源数据';

-- Table Definition
CREATE TABLE "infra_security_config_category" (
    "id" BIGINT NOT NULL,
    "category_code" varchar(50) NOT NULL,
    "category_name" varchar(100) NOT NULL,
    "description" text,
    "sort_order" INTEGER DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "app_application" (
    "id" BIGINT NOT NULL,
    "app_uid" varchar(16),
    "app_name" varchar(128) NOT NULL,
    "app_code" varchar(64) NOT NULL,
    "app_mode" varchar(64) NOT NULL,
    "theme_color" varchar(64) NOT NULL,
    "icon_name" varchar(256) DEFAULT NULL,
    "icon_color" varchar(32) DEFAULT NULL,
    "version_number" varchar(16) NOT NULL,
    "app_status" SMALLINT NOT NULL,
    "version_url" varchar(1024),
    "description" varchar(1024) DEFAULT NULL,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "publish_model" varchar NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_application"."id" IS '主键ID';
COMMENT ON COLUMN "app_application"."app_uid" IS '应用uid(自动生成短码)';
COMMENT ON COLUMN "app_application"."app_name" IS '应用名称';
COMMENT ON COLUMN "app_application"."app_code" IS '应用编码(用户输入)';
COMMENT ON COLUMN "app_application"."app_mode" IS '应用模式';
COMMENT ON COLUMN "app_application"."theme_color" IS '主题颜色';
COMMENT ON COLUMN "app_application"."icon_name" IS '应用图标';
COMMENT ON COLUMN "app_application"."icon_color" IS '图标颜色';
COMMENT ON COLUMN "app_application"."version_number" IS '当前版本';
COMMENT ON COLUMN "app_application"."app_status" IS '状态（编辑、发布）';
COMMENT ON COLUMN "app_application"."description" IS '应用描述';
COMMENT ON COLUMN "app_application"."publish_model" IS '发布模式默认内部模式0，saas1';

-- Table Definition
CREATE TABLE "metadata_entity_relationship" (
    "id" BIGINT NOT NULL,
    "relation_name" varchar(128) NOT NULL,
    "source_entity_id" BIGINT NOT NULL,
    "target_entity_id" BIGINT NOT NULL,
    "relationship_type" varchar(32) NOT NULL,
    "source_field_id" varchar(128) NOT NULL,
    "target_field_id" varchar(128) NOT NULL,
    "cascade_type" varchar(32) DEFAULT 'READ',
    "description" varchar(256),
    "run_mode" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_entity_relationship"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_entity_relationship"."relation_name" IS '关系名称';
COMMENT ON COLUMN "metadata_entity_relationship"."source_entity_id" IS '源实体ID';
COMMENT ON COLUMN "metadata_entity_relationship"."target_entity_id" IS '目标实体ID';
COMMENT ON COLUMN "metadata_entity_relationship"."relationship_type" IS '关系类型(ONE_TO_ONE,ONE_TO_MANY,MANY_TO_ONE,MANY_TO_MANY)';
COMMENT ON COLUMN "metadata_entity_relationship"."source_field_id" IS '源字段id';
COMMENT ON COLUMN "metadata_entity_relationship"."target_field_id" IS '目标字段id';
COMMENT ON COLUMN "metadata_entity_relationship"."cascade_type" IS '级联操作类型(read,all,delete,none)';
COMMENT ON COLUMN "metadata_entity_relationship"."description" IS '关系描述';
COMMENT ON COLUMN "metadata_entity_relationship"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "metadata_entity_relationship"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_entity_relationship"."deleted" IS '软删除标识';
COMMENT ON COLUMN "metadata_entity_relationship"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_entity_relationship"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_entity_relationship"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_entity_relationship"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_entity_relationship"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "metadata_entity_relationship"."tenant_id" IS '租户ID';

-- Table Definition
CREATE TABLE "infra_security_config_template" (
    "id" BIGINT NOT NULL,
    "category_id" BIGINT NOT NULL,
    "config_key" varchar(100) NOT NULL,
    "config_name" varchar(200) NOT NULL,
    "data_type" varchar(20) NOT NULL CHECK ("data_type" IN ('BOOLEAN', 'INTEGER', 'DECIMAL', 'STRING', 'JSON')),
    "default_value" text NOT NULL,
    "description" text,
    "sort_order" INTEGER DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "etl_flink_mapping" (
    "id" BIGINT NOT NULL,
    "datasource_type" varchar(64) NOT NULL,
    "origin_type" varchar(32) NOT NULL,
    "flink_type" varchar(32) NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_flink_mapping"."id" IS '主键ID';
COMMENT ON COLUMN "etl_flink_mapping"."datasource_type" IS '数据库类型';
COMMENT ON COLUMN "etl_flink_mapping"."origin_type" IS '原始列类型';
COMMENT ON COLUMN "etl_flink_mapping"."flink_type" IS '对应Flink类型';

-- Table Definition
CREATE TABLE "metadata_app_and_datasource" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "datasource_id" BIGINT NOT NULL,
    "datasource_type" varchar(64) NOT NULL,
    "app_uid" varchar(255) NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_app_and_datasource"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_app_and_datasource"."application_id" IS '应用ID';
COMMENT ON COLUMN "metadata_app_and_datasource"."datasource_id" IS '数据源ID';
COMMENT ON COLUMN "metadata_app_and_datasource"."datasource_type" IS '数据源类型(POSTGRESQL,MYSQL,KINGBASE,TDENGINE,CLICKHOUSE等)';
COMMENT ON COLUMN "metadata_app_and_datasource"."app_uid" IS '应用UID';
COMMENT ON COLUMN "metadata_app_and_datasource"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "metadata_app_and_datasource"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_app_and_datasource"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_app_and_datasource"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_app_and_datasource"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_app_and_datasource"."deleted" IS '软删除标识';
COMMENT ON COLUMN "metadata_app_and_datasource"."tenant_id" IS '租户ID';

-- Table Definition
CREATE TABLE "metadata_validation_rule_definition" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "parent_rule_id" BIGINT,
    "entity_id" BIGINT,
    "field_id" BIGINT,
    "logic_type" varchar(50) NOT NULL,
    "operator" varchar(50) DEFAULT NULL,
    "logic_operator" varchar(50) DEFAULT NULL,
    "field_code" varchar(50) DEFAULT NULL,
    "field_value" BIGINT,
    "field_value2" BIGINT,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "status" INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_rule_definition"."id" IS '规则唯一标识';
COMMENT ON COLUMN "metadata_validation_rule_definition"."group_id" IS '所属规则组 ID，关联 metadata_validation_rule_group 表的 id';
COMMENT ON COLUMN "metadata_validation_rule_definition"."parent_rule_id" IS '父规则 ID，用于层级关系；顶级规则为 NULL';
COMMENT ON COLUMN "metadata_validation_rule_definition"."entity_id" IS '关联的业务实体ID，关联 metadata_business_entity 表的 id';
COMMENT ON COLUMN "metadata_validation_rule_definition"."field_id" IS '关联的实体字段ID，关联 metadata_entity_field 表的 id';
COMMENT ON COLUMN "metadata_validation_rule_definition"."logic_type" IS '逻辑类型："LOGIC"（逻辑操作符）/"CONDITION"（条件判断）';
COMMENT ON COLUMN "metadata_validation_rule_definition"."operator" IS 'logic_type="CONDITION" 时，取值为 ">"/"<"/"="/"IN"/"BETWEEN" 等';
COMMENT ON COLUMN "metadata_validation_rule_definition"."logic_operator" IS '当 logic_type="LOGIC" 时，取值为 "AND"/"OR"';
COMMENT ON COLUMN "metadata_validation_rule_definition"."field_code" IS '条件字段编码，如 "AGE"、"INCOME"、"CUSTOMER_LEVEL"（仅 logic_type="CONDITION" 时有效）';
COMMENT ON COLUMN "metadata_validation_rule_definition"."field_value" IS '条件值引用（单值条件或范围表达式的第一个）';
COMMENT ON COLUMN "metadata_validation_rule_definition"."field_value2" IS '条件值引用2（单值条件或范围表达式的第二个）';
COMMENT ON COLUMN "metadata_validation_rule_definition"."status" IS '状态：1-激活，0-非激活';

-- Table Definition
CREATE TABLE "infra_config_1" (
    "id" INTEGER NOT NULL,
    "category" varchar(50) NOT NULL,
    "type" SMALLINT NOT NULL,
    "name" varchar(100) NOT NULL DEFAULT '',
    "config_key" varchar(100) NOT NULL DEFAULT '',
    "value" varchar(500) NOT NULL DEFAULT '',
    "visible" SMALLINT NOT NULL,
    "remark" varchar(500) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_config_1"."id" IS '参数主键';
COMMENT ON COLUMN "infra_config_1"."category" IS '参数分组';
COMMENT ON COLUMN "infra_config_1"."type" IS '参数类型';
COMMENT ON COLUMN "infra_config_1"."name" IS '参数名称';
COMMENT ON COLUMN "infra_config_1"."config_key" IS '参数键名';
COMMENT ON COLUMN "infra_config_1"."value" IS '参数键值';
COMMENT ON COLUMN "infra_config_1"."visible" IS '是否可见';
COMMENT ON COLUMN "infra_config_1"."remark" IS '备注';
COMMENT ON COLUMN "infra_config_1"."creator" IS '创建者';
COMMENT ON COLUMN "infra_config_1"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_config_1"."updater" IS '更新者';
COMMENT ON COLUMN "infra_config_1"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_config_1"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "metadata_entity_field_option" (
    "id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "option_label" varchar(256) NOT NULL,
    "option_value" varchar(256) NOT NULL,
    "option_order" INTEGER NOT NULL DEFAULT 0,
    "is_enabled" INTEGER NOT NULL DEFAULT 0,
    "description" varchar(512),
    "app_id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_entity_field_option"."field_id" IS '关联字段ID（metadata_entity_field.id）';
COMMENT ON COLUMN "metadata_entity_field_option"."option_label" IS '选项显示名称';
COMMENT ON COLUMN "metadata_entity_field_option"."option_value" IS '选项值（字段内唯一）';
COMMENT ON COLUMN "metadata_entity_field_option"."option_order" IS '选项排序';
COMMENT ON COLUMN "metadata_entity_field_option"."is_enabled" IS '是否启用：1-启用，0-禁用';

-- Table Definition
CREATE TABLE "metadata_entity_field_constraint" (
    "id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "constraint_type" varchar(32) NOT NULL,
    "min_length" INTEGER,
    "max_length" INTEGER,
    "regex_pattern" varchar(512),
    "prompt_message" varchar(512),
    "is_enabled" INTEGER NOT NULL DEFAULT 0,
    "run_mode" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_entity_field_constraint"."constraint_type" IS '约束类型：LENGTH_RANGE/REGEX';
COMMENT ON COLUMN "metadata_entity_field_constraint"."min_length" IS '最小长度（LENGTH_RANGE有效）';
COMMENT ON COLUMN "metadata_entity_field_constraint"."max_length" IS '最大长度（LENGTH_RANGE有效）';
COMMENT ON COLUMN "metadata_entity_field_constraint"."regex_pattern" IS '正则表达式（REGEX有效）';
COMMENT ON COLUMN "metadata_entity_field_constraint"."prompt_message" IS '提示信息';
COMMENT ON COLUMN "metadata_entity_field_constraint"."is_enabled" IS '是否启用：1-启用，0-禁用';

-- Table Definition
CREATE TABLE "system_corp" (
    "id" BIGINT NOT NULL,
    "corp_code" varchar(63) NOT NULL,
    "corp_name" varchar(63) NOT NULL,
    "industry_type" INTEGER NOT NULL,
    "status" INTEGER NOT NULL,
    "address" varchar(63) NOT NULL,
    "admin_id" varchar(63),
    "user_limit" INTEGER NOT NULL,
    "lock_version" BIGINT DEFAULT '0',
    "creator" varchar(64) DEFAULT '',
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" varchar(64) DEFAULT '',
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_corp"."corp_code" IS '企业编码';
COMMENT ON COLUMN "system_corp"."corp_name" IS '企业名称';
COMMENT ON COLUMN "system_corp"."industry_type" IS '行业类型';
COMMENT ON COLUMN "system_corp"."status" IS '企业状态(选项启用/停用)';
COMMENT ON COLUMN "system_corp"."address" IS '联系地址';
COMMENT ON COLUMN "system_corp"."admin_id" IS '管理员';
COMMENT ON COLUMN "system_corp"."user_limit" IS '用户数量上限';

-- Table Definition
CREATE TABLE "system_corp_app_relation" (
    "id" BIGINT NOT NULL,
    "corp_id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "tenant_id" BIGINT,
    "lock_version" BIGINT DEFAULT '0',
    "creator" varchar(64) DEFAULT '',
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" varchar(64) DEFAULT '',
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT '0',
    "expires_time" timestamp,
    "authorization_time" timestamp,
    "status" SMALLINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_corp_app_relation"."corp_id" IS '企业id';
COMMENT ON COLUMN "system_corp_app_relation"."application_id" IS '应用id';
COMMENT ON COLUMN "system_corp_app_relation"."tenant_id" IS '空间id';
COMMENT ON COLUMN "system_corp_app_relation"."lock_version" IS '锁标识';
COMMENT ON COLUMN "system_corp_app_relation"."expires_time" IS '过期日期';
COMMENT ON COLUMN "system_corp_app_relation"."authorization_time" IS '授权时间';
COMMENT ON COLUMN "system_corp_app_relation"."status" IS '状态';

-- Table Definition
CREATE TABLE "flow_process_time" (
    "id" BIGINT NOT NULL,
    "process_id" BIGINT NOT NULL,
    "job_id" varchar(64),
    "job_status" varchar(64),
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "flow_process_date_field" (
    "id" BIGINT NOT NULL,
    "process_id" BIGINT NOT NULL,
    "job_id" varchar(64),
    "job_status" varchar(64),
    "entity_id" BIGINT NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "dual" (
    "id" SMALLINT
);

-- Table Definition
CREATE TABLE "metadata_auto_number_rule_item" (
    "id" BIGINT NOT NULL,
    "config_id" BIGINT NOT NULL,
    "item_type" varchar(32) NOT NULL,
    "item_order" INTEGER NOT NULL DEFAULT 0,
    "format" varchar(64),
    "text_value" varchar(10),
    "ref_field_id" BIGINT,
    "is_enabled" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_auto_number_rule_item"."is_enabled" IS '是否启用：1-启用，0-禁用';

-- Table Definition
CREATE TABLE "metadata_auto_number_state" (
    "id" BIGINT NOT NULL,
    "config_id" BIGINT NOT NULL,
    "period_key" varchar(32) NOT NULL,
    "current_value" BIGINT NOT NULL,
    "last_reset_time" timestamp,
    "app_id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "metadata_auto_number_reset_log" (
    "id" BIGINT NOT NULL,
    "config_id" BIGINT NOT NULL,
    "period_key" varchar(32) NOT NULL,
    "prev_value" BIGINT,
    "next_value" BIGINT NOT NULL,
    "reset_reason" varchar(512),
    "operator" BIGINT NOT NULL DEFAULT 0,
    "reset_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "app_id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "metadata_auto_number_config" (
    "id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "number_mode" varchar(16) NOT NULL,
    "digit_width" SMALLINT,
    "overflow_continue" SMALLINT NOT NULL DEFAULT 1,
    "initial_value" BIGINT NOT NULL DEFAULT 1,
    "reset_cycle" varchar(16) NOT NULL DEFAULT 'NONE',
    "is_enabled" INTEGER NOT NULL DEFAULT 0,
    "run_mode" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "reset_on_initial_change" SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_auto_number_config"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_auto_number_config"."reset_on_initial_change" IS '下一条记录以修改后的开始值编号：1-是，0-否';

-- Table Definition
CREATE TABLE "app_auth_role_dept" (
    "id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "dept_id" BIGINT NOT NULL DEFAULT 0,
    "is_include_child" SMALLINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_role_dept"."id" IS '主键';
COMMENT ON COLUMN "app_auth_role_dept"."role_id" IS '角色Id';
COMMENT ON COLUMN "app_auth_role_dept"."dept_id" IS '部门Id';
COMMENT ON COLUMN "app_auth_role_dept"."is_include_child" IS '是否包含子部门';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS system_role_menu_seq;

-- Table Definition
CREATE TABLE "system_role_menu" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "role_id" BIGINT NOT NULL,
    "menu_id" BIGINT NOT NULL,
    "creator" varchar(64) DEFAULT '',
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" varchar(64) DEFAULT '',
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_role_menu"."id" IS '自增编号';
COMMENT ON COLUMN "system_role_menu"."role_id" IS '角色ID';
COMMENT ON COLUMN "system_role_menu"."menu_id" IS '菜单ID';
COMMENT ON COLUMN "system_role_menu"."creator" IS '创建者';
COMMENT ON COLUMN "system_role_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_role_menu"."updater" IS '更新者';
COMMENT ON COLUMN "system_role_menu"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_role_menu"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_role_menu"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "app_version" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "version_name" varchar(128) NOT NULL,
    "version_number" varchar(64) NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "version_description" text,
    "environment" varchar(128),
    "operation_type" INTEGER,
    "version_url" varchar(1024),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_version"."id" IS '主键ID';
COMMENT ON COLUMN "app_version"."application_id" IS '应用ID';
COMMENT ON COLUMN "app_version"."version_name" IS '版本名称';
COMMENT ON COLUMN "app_version"."version_number" IS '版本编号';

-- Table Definition
CREATE TABLE "app_auth_view" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "menu_id" BIGINT NOT NULL,
    "view_id" BIGINT NOT NULL,
    "is_allowed" SMALLINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_view"."id" IS '主键Id';
COMMENT ON COLUMN "app_auth_view"."application_id" IS '应用Id';
COMMENT ON COLUMN "app_auth_view"."role_id" IS '角色id';
COMMENT ON COLUMN "app_auth_view"."menu_id" IS '菜单id';
COMMENT ON COLUMN "app_auth_view"."view_id" IS '实体id';
COMMENT ON COLUMN "app_auth_view"."is_allowed" IS '是否可访问';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS system_user_post_seq;

-- Table Definition
CREATE TABLE "system_user_post" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "user_id" BIGINT NOT NULL DEFAULT 0,
    "post_id" BIGINT NOT NULL DEFAULT 0,
    "creator" varchar(64) DEFAULT '',
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" varchar(64) DEFAULT '',
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_user_post"."id" IS 'id';
COMMENT ON COLUMN "system_user_post"."user_id" IS '用户ID';
COMMENT ON COLUMN "system_user_post"."post_id" IS '岗位ID';
COMMENT ON COLUMN "system_user_post"."creator" IS '创建者';
COMMENT ON COLUMN "system_user_post"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_user_post"."updater" IS '更新者';
COMMENT ON COLUMN "system_user_post"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_user_post"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_user_post"."tenant_id" IS '租户编号';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS system_user_role_seq;

-- Table Definition
CREATE TABLE "system_user_role" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "user_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "creator" BIGINT,
    "create_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT,
    "update_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_user_role"."id" IS '自增编号';
COMMENT ON COLUMN "system_user_role"."user_id" IS '用户ID';
COMMENT ON COLUMN "system_user_role"."role_id" IS '角色ID';
COMMENT ON COLUMN "system_user_role"."creator" IS '创建者';
COMMENT ON COLUMN "system_user_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_user_role"."updater" IS '更新者';
COMMENT ON COLUMN "system_user_role"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_user_role"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_user_role"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_tenant_admin" (
    "id" BIGINT,
    "tenant_id" BIGINT,
    "admin_user_id" BIGINT,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT '0'
);

-- Column Comment
COMMENT ON COLUMN "system_tenant_admin"."creator" IS '创建者';
COMMENT ON COLUMN "system_tenant_admin"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_tenant_admin"."updater" IS '更新者';
COMMENT ON COLUMN "system_tenant_admin"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_tenant_admin"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "flow_execution_log" (
    "id" BIGINT NOT NULL,
    "trigger_user_id" BIGINT,
    "trace_id" varchar(64),
    "execution_uuid" varchar(64),
    "application_id" BIGINT NOT NULL,
    "process_id" BIGINT NOT NULL,
    "start_time" timestamp,
    "end_time" timestamp,
    "duration_time" BIGINT,
    "execution_result" varchar(20) NOT NULL,
    "log_text" text,
    "error_message" text,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "flow_execution_log"."id" IS '主键Id';
COMMENT ON COLUMN "flow_execution_log"."trigger_user_id" IS '触发用户';
COMMENT ON COLUMN "flow_execution_log"."trace_id" IS '跟踪Id';
COMMENT ON COLUMN "flow_execution_log"."execution_uuid" IS '执行uuid';
COMMENT ON COLUMN "flow_execution_log"."application_id" IS '应用Id';
COMMENT ON COLUMN "flow_execution_log"."process_id" IS '流程Id';
COMMENT ON COLUMN "flow_execution_log"."start_time" IS '开始时间';
COMMENT ON COLUMN "flow_execution_log"."end_time" IS '结束时间';
COMMENT ON COLUMN "flow_execution_log"."execution_result" IS '执行结果：success-成功, failed-失败';
COMMENT ON COLUMN "flow_execution_log"."log_text" IS '详细执行日志';
COMMENT ON COLUMN "flow_execution_log"."error_message" IS '错误信息（执行失败时使用）';

-- Table Definition
CREATE TABLE "system_license" (
    "id" BIGINT NOT NULL,
    "enterprise_name" varchar(128) NOT NULL,
    "enterprise_code" varchar(128) NOT NULL,
    "enterprise_address" varchar(1024),
    "platform_type" varchar(64),
    "tenant_limit" INTEGER NOT NULL,
    "user_limit" INTEGER NOT NULL,
    "expire_time" timestamp NOT NULL,
    "status" varchar(16) NOT NULL DEFAULT 'enable',
    "is_trial" SMALLINT NOT NULL DEFAULT 0,
    "license_file" text NOT NULL,
    "creator" BIGINT NOT NULL,
    "updater" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_license"."id" IS '主键';
COMMENT ON COLUMN "system_license"."enterprise_name" IS '企业名称';
COMMENT ON COLUMN "system_license"."enterprise_code" IS '企业编号';
COMMENT ON COLUMN "system_license"."enterprise_address" IS '企业地址';
COMMENT ON COLUMN "system_license"."platform_type" IS '平台类型';
COMMENT ON COLUMN "system_license"."tenant_limit" IS '租户数量限制';
COMMENT ON COLUMN "system_license"."user_limit" IS '用户数量限制';
COMMENT ON COLUMN "system_license"."expire_time" IS '到期时间';
COMMENT ON COLUMN "system_license"."status" IS '状态：enable,disable';
COMMENT ON COLUMN "system_license"."is_trial" IS '是否为试用License';
COMMENT ON COLUMN "system_license"."license_file" IS 'License文件';
COMMENT ON COLUMN "system_license"."creator" IS '创建者ID';
COMMENT ON COLUMN "system_license"."updater" IS '更新人ID';
COMMENT ON COLUMN "system_license"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_license"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_license"."deleted" IS '软删标识：非0即删除';

-- Table Definition
CREATE TABLE "bpm_flow_instance_biz_ext" (
    "id" BIGINT NOT NULL,
    "instance_id" BIGINT NOT NULL,
    "business_id" varchar(100) NOT NULL,
    "business_code" varchar(100),
    "business_title" varchar(200) NOT NULL,
    "initiator_id" BIGINT,
    "initiator_name" varchar(100),
    "initiator_dept_id" BIGINT,
    "initiator_dept_name" varchar(100),
    "submit_time" timestamp,
    "form_summary" varchar(500) NOT NULL,
    "form_name" varchar(100) NOT NULL,
    "bpm_version" varchar(50) NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."id" IS '主键ID';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."instance_id" IS '流程实例ID';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."business_id" IS '业务ID';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."business_code" IS '业务编码';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."business_title" IS '业务标题';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."initiator_id" IS '发起人ID';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."initiator_name" IS '发起人名称（冗余字段）';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."initiator_dept_id" IS '发起部门ID';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."initiator_dept_name" IS '发起部门名称（冗余字段）';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."submit_time" IS '发起时间（与create_time的区别：以提交表单动作为标准，而非保存表单）';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."form_summary" IS '表单摘要';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."form_name" IS '流程表单';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."bpm_version" IS '流程版本号';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."creator" IS '创建人';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "bpm_flow_instance_biz_ext"."app_id" IS '应用ID';

-- Table Definition
CREATE TABLE "bpm_flow_definition" (
    "id" BIGINT NOT NULL,
    "flow_code" varchar(40) NOT NULL,
    "flow_name" varchar(100) NOT NULL,
    "model_value" varchar(40) NOT NULL DEFAULT 'CLASSICS',
    "category" varchar(100),
    "version" varchar(20) NOT NULL,
    "is_publish" SMALLINT NOT NULL DEFAULT 0,
    "form_custom" CHAR(1) DEFAULT 'N',
    "form_path" varchar(100),
    "activity_status" SMALLINT NOT NULL DEFAULT 1,
    "listener_type" varchar(100),
    "listener_path" varchar(400),
    "ext" varchar(500),
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_definition"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_definition"."flow_code" IS '流程编码';
COMMENT ON COLUMN "bpm_flow_definition"."flow_name" IS '流程名称';
COMMENT ON COLUMN "bpm_flow_definition"."model_value" IS '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）';
COMMENT ON COLUMN "bpm_flow_definition"."category" IS '流程类别';
COMMENT ON COLUMN "bpm_flow_definition"."version" IS '流程版本';
COMMENT ON COLUMN "bpm_flow_definition"."is_publish" IS '是否发布（0未发布 1已发布 9失效）';
COMMENT ON COLUMN "bpm_flow_definition"."form_custom" IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN "bpm_flow_definition"."form_path" IS '审批表单路径';
COMMENT ON COLUMN "bpm_flow_definition"."activity_status" IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN "bpm_flow_definition"."listener_type" IS '监听器类型';
COMMENT ON COLUMN "bpm_flow_definition"."listener_path" IS '监听器路径';
COMMENT ON COLUMN "bpm_flow_definition"."ext" IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN "bpm_flow_definition"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_definition"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_definition"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_definition"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_definition"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_definition"."tenant_id" IS '租户id';
COMMENT ON COLUMN "bpm_flow_definition"."creator" IS '创建人';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS metadata_data_system_method_id_seq;

-- Table Definition
CREATE TABLE "metadata_data_system_method" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "method_code" varchar(100) NOT NULL,
    "method_name" varchar(200) NOT NULL,
    "method_type" varchar(50) NOT NULL,
    "method_url" varchar(500),
    "method_description" text,
    "is_enabled" SMALLINT DEFAULT 0,
    "request_method" varchar(10) DEFAULT 'POST',
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_data_system_method"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_data_system_method"."method_code" IS '方法编码';
COMMENT ON COLUMN "metadata_data_system_method"."method_name" IS '方法名称';
COMMENT ON COLUMN "metadata_data_system_method"."method_type" IS '方法类型：CREATE-新增,READ-查询,UPDATE-更新,DELETE-删除,BATCH-批量操作,DRAFT-草稿';
COMMENT ON COLUMN "metadata_data_system_method"."method_url" IS '方法URL地址';
COMMENT ON COLUMN "metadata_data_system_method"."method_description" IS '方法描述';
COMMENT ON COLUMN "metadata_data_system_method"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_data_system_method"."request_method" IS 'HTTP请求方法：GET,POST,PUT,DELETE';
COMMENT ON COLUMN "metadata_data_system_method"."deleted" IS '是否删除：0-否，1-是';
COMMENT ON COLUMN "metadata_data_system_method"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_data_system_method"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_data_system_method"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_data_system_method"."update_time" IS '更新时间';

-- Table Definition
CREATE TABLE "bpm_flow_node" (
    "id" BIGINT NOT NULL,
    "node_type" SMALLINT NOT NULL,
    "definition_id" BIGINT NOT NULL,
    "node_code" varchar(100) NOT NULL,
    "node_name" varchar(100),
    "permission_flag" text,
    "node_ratio" numeric(6,3),
    "coordinate" varchar(100),
    "any_node_skip" varchar(100),
    "listener_type" varchar(100),
    "listener_path" varchar(400),
    "handler_type" varchar(100),
    "handler_path" varchar(400),
    "form_custom" CHAR(1) DEFAULT 'N',
    "form_path" varchar(100),
    "version" varchar(20) NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "ext" text,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_node"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_node"."node_type" IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN "bpm_flow_node"."definition_id" IS '流程定义id';
COMMENT ON COLUMN "bpm_flow_node"."node_code" IS '流程节点编码';
COMMENT ON COLUMN "bpm_flow_node"."node_name" IS '流程节点名称';
COMMENT ON COLUMN "bpm_flow_node"."permission_flag" IS '权限标识（权限类型:权限标识，可以多个，用@@隔开)';
COMMENT ON COLUMN "bpm_flow_node"."node_ratio" IS '流程签署比例值';
COMMENT ON COLUMN "bpm_flow_node"."coordinate" IS '坐标';
COMMENT ON COLUMN "bpm_flow_node"."any_node_skip" IS '任意结点跳转';
COMMENT ON COLUMN "bpm_flow_node"."listener_type" IS '监听器类型';
COMMENT ON COLUMN "bpm_flow_node"."listener_path" IS '监听器路径';
COMMENT ON COLUMN "bpm_flow_node"."handler_type" IS '处理器类型';
COMMENT ON COLUMN "bpm_flow_node"."handler_path" IS '处理器路径';
COMMENT ON COLUMN "bpm_flow_node"."form_custom" IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN "bpm_flow_node"."form_path" IS '审批表单路径';
COMMENT ON COLUMN "bpm_flow_node"."version" IS '版本';
COMMENT ON COLUMN "bpm_flow_node"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_node"."creator" IS '创建人';
COMMENT ON COLUMN "bpm_flow_node"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_node"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_node"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_node"."ext" IS '节点扩展属性';
COMMENT ON COLUMN "bpm_flow_node"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_node"."tenant_id" IS '租户id';

-- Table Definition
CREATE TABLE "bpm_flow_skip" (
    "id" BIGINT NOT NULL,
    "definition_id" BIGINT NOT NULL,
    "now_node_code" varchar(100) NOT NULL,
    "now_node_type" SMALLINT,
    "next_node_code" varchar(100) NOT NULL,
    "next_node_type" SMALLINT,
    "skip_name" varchar(100),
    "skip_type" varchar(40),
    "skip_condition" varchar(200),
    "coordinate" varchar(100),
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_skip"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_skip"."definition_id" IS '流程定义id';
COMMENT ON COLUMN "bpm_flow_skip"."now_node_code" IS '当前流程节点的编码';
COMMENT ON COLUMN "bpm_flow_skip"."now_node_type" IS '当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN "bpm_flow_skip"."next_node_code" IS '下一个流程节点的编码';
COMMENT ON COLUMN "bpm_flow_skip"."next_node_type" IS '下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN "bpm_flow_skip"."skip_name" IS '跳转名称';
COMMENT ON COLUMN "bpm_flow_skip"."skip_type" IS '跳转类型（PASS审批通过 REJECT退回）';
COMMENT ON COLUMN "bpm_flow_skip"."skip_condition" IS '跳转条件';
COMMENT ON COLUMN "bpm_flow_skip"."coordinate" IS '坐标';
COMMENT ON COLUMN "bpm_flow_skip"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_skip"."creator" IS '创建人';
COMMENT ON COLUMN "bpm_flow_skip"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_skip"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_skip"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_skip"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_skip"."tenant_id" IS '租户id';

-- Table Definition
CREATE TABLE "bpm_flow_instance" (
    "id" BIGINT NOT NULL,
    "definition_id" BIGINT NOT NULL,
    "business_id" varchar(40) NOT NULL,
    "node_type" SMALLINT NOT NULL,
    "node_code" varchar(40) NOT NULL,
    "node_name" varchar(100),
    "variable" text,
    "flow_status" varchar(20) NOT NULL,
    "activity_status" SMALLINT NOT NULL DEFAULT 1,
    "def_json" text,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "ext" varchar(500),
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_instance"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_instance"."definition_id" IS '对应flow_definition表的id';
COMMENT ON COLUMN "bpm_flow_instance"."business_id" IS '业务id';
COMMENT ON COLUMN "bpm_flow_instance"."node_type" IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN "bpm_flow_instance"."node_code" IS '流程节点编码';
COMMENT ON COLUMN "bpm_flow_instance"."node_name" IS '流程节点名称';
COMMENT ON COLUMN "bpm_flow_instance"."variable" IS '任务变量';
COMMENT ON COLUMN "bpm_flow_instance"."flow_status" IS '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';
COMMENT ON COLUMN "bpm_flow_instance"."activity_status" IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN "bpm_flow_instance"."def_json" IS '流程定义json';
COMMENT ON COLUMN "bpm_flow_instance"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_instance"."creator" IS '创建人';
COMMENT ON COLUMN "bpm_flow_instance"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_instance"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_instance"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_instance"."ext" IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN "bpm_flow_instance"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_instance"."tenant_id" IS '租户id';

-- Table Definition
CREATE TABLE "bpm_flow_task" (
    "id" BIGINT NOT NULL,
    "definition_id" BIGINT NOT NULL,
    "instance_id" BIGINT NOT NULL,
    "node_code" varchar(100) NOT NULL,
    "node_name" varchar(100),
    "node_type" SMALLINT NOT NULL,
    "flow_status" varchar(20) NOT NULL,
    "form_custom" CHAR(1) DEFAULT 'N',
    "form_path" varchar(100),
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_task"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_task"."definition_id" IS '对应flow_definition表的id';
COMMENT ON COLUMN "bpm_flow_task"."instance_id" IS '对应flow_instance表的id';
COMMENT ON COLUMN "bpm_flow_task"."node_code" IS '节点编码';
COMMENT ON COLUMN "bpm_flow_task"."node_name" IS '节点名称';
COMMENT ON COLUMN "bpm_flow_task"."node_type" IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN "bpm_flow_task"."flow_status" IS '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';
COMMENT ON COLUMN "bpm_flow_task"."form_custom" IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN "bpm_flow_task"."form_path" IS '审批表单路径';
COMMENT ON COLUMN "bpm_flow_task"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_task"."creator" IS '创建人';
COMMENT ON COLUMN "bpm_flow_task"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_task"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_task"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_task"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_task"."tenant_id" IS '租户id';

-- Table Definition
CREATE TABLE "bpm_flow_his_task" (
    "id" BIGINT NOT NULL,
    "definition_id" BIGINT NOT NULL,
    "instance_id" BIGINT NOT NULL,
    "task_id" BIGINT NOT NULL,
    "node_code" varchar(100),
    "node_name" varchar(100),
    "node_type" SMALLINT,
    "target_node_code" varchar(200),
    "target_node_name" varchar(200),
    "approver" varchar(40),
    "cooperate_type" SMALLINT NOT NULL DEFAULT 0,
    "collaborator" varchar(500),
    "skip_type" varchar(10),
    "flow_status" varchar(20) NOT NULL,
    "form_custom" CHAR(1) DEFAULT 'N',
    "form_path" varchar(100),
    "ext" text,
    "message" varchar(500),
    "variable" text,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_his_task"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_his_task"."definition_id" IS '对应flow_definition表的id';
COMMENT ON COLUMN "bpm_flow_his_task"."instance_id" IS '对应flow_instance表的id';
COMMENT ON COLUMN "bpm_flow_his_task"."task_id" IS '对应flow_task表的id';
COMMENT ON COLUMN "bpm_flow_his_task"."node_code" IS '开始节点编码';
COMMENT ON COLUMN "bpm_flow_his_task"."node_name" IS '开始节点名称';
COMMENT ON COLUMN "bpm_flow_his_task"."node_type" IS '开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN "bpm_flow_his_task"."target_node_code" IS '目标节点编码';
COMMENT ON COLUMN "bpm_flow_his_task"."target_node_name" IS '结束节点名称';
COMMENT ON COLUMN "bpm_flow_his_task"."approver" IS '审批者';
COMMENT ON COLUMN "bpm_flow_his_task"."cooperate_type" IS '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)';
COMMENT ON COLUMN "bpm_flow_his_task"."collaborator" IS '协作人';
COMMENT ON COLUMN "bpm_flow_his_task"."skip_type" IS '流转类型（PASS通过 REJECT退回 NONE无动作）';
COMMENT ON COLUMN "bpm_flow_his_task"."flow_status" IS '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';
COMMENT ON COLUMN "bpm_flow_his_task"."form_custom" IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN "bpm_flow_his_task"."form_path" IS '审批表单路径';
COMMENT ON COLUMN "bpm_flow_his_task"."ext" IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN "bpm_flow_his_task"."message" IS '审批意见';
COMMENT ON COLUMN "bpm_flow_his_task"."variable" IS '任务变量';
COMMENT ON COLUMN "bpm_flow_his_task"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_his_task"."create_time" IS '任务开始时间';
COMMENT ON COLUMN "bpm_flow_his_task"."update_time" IS '审批完成时间';
COMMENT ON COLUMN "bpm_flow_his_task"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_his_task"."tenant_id" IS '租户id';

-- Table Definition
CREATE TABLE "bpm_flow_user" (
    "id" BIGINT NOT NULL,
    "type" CHAR(1) NOT NULL,
    "processed_by" varchar(80),
    "associated" BIGINT NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "bpm_flow_user"."id" IS '主键id';
COMMENT ON COLUMN "bpm_flow_user"."type" IS '人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）';
COMMENT ON COLUMN "bpm_flow_user"."processed_by" IS '权限人';
COMMENT ON COLUMN "bpm_flow_user"."associated" IS '任务表id';
COMMENT ON COLUMN "bpm_flow_user"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "bpm_flow_user"."creator" IS '创建人';
COMMENT ON COLUMN "bpm_flow_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "bpm_flow_user"."updater" IS '更新人';
COMMENT ON COLUMN "bpm_flow_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "bpm_flow_user"."deleted" IS '删除标志';
COMMENT ON COLUMN "bpm_flow_user"."tenant_id" IS '租户id';

-- Table Definition
CREATE TABLE "app_auth_permission" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "menu_id" BIGINT NOT NULL,
    "is_page_allowed" SMALLINT NOT NULL DEFAULT 0,
    "is_all_views_allowed" SMALLINT NOT NULL DEFAULT 0,
    "is_all_fields_allowed" SMALLINT NOT NULL DEFAULT 0,
    "operation_tags" varchar(64),
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_permission"."id" IS '主键Id';
COMMENT ON COLUMN "app_auth_permission"."application_id" IS '应用id';
COMMENT ON COLUMN "app_auth_permission"."role_id" IS '角色id';
COMMENT ON COLUMN "app_auth_permission"."menu_id" IS '菜单id';
COMMENT ON COLUMN "app_auth_permission"."is_page_allowed" IS '页面是否可访问';
COMMENT ON COLUMN "app_auth_permission"."is_all_views_allowed" IS '所有视图可访问';
COMMENT ON COLUMN "app_auth_permission"."is_all_fields_allowed" IS '所有字段可操作';
COMMENT ON COLUMN "app_auth_permission"."operation_tags" IS '操作权限标签';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS metadata_component_field_type_id_seq;

-- Table Definition
CREATE TABLE "metadata_component_field_type" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "field_type_code" varchar(50) NOT NULL,
    "field_type_name" varchar(100) NOT NULL,
    "field_type_desc" text,
    "data_type" varchar(50),
    "sort_order" INTEGER DEFAULT 0,
    "status" SMALLINT DEFAULT 1,
    "creator" BIGINT,
    "create_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT,
    "update_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "deleted" SMALLINT DEFAULT 0,
    "type" SMALLINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_component_field_type"."field_type_code" IS '字段类型编码';
COMMENT ON COLUMN "metadata_component_field_type"."field_type_name" IS '字段类型名称';
COMMENT ON COLUMN "metadata_component_field_type"."field_type_desc" IS '字段类型描述';
COMMENT ON COLUMN "metadata_component_field_type"."data_type" IS 'jdbc数据类型';
COMMENT ON COLUMN "metadata_component_field_type"."sort_order" IS '排序顺序';
COMMENT ON COLUMN "metadata_component_field_type"."status" IS '状态：0-启用，1-禁用';
COMMENT ON COLUMN "metadata_component_field_type"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_component_field_type"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_component_field_type"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_component_field_type"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_component_field_type"."deleted" IS '删除标识：0-未删除，1-已删除';
COMMENT ON COLUMN "metadata_component_field_type"."type" IS '类型（应用过滤使用）';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS metadata_validation_type_id_seq;

-- Table Definition
CREATE TABLE "metadata_validation_type" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "validation_code" varchar(50) NOT NULL,
    "validation_name" varchar(100) NOT NULL,
    "validation_desc" text,
    "sort_order" INTEGER DEFAULT 0,
    "status" SMALLINT DEFAULT 1,
    "creator" BIGINT,
    "create_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT,
    "update_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "deleted" SMALLINT DEFAULT 0,
    "type" SMALLINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_type"."validation_code" IS '校验类型编码';
COMMENT ON COLUMN "metadata_validation_type"."validation_name" IS '校验类型名称';
COMMENT ON COLUMN "metadata_validation_type"."validation_desc" IS '校验类型描述';
COMMENT ON COLUMN "metadata_validation_type"."sort_order" IS '排序顺序';
COMMENT ON COLUMN "metadata_validation_type"."status" IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_type"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_validation_type"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_type"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_validation_type"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_type"."deleted" IS '删除标识：0-未删除，1-已删除';
COMMENT ON COLUMN "metadata_validation_type"."type" IS '类型（给应用过滤用）';

-- Table Definition
CREATE TABLE "app_auth_data_group" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "menu_id" BIGINT NOT NULL DEFAULT 0,
    "group_name" varchar(100) NOT NULL,
    "group_order" INTEGER,
    "description" varchar(256) DEFAULT NULL,
    "scope_tags" varchar(256),
    "scope_field_id" BIGINT,
    "scope_level" varchar(32),
    "scope_value" varchar(256),
    "data_filter" text,
    "operation_tags" varchar(64) DEFAULT '0',
    "lock_version" BIGINT DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "data_range_list" varchar(20),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "app_auth_data_group"."id" IS '主键Id';
COMMENT ON COLUMN "app_auth_data_group"."application_id" IS '应用id';
COMMENT ON COLUMN "app_auth_data_group"."role_id" IS '角色id';
COMMENT ON COLUMN "app_auth_data_group"."menu_id" IS '菜单id';
COMMENT ON COLUMN "app_auth_data_group"."group_name" IS '组名称';
COMMENT ON COLUMN "app_auth_data_group"."group_order" IS '组排序';
COMMENT ON COLUMN "app_auth_data_group"."description" IS '描述';
COMMENT ON COLUMN "app_auth_data_group"."scope_field_id" IS '关联业务实体字段id';
COMMENT ON COLUMN "app_auth_data_group"."scope_level" IS '关联业务实体字段对应的权限范围';
COMMENT ON COLUMN "app_auth_data_group"."scope_value" IS '关联业务实体字段对应的权限值';
COMMENT ON COLUMN "app_auth_data_group"."data_filter" IS '数据过滤日志的JSON';
COMMENT ON COLUMN "app_auth_data_group"."operation_tags" IS '是否可以操作';
COMMENT ON COLUMN "app_auth_data_group"."data_range_list" IS '数据权限范围：0全部数据，1本人提交，2本部门提交，3下级部门提交，4自定义条件。可以多选，最终组成list';

-- Table Definition
CREATE TABLE "system_mail_account" (
    "id" BIGINT NOT NULL,
    "mail" varchar(255) NOT NULL,
    "username" varchar(255) NOT NULL,
    "password" varchar(255) NOT NULL,
    "host" varchar(255) NOT NULL,
    "port" INTEGER NOT NULL,
    "ssl_enable" SMALLINT NOT NULL DEFAULT 0,
    "starttls_enable" SMALLINT NOT NULL DEFAULT 0,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_mail_account"."id" IS '主键';
COMMENT ON COLUMN "system_mail_account"."mail" IS '邮箱';
COMMENT ON COLUMN "system_mail_account"."username" IS '用户名';
COMMENT ON COLUMN "system_mail_account"."password" IS '密码';
COMMENT ON COLUMN "system_mail_account"."host" IS 'SMTP 服务器域名';
COMMENT ON COLUMN "system_mail_account"."port" IS 'SMTP 服务器端口';
COMMENT ON COLUMN "system_mail_account"."ssl_enable" IS '是否开启 SSL';
COMMENT ON COLUMN "system_mail_account"."starttls_enable" IS '是否开启 STARTTLS';
COMMENT ON COLUMN "system_mail_account"."creator" IS '创建者';
COMMENT ON COLUMN "system_mail_account"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_mail_account"."updater" IS '更新者';
COMMENT ON COLUMN "system_mail_account"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_mail_account"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "flow_process_form" (
    "id" BIGINT NOT NULL,
    "process_id" BIGINT NOT NULL,
    "page_id" BIGINT NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "flow_process_entity" (
    "id" BIGINT NOT NULL,
    "process_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS metadata_system_fields_id_seq;

-- Table Definition
CREATE TABLE "metadata_system_fields" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "field_name" varchar(50) NOT NULL,
    "field_type" varchar(20) NOT NULL,
    "is_snowflake_id" INTEGER NOT NULL DEFAULT 0,
    "is_required" INTEGER NOT NULL DEFAULT 0,
    "default_value" varchar(100),
    "description" text,
    "is_enabled" INTEGER NOT NULL DEFAULT 0,
    "display_name" varchar(255),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_system_fields"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_system_fields"."field_name" IS '字段名';
COMMENT ON COLUMN "metadata_system_fields"."field_type" IS '字段类型';
COMMENT ON COLUMN "metadata_system_fields"."is_snowflake_id" IS '是否为雪花ID：1-是，0-否';
COMMENT ON COLUMN "metadata_system_fields"."is_required" IS '是否必填：1-是，0-否';
COMMENT ON COLUMN "metadata_system_fields"."default_value" IS '默认值';
COMMENT ON COLUMN "metadata_system_fields"."description" IS '字段说明';
COMMENT ON COLUMN "metadata_system_fields"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_system_fields"."display_name" IS '对外展示名称';

-- Table Definition
CREATE TABLE "metadata_validation_required" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 1,
    "prompt_message" varchar(512),
    "run_mode" INTEGER,
    "app_id" BIGINT,
    "tenant_id" BIGINT,
    "creator" BIGINT,
    "updater" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "deleted" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_required"."id" IS '主键（雪花/外部生成）';
COMMENT ON COLUMN "metadata_validation_required"."group_id" IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN "metadata_validation_required"."entity_id" IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_required"."field_id" IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN "metadata_validation_required"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_required"."prompt_message" IS '提示信息（触发校验时给用户的提示）';
COMMENT ON COLUMN "metadata_validation_required"."run_mode" IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN "metadata_validation_required"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_validation_required"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_validation_required"."creator" IS '创建人';
COMMENT ON COLUMN "metadata_validation_required"."updater" IS '更新人';
COMMENT ON COLUMN "metadata_validation_required"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_required"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_required"."deleted" IS '软删除标记：0-未删除，1-已删除';

-- Table Definition
CREATE TABLE "metadata_validation_unique" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 1,
    "prompt_message" varchar(512),
    "run_mode" INTEGER,
    "app_id" BIGINT,
    "tenant_id" BIGINT,
    "creator" BIGINT,
    "updater" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "deleted" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_unique"."id" IS '主键（雪花/外部生成）';
COMMENT ON COLUMN "metadata_validation_unique"."group_id" IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN "metadata_validation_unique"."entity_id" IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_unique"."field_id" IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN "metadata_validation_unique"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_unique"."prompt_message" IS '提示信息';
COMMENT ON COLUMN "metadata_validation_unique"."run_mode" IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN "metadata_validation_unique"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_validation_unique"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_validation_unique"."creator" IS '创建人';
COMMENT ON COLUMN "metadata_validation_unique"."updater" IS '更新人';
COMMENT ON COLUMN "metadata_validation_unique"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_unique"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_unique"."deleted" IS '软删除标记：0-未删除，1-已删除';

-- Table Definition
CREATE TABLE "app_tag" (
    "id" BIGINT NOT NULL,
    "tag_name" varchar(128) NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "metadata_permit_ref_otft" (
    "id" BIGINT NOT NULL,
    "field_type_id" BIGINT NOT NULL,
    "validation_type_id" BIGINT NOT NULL,
    "sort_order" INTEGER NOT NULL,
    "creator" BIGINT,
    "create_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT,
    "update_time" timestamp DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT DEFAULT 0
);

-- Column Comment
COMMENT ON COLUMN "metadata_permit_ref_otft"."id" IS '主键Id';
COMMENT ON COLUMN "metadata_permit_ref_otft"."field_type_id" IS '字段类型Id';
COMMENT ON COLUMN "metadata_permit_ref_otft"."validation_type_id" IS '操作符号Id';
COMMENT ON COLUMN "metadata_permit_ref_otft"."sort_order" IS '排序';

-- Table Definition
CREATE TABLE "metadata_validation_length" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 1,
    "min_length" INTEGER,
    "max_length" INTEGER,
    "trim_before" INTEGER DEFAULT 1,
    "prompt_message" varchar(512),
    "run_mode" INTEGER,
    "app_id" BIGINT,
    "tenant_id" BIGINT,
    "creator" BIGINT,
    "updater" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "deleted" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_length"."id" IS '主键（雪花/外部生成）';
COMMENT ON COLUMN "metadata_validation_length"."group_id" IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN "metadata_validation_length"."entity_id" IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_length"."field_id" IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN "metadata_validation_length"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_length"."min_length" IS '最小长度（为空则不校验下限）';
COMMENT ON COLUMN "metadata_validation_length"."max_length" IS '最大长度（为空则不校验上限）';
COMMENT ON COLUMN "metadata_validation_length"."trim_before" IS '校验前是否trim：1-是，0-否';
COMMENT ON COLUMN "metadata_validation_length"."prompt_message" IS '提示信息';
COMMENT ON COLUMN "metadata_validation_length"."run_mode" IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN "metadata_validation_length"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_validation_length"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_validation_length"."creator" IS '创建人';
COMMENT ON COLUMN "metadata_validation_length"."updater" IS '更新人';
COMMENT ON COLUMN "metadata_validation_length"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_length"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_length"."deleted" IS '软删除标记：0-未删除，1-已删除';

-- Table Definition
CREATE TABLE "flow_process" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "process_name" varchar(128) NOT NULL,
    "process_description" varchar(1024),
    "process_definition" text,
    "enable_status" SMALLINT NOT NULL DEFAULT 0,
    "publish_status" SMALLINT NOT NULL DEFAULT 0,
    "trigger_type" varchar(64) NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "flow_process"."trigger_type" IS '流程类型，如表达触发，定时触发，API触发等';

-- Table Definition
CREATE TABLE "system_menu" (
    "id" BIGINT NOT NULL,
    "name" varchar(50) NOT NULL,
    "permission" varchar(100) NOT NULL DEFAULT '',
    "type" SMALLINT NOT NULL,
    "sort" INTEGER NOT NULL DEFAULT 0,
    "parent_id" BIGINT NOT NULL DEFAULT 0,
    "path" varchar(200) DEFAULT '',
    "icon" varchar(100) DEFAULT '#',
    "component" varchar(255) DEFAULT NULL,
    "component_name" varchar(255) DEFAULT NULL,
    "status" SMALLINT NOT NULL DEFAULT 0,
    "visible" SMALLINT NOT NULL DEFAULT 1,
    "keep_alive" SMALLINT NOT NULL DEFAULT 1,
    "always_show" SMALLINT NOT NULL DEFAULT 1,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_menu"."id" IS '菜单ID';
COMMENT ON COLUMN "system_menu"."name" IS '菜单名称';
COMMENT ON COLUMN "system_menu"."permission" IS '权限标识';
COMMENT ON COLUMN "system_menu"."type" IS '菜单类型';
COMMENT ON COLUMN "system_menu"."sort" IS '显示顺序';
COMMENT ON COLUMN "system_menu"."parent_id" IS '父菜单ID';
COMMENT ON COLUMN "system_menu"."path" IS '路由地址';
COMMENT ON COLUMN "system_menu"."icon" IS '菜单图标';
COMMENT ON COLUMN "system_menu"."component" IS '组件路径';
COMMENT ON COLUMN "system_menu"."component_name" IS '组件名';
COMMENT ON COLUMN "system_menu"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_menu"."visible" IS '是否可见';
COMMENT ON COLUMN "system_menu"."keep_alive" IS '是否缓存';
COMMENT ON COLUMN "system_menu"."always_show" IS '是否总是显示';
COMMENT ON COLUMN "system_menu"."creator" IS '创建者';
COMMENT ON COLUMN "system_menu"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_menu"."updater" IS '更新者';
COMMENT ON COLUMN "system_menu"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_menu"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "infra_api_access_log" (
    "id" BIGINT NOT NULL,
    "trace_id" varchar(64) NOT NULL DEFAULT '',
    "user_id" BIGINT NOT NULL DEFAULT 0,
    "user_type" SMALLINT NOT NULL DEFAULT 0,
    "application_name" varchar(50) NOT NULL,
    "request_method" varchar(16) NOT NULL DEFAULT '',
    "request_url" varchar(255) NOT NULL DEFAULT '',
    "request_params" text,
    "response_body" text,
    "user_ip" varchar(50) NOT NULL,
    "user_agent" varchar(512) NOT NULL,
    "operate_module" varchar(50) DEFAULT NULL,
    "operate_name" varchar(50) DEFAULT NULL,
    "operate_type" SMALLINT DEFAULT 0,
    "begin_time" timestamp NOT NULL,
    "end_time" timestamp NOT NULL,
    "duration" INTEGER NOT NULL,
    "result_code" INTEGER NOT NULL DEFAULT 0,
    "result_msg" varchar(512) DEFAULT '',
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_api_access_log"."id" IS '日志主键';
COMMENT ON COLUMN "infra_api_access_log"."trace_id" IS '链路追踪编号';
COMMENT ON COLUMN "infra_api_access_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "infra_api_access_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "infra_api_access_log"."application_name" IS '应用名';
COMMENT ON COLUMN "infra_api_access_log"."request_method" IS '请求方法名';
COMMENT ON COLUMN "infra_api_access_log"."request_url" IS '请求地址';
COMMENT ON COLUMN "infra_api_access_log"."request_params" IS '请求参数';
COMMENT ON COLUMN "infra_api_access_log"."response_body" IS '响应结果';
COMMENT ON COLUMN "infra_api_access_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "infra_api_access_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "infra_api_access_log"."operate_module" IS '操作模块';
COMMENT ON COLUMN "infra_api_access_log"."operate_name" IS '操作名';
COMMENT ON COLUMN "infra_api_access_log"."operate_type" IS '操作分类';
COMMENT ON COLUMN "infra_api_access_log"."begin_time" IS '开始请求时间';
COMMENT ON COLUMN "infra_api_access_log"."end_time" IS '结束请求时间';
COMMENT ON COLUMN "infra_api_access_log"."duration" IS '执行时长';
COMMENT ON COLUMN "infra_api_access_log"."result_code" IS '结果码';
COMMENT ON COLUMN "infra_api_access_log"."result_msg" IS '结果提示';
COMMENT ON COLUMN "infra_api_access_log"."creator" IS '创建者';
COMMENT ON COLUMN "infra_api_access_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_api_access_log"."updater" IS '更新者';
COMMENT ON COLUMN "infra_api_access_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_api_access_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "infra_api_access_log"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "infra_api_error_log" (
    "id" BIGINT NOT NULL,
    "trace_id" varchar(64) NOT NULL,
    "user_id" INTEGER NOT NULL DEFAULT 0,
    "user_type" SMALLINT NOT NULL DEFAULT 0,
    "application_name" varchar(50) NOT NULL,
    "request_method" varchar(16) NOT NULL,
    "request_url" varchar(255) NOT NULL,
    "request_params" varchar(8000) NOT NULL,
    "user_ip" varchar(50) NOT NULL,
    "user_agent" varchar(512) NOT NULL,
    "exception_time" timestamp NOT NULL,
    "exception_name" varchar(128) NOT NULL DEFAULT '',
    "exception_message" text NOT NULL,
    "exception_root_cause_message" text NOT NULL,
    "exception_stack_trace" text NOT NULL,
    "exception_class_name" varchar(512) NOT NULL,
    "exception_file_name" varchar(512) NOT NULL,
    "exception_method_name" varchar(512) NOT NULL,
    "exception_line_number" INTEGER NOT NULL,
    "process_status" SMALLINT NOT NULL,
    "process_time" timestamp,
    "process_user_id" INTEGER DEFAULT 0,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_api_error_log"."id" IS '编号';
COMMENT ON COLUMN "infra_api_error_log"."trace_id" IS '链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。';
COMMENT ON COLUMN "infra_api_error_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "infra_api_error_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "infra_api_error_log"."application_name" IS '应用名
     *
     * 目前读取 spring.application.name';
COMMENT ON COLUMN "infra_api_error_log"."request_method" IS '请求方法名';
COMMENT ON COLUMN "infra_api_error_log"."request_url" IS '请求地址';
COMMENT ON COLUMN "infra_api_error_log"."request_params" IS '请求参数';
COMMENT ON COLUMN "infra_api_error_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "infra_api_error_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "infra_api_error_log"."exception_time" IS '异常发生时间';
COMMENT ON COLUMN "infra_api_error_log"."exception_name" IS '异常名
     *
     * {@link Throwable#getClass()} 的类全名';
COMMENT ON COLUMN "infra_api_error_log"."exception_message" IS '异常导致的消息
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getMessage(Throwable)}';
COMMENT ON COLUMN "infra_api_error_log"."exception_root_cause_message" IS '异常导致的根消息
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getRootCauseMessage(Throwable)}';
COMMENT ON COLUMN "infra_api_error_log"."exception_stack_trace" IS '异常的栈轨迹
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getServiceException(Exception)}';
COMMENT ON COLUMN "infra_api_error_log"."exception_class_name" IS '异常发生的类全名
     *
     * {@link StackTraceElement#getClassName()}';
COMMENT ON COLUMN "infra_api_error_log"."exception_file_name" IS '异常发生的类文件
     *
     * {@link StackTraceElement#getFileName()}';
COMMENT ON COLUMN "infra_api_error_log"."exception_method_name" IS '异常发生的方法名
     *
     * {@link StackTraceElement#getMethodName()}';
COMMENT ON COLUMN "infra_api_error_log"."exception_line_number" IS '异常发生的方法所在行
     *
     * {@link StackTraceElement#getLineNumber()}';
COMMENT ON COLUMN "infra_api_error_log"."process_status" IS '处理状态';
COMMENT ON COLUMN "infra_api_error_log"."process_time" IS '处理时间';
COMMENT ON COLUMN "infra_api_error_log"."process_user_id" IS '处理用户编号';
COMMENT ON COLUMN "infra_api_error_log"."creator" IS '创建者';
COMMENT ON COLUMN "infra_api_error_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_api_error_log"."updater" IS '更新者';
COMMENT ON COLUMN "infra_api_error_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_api_error_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "infra_api_error_log"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "metadata_validation_range" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 1,
    "range_type" varchar(16) NOT NULL CHECK ("range_type" IN ('NUMBER', 'DATE')),
    "min_value" numeric(38,10),
    "max_value" numeric(38,10),
    "min_date" timestamp,
    "max_date" timestamp,
    "include_min" INTEGER DEFAULT 1,
    "include_max" INTEGER DEFAULT 1,
    "prompt_message" varchar(512),
    "run_mode" INTEGER,
    "app_id" BIGINT,
    "tenant_id" BIGINT,
    "creator" BIGINT,
    "updater" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "deleted" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_range"."id" IS '主键（雪花/外部生成）';
COMMENT ON COLUMN "metadata_validation_range"."group_id" IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN "metadata_validation_range"."entity_id" IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_range"."field_id" IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN "metadata_validation_range"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_range"."range_type" IS '范围类型：NUMBER/DATE';
COMMENT ON COLUMN "metadata_validation_range"."min_value" IS '最小值（Number类型）';
COMMENT ON COLUMN "metadata_validation_range"."max_value" IS '最大值（Number类型）';
COMMENT ON COLUMN "metadata_validation_range"."min_date" IS '最小时间（Date类型）';
COMMENT ON COLUMN "metadata_validation_range"."max_date" IS '最大时间（Date类型）';
COMMENT ON COLUMN "metadata_validation_range"."include_min" IS '是否包含最小边界：1-包含，0-不包含';
COMMENT ON COLUMN "metadata_validation_range"."include_max" IS '是否包含最大边界：1-包含，0-不包含';
COMMENT ON COLUMN "metadata_validation_range"."prompt_message" IS '提示信息';
COMMENT ON COLUMN "metadata_validation_range"."run_mode" IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN "metadata_validation_range"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_validation_range"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_validation_range"."creator" IS '创建人';
COMMENT ON COLUMN "metadata_validation_range"."updater" IS '更新人';
COMMENT ON COLUMN "metadata_validation_range"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_range"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_range"."deleted" IS '软删除标记：0-未删除，1-已删除';

-- Table Definition
CREATE TABLE "metadata_entity_field" (
    "id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_name" varchar(128) NOT NULL,
    "display_name" varchar(128) NOT NULL,
    "field_type" varchar(64) NOT NULL,
    "data_length" INTEGER,
    "decimal_places" INTEGER,
    "default_value" text,
    "description" varchar(256),
    "is_system_field" INTEGER NOT NULL DEFAULT 0,
    "is_primary_key" INTEGER NOT NULL DEFAULT 0,
    "is_required" INTEGER NOT NULL DEFAULT 0,
    "is_unique" INTEGER NOT NULL DEFAULT 0,
    "allow_null" INTEGER NOT NULL DEFAULT 1,
    "sort_order" INTEGER NOT NULL DEFAULT 0,
    "validation_rules" text,
    "run_mode" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    "status" INTEGER DEFAULT 1,
    "field_code" varchar(255),
    "dict_type_id" BIGINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_entity_field"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_entity_field"."entity_id" IS '实体ID';
COMMENT ON COLUMN "metadata_entity_field"."field_name" IS '字段名称';
COMMENT ON COLUMN "metadata_entity_field"."display_name" IS '显示名称';
COMMENT ON COLUMN "metadata_entity_field"."field_type" IS '字段类型';
COMMENT ON COLUMN "metadata_entity_field"."data_length" IS '数据长度';
COMMENT ON COLUMN "metadata_entity_field"."decimal_places" IS '小数位数';
COMMENT ON COLUMN "metadata_entity_field"."default_value" IS '默认值';
COMMENT ON COLUMN "metadata_entity_field"."description" IS '字段描述';
COMMENT ON COLUMN "metadata_entity_field"."is_system_field" IS '是否系统字段：1-是，0-不是';
COMMENT ON COLUMN "metadata_entity_field"."is_primary_key" IS '是否主键：1-是，0-不是';
COMMENT ON COLUMN "metadata_entity_field"."is_required" IS '是否必填：1-是，0-不是';
COMMENT ON COLUMN "metadata_entity_field"."is_unique" IS '是否唯一：1-是，0-不是';
COMMENT ON COLUMN "metadata_entity_field"."allow_null" IS '是否允许空值：1-是，0-不是';
COMMENT ON COLUMN "metadata_entity_field"."sort_order" IS '排序';
COMMENT ON COLUMN "metadata_entity_field"."validation_rules" IS '校验规则表达式';
COMMENT ON COLUMN "metadata_entity_field"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "metadata_entity_field"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_entity_field"."deleted" IS '软删除标识';
COMMENT ON COLUMN "metadata_entity_field"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_entity_field"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_entity_field"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_entity_field"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_entity_field"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "metadata_entity_field"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_entity_field"."status" IS '字段状态：1-开启，0-关闭';
COMMENT ON COLUMN "metadata_entity_field"."field_code" IS '字段编码';
COMMENT ON COLUMN "metadata_entity_field"."dict_type_id" IS '关联的字典类型ID(system_dict_type.id),用于SELECT/MULTI_SELECT字段复用系统字典,为null时使用自定义选项';

-- Table Definition
CREATE TABLE "infra_config" (
    "id" INTEGER NOT NULL,
    "category" varchar(50) NOT NULL,
    "type" SMALLINT NOT NULL,
    "name" varchar(100) NOT NULL DEFAULT '',
    "config_key" varchar(100) NOT NULL DEFAULT '',
    "value" varchar(500) NOT NULL DEFAULT '',
    "visible" SMALLINT NOT NULL,
    "remark" varchar(500) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_config"."id" IS '参数主键';
COMMENT ON COLUMN "infra_config"."category" IS '参数分组';
COMMENT ON COLUMN "infra_config"."type" IS '参数类型';
COMMENT ON COLUMN "infra_config"."name" IS '参数名称';
COMMENT ON COLUMN "infra_config"."config_key" IS '参数键名';
COMMENT ON COLUMN "infra_config"."value" IS '参数键值';
COMMENT ON COLUMN "infra_config"."visible" IS '是否可见';
COMMENT ON COLUMN "infra_config"."remark" IS '备注';
COMMENT ON COLUMN "infra_config"."creator" IS '创建者';
COMMENT ON COLUMN "infra_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_config"."updater" IS '更新者';
COMMENT ON COLUMN "infra_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_config"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "infra_data_source_config" (
    "id" BIGINT NOT NULL,
    "name" varchar(100) NOT NULL DEFAULT '',
    "url" varchar(1024) NOT NULL,
    "username" varchar(255) NOT NULL,
    "password" varchar(255) NOT NULL DEFAULT '',
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_data_source_config"."id" IS '主键编号';
COMMENT ON COLUMN "infra_data_source_config"."name" IS '参数名称';
COMMENT ON COLUMN "infra_data_source_config"."url" IS '数据源连接';
COMMENT ON COLUMN "infra_data_source_config"."username" IS '用户名';
COMMENT ON COLUMN "infra_data_source_config"."password" IS '密码';
COMMENT ON COLUMN "infra_data_source_config"."creator" IS '创建者';
COMMENT ON COLUMN "infra_data_source_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_data_source_config"."updater" IS '更新者';
COMMENT ON COLUMN "infra_data_source_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_data_source_config"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "infra_file" (
    "id" BIGINT NOT NULL,
    "config_id" BIGINT,
    "name" varchar(256) DEFAULT NULL,
    "path" varchar(512) NOT NULL,
    "url" varchar(1024) NOT NULL,
    "type" varchar(128) DEFAULT NULL,
    "size" INTEGER NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_file"."id" IS '文件编号';
COMMENT ON COLUMN "infra_file"."config_id" IS '配置编号';
COMMENT ON COLUMN "infra_file"."name" IS '文件名';
COMMENT ON COLUMN "infra_file"."path" IS '文件路径';
COMMENT ON COLUMN "infra_file"."url" IS '文件 URL';
COMMENT ON COLUMN "infra_file"."type" IS '文件类型';
COMMENT ON COLUMN "infra_file"."size" IS '文件大小';
COMMENT ON COLUMN "infra_file"."creator" IS '创建者';
COMMENT ON COLUMN "infra_file"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_file"."updater" IS '更新者';
COMMENT ON COLUMN "infra_file"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_file"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "infra_file_config" (
    "id" BIGINT NOT NULL,
    "name" varchar(63) NOT NULL,
    "storage" SMALLINT NOT NULL,
    "remark" varchar(255) DEFAULT NULL,
    "master" SMALLINT NOT NULL,
    "config" json NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_file_config"."id" IS '编号';
COMMENT ON COLUMN "infra_file_config"."name" IS '配置名';
COMMENT ON COLUMN "infra_file_config"."storage" IS '存储器';
COMMENT ON COLUMN "infra_file_config"."remark" IS '备注';
COMMENT ON COLUMN "infra_file_config"."master" IS '是否为主配置';
COMMENT ON COLUMN "infra_file_config"."config" IS '存储配置';
COMMENT ON COLUMN "infra_file_config"."creator" IS '创建者';
COMMENT ON COLUMN "infra_file_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_file_config"."updater" IS '更新者';
COMMENT ON COLUMN "infra_file_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_file_config"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "metadata_validation_rule_group" (
    "id" BIGINT NOT NULL,
    "rg_name" varchar(100) NOT NULL,
    "rg_desc" text,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "val_method" varchar(255),
    "pop_prompt" varchar(255),
    "pop_type" varchar(255),
    "rg_status" INTEGER NOT NULL DEFAULT 1,
    "validation_type" varchar(50),
    "entity_id" BIGINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_rule_group"."id" IS '规则组唯一标识';
COMMENT ON COLUMN "metadata_validation_rule_group"."rg_name" IS '规则组名称，如"客户信用评级规则"';
COMMENT ON COLUMN "metadata_validation_rule_group"."rg_desc" IS '规则组描述';
COMMENT ON COLUMN "metadata_validation_rule_group"."val_method" IS '校验方式，如：满足条件时，不允许提交表单，并弹窗提示';
COMMENT ON COLUMN "metadata_validation_rule_group"."pop_prompt" IS '弹窗提示内容';
COMMENT ON COLUMN "metadata_validation_rule_group"."pop_type" IS '弹窗类型，如：短提示弹窗，长提示弹窗等';
COMMENT ON COLUMN "metadata_validation_rule_group"."rg_status" IS '状态：1-激活，0-非激活';
COMMENT ON COLUMN "metadata_validation_rule_group"."validation_type" IS '校验类型：REQUIRED/UNIQUE/LENGTH/RANGE/FORMAT/CHILD_NOT_EMPTY/SELF_DEFINED';
COMMENT ON COLUMN "metadata_validation_rule_group"."entity_id" IS '实体 id';

-- Table Definition
CREATE TABLE "metadata_validation_format" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_id" BIGINT NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 1,
    "format_code" varchar(64) NOT NULL,
    "regex_pattern" text,
    "flags" varchar(64),
    "prompt_message" varchar(512),
    "run_mode" INTEGER,
    "app_id" BIGINT,
    "tenant_id" BIGINT,
    "creator" BIGINT,
    "updater" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "deleted" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_format"."id" IS '主键（雪花/外部生成）';
COMMENT ON COLUMN "metadata_validation_format"."group_id" IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN "metadata_validation_format"."entity_id" IS '业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_format"."field_id" IS '实体字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN "metadata_validation_format"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_format"."format_code" IS '格式类型：REGEX/EMAIL/MOBILE/ID_CARD/URL/IP/...';
COMMENT ON COLUMN "metadata_validation_format"."regex_pattern" IS '当format_code=REGEX时的正则表达式';
COMMENT ON COLUMN "metadata_validation_format"."flags" IS '正则标志位：i/m/s等';
COMMENT ON COLUMN "metadata_validation_format"."prompt_message" IS '提示信息';
COMMENT ON COLUMN "metadata_validation_format"."run_mode" IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN "metadata_validation_format"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_validation_format"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_validation_format"."creator" IS '创建人';
COMMENT ON COLUMN "metadata_validation_format"."updater" IS '更新人';
COMMENT ON COLUMN "metadata_validation_format"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_format"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_format"."deleted" IS '软删除标记：0-未删除，1-已删除';

-- Table Definition
CREATE TABLE "metadata_business_entity" (
    "id" BIGINT NOT NULL,
    "display_name" varchar(64) NOT NULL,
    "code" varchar(32) NOT NULL,
    "entity_type" INTEGER NOT NULL DEFAULT 1,
    "description" varchar(512),
    "datasource_id" BIGINT NOT NULL,
    "table_name" varchar(128),
    "run_mode" INTEGER NOT NULL DEFAULT 0,
    "app_id" BIGINT NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    "display_config" text,
    "status" SMALLINT NOT NULL DEFAULT 1,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_business_entity"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_business_entity"."display_name" IS '实体名称';
COMMENT ON COLUMN "metadata_business_entity"."code" IS '实体编码';
COMMENT ON COLUMN "metadata_business_entity"."entity_type" IS '实体类型(1:自建表 2:复用已有表 3中间表用做多对多关联用不给前端展示)';
COMMENT ON COLUMN "metadata_business_entity"."description" IS '实体描述';
COMMENT ON COLUMN "metadata_business_entity"."datasource_id" IS '数据源ID';
COMMENT ON COLUMN "metadata_business_entity"."table_name" IS '对应数据表名';
COMMENT ON COLUMN "metadata_business_entity"."run_mode" IS '运行模式：0 编辑态，1 运行态';
COMMENT ON COLUMN "metadata_business_entity"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_business_entity"."deleted" IS '软删除标识';
COMMENT ON COLUMN "metadata_business_entity"."creator" IS '创建人ID';
COMMENT ON COLUMN "metadata_business_entity"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_business_entity"."updater" IS '更新人ID';
COMMENT ON COLUMN "metadata_business_entity"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_business_entity"."lock_version" IS '版本锁标识';
COMMENT ON COLUMN "metadata_business_entity"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_business_entity"."display_config" IS '前端显示配置json';
COMMENT ON COLUMN "metadata_business_entity"."status" IS '0 关闭，1 开启';

-- Table Definition
CREATE TABLE "system_dict_data" (
    "id" BIGINT NOT NULL,
    "sort" INTEGER NOT NULL DEFAULT 0,
    "label" varchar(100) NOT NULL DEFAULT '',
    "value" varchar(100) NOT NULL DEFAULT '',
    "dict_type" varchar(100) NOT NULL DEFAULT '',
    "status" SMALLINT NOT NULL DEFAULT 0,
    "color_type" varchar(100) DEFAULT '',
    "css_class" varchar(100) DEFAULT '',
    "remark" varchar(500) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_dict_data"."id" IS '字典编码';
COMMENT ON COLUMN "system_dict_data"."sort" IS '字典排序';
COMMENT ON COLUMN "system_dict_data"."label" IS '字典标签';
COMMENT ON COLUMN "system_dict_data"."value" IS '字典键值';
COMMENT ON COLUMN "system_dict_data"."dict_type" IS '字典类型';
COMMENT ON COLUMN "system_dict_data"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_dict_data"."color_type" IS '颜色类型';
COMMENT ON COLUMN "system_dict_data"."css_class" IS 'css 样式';
COMMENT ON COLUMN "system_dict_data"."remark" IS '备注';
COMMENT ON COLUMN "system_dict_data"."creator" IS '创建者';
COMMENT ON COLUMN "system_dict_data"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_dict_data"."updater" IS '更新者';
COMMENT ON COLUMN "system_dict_data"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_dict_data"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "system_dept" (
    "id" BIGINT NOT NULL,
    "name" varchar(30) NOT NULL DEFAULT '',
    "parent_id" BIGINT NOT NULL DEFAULT 0,
    "sort" INTEGER NOT NULL DEFAULT 0,
    "leader_user_id" BIGINT,
    "phone" varchar(11) DEFAULT NULL,
    "email" varchar(50) DEFAULT NULL,
    "status" SMALLINT NOT NULL DEFAULT 0,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "tenant_id" BIGINT NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "remark" varchar(512),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_dept"."id" IS '部门id';
COMMENT ON COLUMN "system_dept"."name" IS '部门名称';
COMMENT ON COLUMN "system_dept"."parent_id" IS '父部门id';
COMMENT ON COLUMN "system_dept"."sort" IS '显示顺序';
COMMENT ON COLUMN "system_dept"."leader_user_id" IS '负责人';
COMMENT ON COLUMN "system_dept"."phone" IS '联系电话';
COMMENT ON COLUMN "system_dept"."email" IS '邮箱';
COMMENT ON COLUMN "system_dept"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_dept"."creator" IS '创建者';
COMMENT ON COLUMN "system_dept"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_dept"."updater" IS '更新者';
COMMENT ON COLUMN "system_dept"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_dept"."tenant_id" IS '租户编号';
COMMENT ON COLUMN "system_dept"."remark" IS '简介和备注';

-- Table Definition
CREATE TABLE "metadata_validation_child_not_empty" (
    "id" BIGINT NOT NULL,
    "group_id" BIGINT NOT NULL,
    "entity_id" BIGINT NOT NULL,
    "field_id" BIGINT,
    "child_entity_id" BIGINT NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 1,
    "min_rows" INTEGER DEFAULT 1,
    "prompt_message" varchar(512),
    "run_mode" INTEGER,
    "app_id" BIGINT,
    "tenant_id" BIGINT,
    "creator" BIGINT,
    "updater" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "deleted" INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_validation_child_not_empty"."id" IS '主键（雪花/外部生成）';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."group_id" IS '规则组ID，关联metadata_validation_rule_group.id';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."entity_id" IS '父业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."field_id" IS '父实体中指向子表的字段ID，关联metadata_entity_field.id';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."child_entity_id" IS '子业务实体ID，关联metadata_business_entity.id';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."is_enabled" IS '是否启用：1-启用，0-禁用';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."min_rows" IS '最少行数（默认1）';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."prompt_message" IS '提示信息';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."run_mode" IS '运行模式：0-编辑态，1-运行态';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."app_id" IS '应用ID';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."creator" IS '创建人';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."updater" IS '更新人';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."update_time" IS '更新时间';
COMMENT ON COLUMN "metadata_validation_child_not_empty"."deleted" IS '软删除标记：0-未删除，1-已删除';

-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS field_type_mapping_id_seq;

-- Table Definition
CREATE TABLE "metadata_field_type_mapping" (
    "id" BIGINT IDENTITY(1,1) NOT NULL,
    "business_field_type" varchar(50) NOT NULL,
    "business_meaning" text NOT NULL,
    "database_type" varchar(20) NOT NULL,
    "database_field" varchar(100) NOT NULL,
    "is_default" INTEGER DEFAULT 0,
    "default_length" INTEGER DEFAULT 255,
    "default_decimal_places" INTEGER DEFAULT 0,
    "created_at" timestamp DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "metadata_field_type_mapping"."id" IS '主键ID';
COMMENT ON COLUMN "metadata_field_type_mapping"."business_field_type" IS '业务字段类型';
COMMENT ON COLUMN "metadata_field_type_mapping"."business_meaning" IS '业务含义';
COMMENT ON COLUMN "metadata_field_type_mapping"."database_type" IS '数据库类型';
COMMENT ON COLUMN "metadata_field_type_mapping"."database_field" IS '数据库中对应的字段';
COMMENT ON COLUMN "metadata_field_type_mapping"."is_default" IS '默认还是备选：0-备选，1-默认';
COMMENT ON COLUMN "metadata_field_type_mapping"."default_length" IS '默认长度';
COMMENT ON COLUMN "metadata_field_type_mapping"."default_decimal_places" IS '默认小数点后长度';
COMMENT ON COLUMN "metadata_field_type_mapping"."created_at" IS '创建时间';
COMMENT ON COLUMN "metadata_field_type_mapping"."updated_at" IS '更新时间';

-- Table Definition
CREATE TABLE "infra_file_content" (
    "id" BIGINT NOT NULL,
    "config_id" BIGINT NOT NULL,
    "path" varchar(512) NOT NULL,
    "content" BLOB NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_file_content"."id" IS '编号';
COMMENT ON COLUMN "infra_file_content"."config_id" IS '配置编号';
COMMENT ON COLUMN "infra_file_content"."path" IS '文件路径';
COMMENT ON COLUMN "infra_file_content"."content" IS '文件内容';
COMMENT ON COLUMN "infra_file_content"."creator" IS '创建者';
COMMENT ON COLUMN "infra_file_content"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_file_content"."updater" IS '更新者';
COMMENT ON COLUMN "infra_file_content"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_file_content"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "infra_job" (
    "id" BIGINT NOT NULL,
    "name" varchar(32) NOT NULL,
    "status" SMALLINT NOT NULL,
    "handler_name" varchar(64) NOT NULL,
    "handler_param" varchar(255) DEFAULT NULL,
    "cron_expression" varchar(32) NOT NULL,
    "retry_count" INTEGER NOT NULL DEFAULT 0,
    "retry_interval" INTEGER NOT NULL DEFAULT 0,
    "monitor_timeout" INTEGER NOT NULL DEFAULT 0,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_job"."id" IS '任务编号';
COMMENT ON COLUMN "infra_job"."name" IS '任务名称';
COMMENT ON COLUMN "infra_job"."status" IS '任务状态';
COMMENT ON COLUMN "infra_job"."handler_name" IS '处理器的名字';
COMMENT ON COLUMN "infra_job"."handler_param" IS '处理器的参数';
COMMENT ON COLUMN "infra_job"."cron_expression" IS 'CRON 表达式';
COMMENT ON COLUMN "infra_job"."retry_count" IS '重试次数';
COMMENT ON COLUMN "infra_job"."retry_interval" IS '重试间隔';
COMMENT ON COLUMN "infra_job"."monitor_timeout" IS '监控超时时间';
COMMENT ON COLUMN "infra_job"."creator" IS '创建者';
COMMENT ON COLUMN "infra_job"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_job"."updater" IS '更新者';
COMMENT ON COLUMN "infra_job"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_job"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "infra_job_log" (
    "id" BIGINT NOT NULL,
    "job_id" BIGINT NOT NULL,
    "handler_name" varchar(64) NOT NULL,
    "handler_param" varchar(255) DEFAULT NULL,
    "execute_index" SMALLINT NOT NULL DEFAULT 1,
    "begin_time" timestamp NOT NULL,
    "end_time" timestamp,
    "duration" INTEGER,
    "status" SMALLINT NOT NULL,
    "result" varchar(4000) DEFAULT '',
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "infra_job_log"."id" IS '日志编号';
COMMENT ON COLUMN "infra_job_log"."job_id" IS '任务编号';
COMMENT ON COLUMN "infra_job_log"."handler_name" IS '处理器的名字';
COMMENT ON COLUMN "infra_job_log"."handler_param" IS '处理器的参数';
COMMENT ON COLUMN "infra_job_log"."execute_index" IS '第几次执行';
COMMENT ON COLUMN "infra_job_log"."begin_time" IS '开始执行时间';
COMMENT ON COLUMN "infra_job_log"."end_time" IS '结束执行时间';
COMMENT ON COLUMN "infra_job_log"."duration" IS '执行时长';
COMMENT ON COLUMN "infra_job_log"."status" IS '任务状态';
COMMENT ON COLUMN "infra_job_log"."result" IS '结果数据';
COMMENT ON COLUMN "infra_job_log"."creator" IS '创建者';
COMMENT ON COLUMN "infra_job_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "infra_job_log"."updater" IS '更新者';
COMMENT ON COLUMN "infra_job_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "infra_job_log"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "system_dict_type" (
    "id" BIGINT NOT NULL,
    "name" varchar(100) NOT NULL DEFAULT '',
    "type" varchar(100) NOT NULL DEFAULT '',
    "status" SMALLINT NOT NULL DEFAULT 0,
    "remark" varchar(500) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "deleted_time" timestamp,
    "dict_owner_type" varchar(20) NOT NULL DEFAULT 'tenant',
    "dict_owner_id" BIGINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_dict_type"."id" IS '字典主键';
COMMENT ON COLUMN "system_dict_type"."name" IS '字典名称';
COMMENT ON COLUMN "system_dict_type"."type" IS '字典类型';
COMMENT ON COLUMN "system_dict_type"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_dict_type"."remark" IS '备注';
COMMENT ON COLUMN "system_dict_type"."creator" IS '创建者';
COMMENT ON COLUMN "system_dict_type"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_dict_type"."updater" IS '更新者';
COMMENT ON COLUMN "system_dict_type"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_dict_type"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_dict_type"."deleted_time" IS '删除时间';
COMMENT ON COLUMN "system_dict_type"."dict_owner_type" IS '字典所有者类型（app-应用自定义字典，tenant-空间公共字典）';
COMMENT ON COLUMN "system_dict_type"."dict_owner_id" IS '字典所有者ID（应用ID或租户ID）';

-- Table Definition
CREATE TABLE "system_login_log" (
    "id" BIGINT NOT NULL,
    "log_type" BIGINT NOT NULL,
    "trace_id" varchar(64) NOT NULL DEFAULT '',
    "user_id" BIGINT NOT NULL DEFAULT 0,
    "user_type" SMALLINT NOT NULL DEFAULT 0,
    "username" varchar(50) NOT NULL DEFAULT '',
    "result" SMALLINT NOT NULL,
    "user_ip" varchar(50) NOT NULL,
    "user_agent" varchar(512) NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_login_log"."id" IS '访问ID';
COMMENT ON COLUMN "system_login_log"."log_type" IS '日志类型';
COMMENT ON COLUMN "system_login_log"."trace_id" IS '链路追踪编号';
COMMENT ON COLUMN "system_login_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_login_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_login_log"."username" IS '用户账号';
COMMENT ON COLUMN "system_login_log"."result" IS '登陆结果';
COMMENT ON COLUMN "system_login_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "system_login_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "system_login_log"."creator" IS '创建者';
COMMENT ON COLUMN "system_login_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_login_log"."updater" IS '更新者';
COMMENT ON COLUMN "system_login_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_login_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_login_log"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_uid_worker_node" (
    "id" INTEGER NOT NULL,
    "worker_host" varchar(64),
    "worker_port" varchar(64),
    "node_type" SMALLINT,
    "launch_date" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "system_mail_log" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT,
    "user_type" SMALLINT,
    "to_mail" varchar(255) NOT NULL,
    "account_id" BIGINT NOT NULL,
    "from_mail" varchar(255) NOT NULL,
    "template_id" BIGINT NOT NULL,
    "template_code" varchar(63) NOT NULL,
    "template_nickname" varchar(255) DEFAULT NULL,
    "template_title" varchar(255) NOT NULL,
    "template_content" varchar(10240) NOT NULL,
    "template_params" varchar(255) NOT NULL,
    "send_status" SMALLINT NOT NULL DEFAULT 0,
    "send_time" timestamp,
    "send_message_id" varchar(255) DEFAULT NULL,
    "send_exception" varchar(4096) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_mail_log"."id" IS '编号';
COMMENT ON COLUMN "system_mail_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_mail_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_mail_log"."to_mail" IS '接收邮箱地址';
COMMENT ON COLUMN "system_mail_log"."account_id" IS '邮箱账号编号';
COMMENT ON COLUMN "system_mail_log"."from_mail" IS '发送邮箱地址';
COMMENT ON COLUMN "system_mail_log"."template_id" IS '模板编号';
COMMENT ON COLUMN "system_mail_log"."template_code" IS '模板编码';
COMMENT ON COLUMN "system_mail_log"."template_nickname" IS '模版发送人名称';
COMMENT ON COLUMN "system_mail_log"."template_title" IS '邮件标题';
COMMENT ON COLUMN "system_mail_log"."template_content" IS '邮件内容';
COMMENT ON COLUMN "system_mail_log"."template_params" IS '邮件参数';
COMMENT ON COLUMN "system_mail_log"."send_status" IS '发送状态';
COMMENT ON COLUMN "system_mail_log"."send_time" IS '发送时间';
COMMENT ON COLUMN "system_mail_log"."send_message_id" IS '发送返回的消息 ID';
COMMENT ON COLUMN "system_mail_log"."send_exception" IS '发送异常';
COMMENT ON COLUMN "system_mail_log"."creator" IS '创建者';
COMMENT ON COLUMN "system_mail_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_mail_log"."updater" IS '更新者';
COMMENT ON COLUMN "system_mail_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_mail_log"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "system_mail_template" (
    "id" BIGINT NOT NULL,
    "name" varchar(63) NOT NULL,
    "code" varchar(63) NOT NULL,
    "account_id" BIGINT NOT NULL,
    "nickname" varchar(255) DEFAULT NULL,
    "title" varchar(255) NOT NULL,
    "content" varchar(10240) NOT NULL,
    "params" varchar(255) NOT NULL,
    "status" SMALLINT NOT NULL,
    "remark" varchar(255) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_mail_template"."id" IS '编号';
COMMENT ON COLUMN "system_mail_template"."name" IS '模板名称';
COMMENT ON COLUMN "system_mail_template"."code" IS '模板编码';
COMMENT ON COLUMN "system_mail_template"."account_id" IS '发送的邮箱账号编号';
COMMENT ON COLUMN "system_mail_template"."nickname" IS '发送人名称';
COMMENT ON COLUMN "system_mail_template"."title" IS '模板标题';
COMMENT ON COLUMN "system_mail_template"."content" IS '模板内容';
COMMENT ON COLUMN "system_mail_template"."params" IS '参数数组';
COMMENT ON COLUMN "system_mail_template"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_mail_template"."remark" IS '备注';
COMMENT ON COLUMN "system_mail_template"."creator" IS '创建者';
COMMENT ON COLUMN "system_mail_template"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_mail_template"."updater" IS '更新者';
COMMENT ON COLUMN "system_mail_template"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_mail_template"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "etl_workflow_table" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "workflow_id" BIGINT NOT NULL,
    "relation" varchar(16) NOT NULL,
    "table_id" BIGINT NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "datasource_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Table Definition
CREATE TABLE "etl_catalog" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "datasource_id" BIGINT NOT NULL,
    "catalog_name" varchar(256) NOT NULL,
    "display_name" varchar(256) DEFAULT NULL,
    "meta_info" text NOT NULL,
    "remarks" varchar(256),
    "declaration" varchar(256),
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_catalog"."id" IS '主键ID';
COMMENT ON COLUMN "etl_catalog"."datasource_id" IS '数据源Id';
COMMENT ON COLUMN "etl_catalog"."catalog_name" IS 'catalog名称';
COMMENT ON COLUMN "etl_catalog"."display_name" IS '展示名称（用户可修改）';
COMMENT ON COLUMN "etl_catalog"."meta_info" IS '采集到的信息';
COMMENT ON COLUMN "etl_catalog"."remarks" IS '采集到的描述';
COMMENT ON COLUMN "etl_catalog"."declaration" IS '描述（用户可修改）';
COMMENT ON COLUMN "etl_catalog"."deleted" IS '是否删除（逻辑删除）';

-- Table Definition
CREATE TABLE "etl_schema" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "datasource_id" BIGINT NOT NULL,
    "catalog_id" BIGINT NOT NULL,
    "schema_name" varchar(256) NOT NULL,
    "display_name" varchar(256) DEFAULT NULL,
    "meta_info" text NOT NULL,
    "remarks" varchar(256),
    "declaration" varchar(256),
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_schema"."id" IS '主键Id';
COMMENT ON COLUMN "etl_schema"."datasource_id" IS '数据源Id';
COMMENT ON COLUMN "etl_schema"."catalog_id" IS 'catalog表Id';
COMMENT ON COLUMN "etl_schema"."schema_name" IS '名称';
COMMENT ON COLUMN "etl_schema"."display_name" IS '展示名称（用户可修改）';
COMMENT ON COLUMN "etl_schema"."meta_info" IS '采集的数据';
COMMENT ON COLUMN "etl_schema"."remarks" IS '采集到的描述';
COMMENT ON COLUMN "etl_schema"."declaration" IS '描述（用户可修改）';
COMMENT ON COLUMN "etl_schema"."deleted" IS '是否删除（逻辑删除）';

-- Table Definition
CREATE TABLE "etl_table" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "datasource_id" BIGINT NOT NULL,
    "catalog_id" BIGINT NOT NULL,
    "schema_id" BIGINT NOT NULL,
    "table_name" varchar(256) NOT NULL,
    "table_type" varchar(16) NOT NULL,
    "display_name" varchar(256) DEFAULT NULL,
    "meta_info" text NOT NULL,
    "remarks" varchar(256),
    "declaration" varchar(256),
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_table"."id" IS '主键Id';
COMMENT ON COLUMN "etl_table"."datasource_id" IS '数据源Id';
COMMENT ON COLUMN "etl_table"."catalog_id" IS 'catalog表Id';
COMMENT ON COLUMN "etl_table"."schema_id" IS 'schema表id';
COMMENT ON COLUMN "etl_table"."table_name" IS '表名称';
COMMENT ON COLUMN "etl_table"."table_type" IS '表类别，如table和view等';
COMMENT ON COLUMN "etl_table"."display_name" IS '表展示名称（用户可修改）';
COMMENT ON COLUMN "etl_table"."meta_info" IS '采集表信息-字段信息';
COMMENT ON COLUMN "etl_table"."remarks" IS '采集到的表描述';
COMMENT ON COLUMN "etl_table"."declaration" IS '表的描述（用户可修改）';
COMMENT ON COLUMN "etl_table"."deleted" IS '是否删除（逻辑删除）';

-- Table Definition
CREATE TABLE "etl_datasource" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "datasource_code" varchar(40) NOT NULL,
    "datasource_name" varchar(200) NOT NULL,
    "datasource_type" varchar(64) NOT NULL,
    "config" text NOT NULL,
    "collect_status" varchar(16) NOT NULL DEFAULT 'none',
    "collect_start_time" timestamp,
    "collect_end_time" timestamp,
    "readonly" INTEGER NOT NULL DEFAULT 1,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "declaration" varchar(256),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_datasource"."id" IS '主键Id';
COMMENT ON COLUMN "etl_datasource"."application_id" IS '应用Id';
COMMENT ON COLUMN "etl_datasource"."datasource_code" IS '数据源编码';
COMMENT ON COLUMN "etl_datasource"."datasource_name" IS '数据源名称';
COMMENT ON COLUMN "etl_datasource"."datasource_type" IS '数据源类型，比如MySQL、PostgreSQL等';
COMMENT ON COLUMN "etl_datasource"."config" IS '数据源配置信息（JSON）';
COMMENT ON COLUMN "etl_datasource"."collect_status" IS '采集状态，枚举值(none,required,success,failed,running)，默认为none';
COMMENT ON COLUMN "etl_datasource"."collect_start_time" IS '采集开始时间';
COMMENT ON COLUMN "etl_datasource"."collect_end_time" IS '采集结束时间';
COMMENT ON COLUMN "etl_datasource"."readonly" IS '是否只读数据源';
COMMENT ON COLUMN "etl_datasource"."deleted" IS '是否删除（逻辑删除）';
COMMENT ON COLUMN "etl_datasource"."declaration" IS '数据源描述';

-- Table Definition
CREATE TABLE "etl_execution_log" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "workflow_id" BIGINT NOT NULL,
    "business_date" timestamp NOT NULL,
    "start_time" timestamp NOT NULL,
    "end_time" timestamp,
    "duration_time" BIGINT,
    "trigger_type" varchar(32) NOT NULL,
    "trigger_user" BIGINT NOT NULL,
    "task_status" varchar(32) NOT NULL,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_execution_log"."id" IS '主键Id';
COMMENT ON COLUMN "etl_execution_log"."application_id" IS '应用Id';
COMMENT ON COLUMN "etl_execution_log"."workflow_id" IS '工作流Id';
COMMENT ON COLUMN "etl_execution_log"."business_date" IS '计划执行日期';
COMMENT ON COLUMN "etl_execution_log"."start_time" IS '开始时间';
COMMENT ON COLUMN "etl_execution_log"."end_time" IS '结束时间';
COMMENT ON COLUMN "etl_execution_log"."duration_time" IS '执行时间（毫秒）';
COMMENT ON COLUMN "etl_execution_log"."trigger_type" IS '触发类型';
COMMENT ON COLUMN "etl_execution_log"."trigger_user" IS '触发用户';
COMMENT ON COLUMN "etl_execution_log"."task_status" IS '任务状态';
COMMENT ON COLUMN "etl_execution_log"."deleted" IS '是否删除（逻辑删除）';
COMMENT ON COLUMN "etl_execution_log"."creator" IS '创建人';
COMMENT ON COLUMN "etl_execution_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "etl_execution_log"."updater" IS '更新人';
COMMENT ON COLUMN "etl_execution_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "etl_execution_log"."lock_version" IS '乐观锁版本';
COMMENT ON COLUMN "etl_execution_log"."tenant_id" IS '租户ID';

-- Table Definition
CREATE TABLE "formula_formula" (
    "id" BIGINT NOT NULL,
    "name" varchar(128) NOT NULL,
    "description" varchar(1024),
    "expression" text NOT NULL,
    "use_scene" varchar(64) NOT NULL,
    "type" varchar(32) NOT NULL,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "formula_formula"."id" IS '主键ID';
COMMENT ON COLUMN "formula_formula"."name" IS '公式名称';
COMMENT ON COLUMN "formula_formula"."description" IS '公式描述';
COMMENT ON COLUMN "formula_formula"."expression" IS '公式表达式';
COMMENT ON COLUMN "formula_formula"."use_scene" IS '使用场景';
COMMENT ON COLUMN "formula_formula"."type" IS '公式类型（内置/自定义）';
COMMENT ON COLUMN "formula_formula"."creator" IS '创建者';
COMMENT ON COLUMN "formula_formula"."create_time" IS '创建时间';
COMMENT ON COLUMN "formula_formula"."updater" IS '更新者';
COMMENT ON COLUMN "formula_formula"."update_time" IS '更新时间';
COMMENT ON COLUMN "formula_formula"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "formula_formula"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "formula_formula"."deleted" IS '软删标记，0未删除，非0已删除';

-- Table Definition
CREATE TABLE "system_oauth2_approve" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "user_type" SMALLINT NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "scope" varchar(255) NOT NULL DEFAULT '',
    "approved" SMALLINT NOT NULL DEFAULT 0,
    "expires_time" timestamp NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_oauth2_approve"."id" IS '编号';
COMMENT ON COLUMN "system_oauth2_approve"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_oauth2_approve"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_oauth2_approve"."client_id" IS '客户端编号';
COMMENT ON COLUMN "system_oauth2_approve"."scope" IS '授权范围';
COMMENT ON COLUMN "system_oauth2_approve"."approved" IS '是否接受';
COMMENT ON COLUMN "system_oauth2_approve"."expires_time" IS '过期时间';
COMMENT ON COLUMN "system_oauth2_approve"."creator" IS '创建者';
COMMENT ON COLUMN "system_oauth2_approve"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_oauth2_approve"."updater" IS '更新者';
COMMENT ON COLUMN "system_oauth2_approve"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_oauth2_approve"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_oauth2_approve"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_oauth2_access_token" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "user_type" SMALLINT NOT NULL,
    "user_info" varchar(512) NOT NULL,
    "access_token" varchar(255) NOT NULL,
    "refresh_token" varchar(32) NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "scopes" varchar(255) DEFAULT NULL,
    "expires_time" timestamp NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_oauth2_access_token"."id" IS '编号';
COMMENT ON COLUMN "system_oauth2_access_token"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_oauth2_access_token"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_oauth2_access_token"."user_info" IS '用户信息';
COMMENT ON COLUMN "system_oauth2_access_token"."access_token" IS '访问令牌';
COMMENT ON COLUMN "system_oauth2_access_token"."refresh_token" IS '刷新令牌';
COMMENT ON COLUMN "system_oauth2_access_token"."client_id" IS '客户端编号';
COMMENT ON COLUMN "system_oauth2_access_token"."scopes" IS '授权范围';
COMMENT ON COLUMN "system_oauth2_access_token"."expires_time" IS '过期时间';
COMMENT ON COLUMN "system_oauth2_access_token"."creator" IS '创建者';
COMMENT ON COLUMN "system_oauth2_access_token"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_oauth2_access_token"."updater" IS '更新者';
COMMENT ON COLUMN "system_oauth2_access_token"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_oauth2_access_token"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_oauth2_access_token"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "formula_function" (
    "id" BIGINT NOT NULL,
    "type" varchar(64) NOT NULL,
    "name" varchar(128) NOT NULL,
    "expression" text NOT NULL,
    "summary" varchar(512),
    "usage" text,
    "example" text,
    "return_type" varchar(64) NOT NULL,
    "version" varchar(32) NOT NULL DEFAULT 1,
    "status" INTEGER NOT NULL DEFAULT 1,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "tenant_id" BIGINT NOT NULL,
    "lock_version" BIGINT NOT NULL DEFAULT 0,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "formula_function"."id" IS '主键ID';
COMMENT ON COLUMN "formula_function"."type" IS '函数类型';
COMMENT ON COLUMN "formula_function"."name" IS '函数名称';
COMMENT ON COLUMN "formula_function"."expression" IS '函数表达式';
COMMENT ON COLUMN "formula_function"."summary" IS '函数简介';
COMMENT ON COLUMN "formula_function"."usage" IS '函数用法（md格式）';
COMMENT ON COLUMN "formula_function"."example" IS '函数示例（md格式）';
COMMENT ON COLUMN "formula_function"."return_type" IS '返回值类型';
COMMENT ON COLUMN "formula_function"."version" IS '函数版本';
COMMENT ON COLUMN "formula_function"."status" IS '函数状态';
COMMENT ON COLUMN "formula_function"."creator" IS '创建者';
COMMENT ON COLUMN "formula_function"."create_time" IS '创建时间';
COMMENT ON COLUMN "formula_function"."updater" IS '更新者';
COMMENT ON COLUMN "formula_function"."update_time" IS '更新时间';
COMMENT ON COLUMN "formula_function"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "formula_function"."lock_version" IS '乐观锁';
COMMENT ON COLUMN "formula_function"."deleted" IS '软删标记，0未删除，非0已删除';

-- Table Definition
CREATE TABLE "system_oauth2_client" (
    "id" BIGINT NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "secret" varchar(255) NOT NULL,
    "name" varchar(255) NOT NULL,
    "logo" varchar(255) NOT NULL,
    "description" varchar(255) DEFAULT NULL,
    "status" SMALLINT NOT NULL,
    "access_token_validity_seconds" INTEGER NOT NULL,
    "refresh_token_validity_seconds" INTEGER NOT NULL,
    "redirect_uris" varchar(255) NOT NULL,
    "authorized_grant_types" varchar(255) NOT NULL,
    "scopes" varchar(255) DEFAULT NULL,
    "auto_approve_scopes" varchar(255) DEFAULT NULL,
    "authorities" varchar(255) DEFAULT NULL,
    "resource_ids" varchar(255) DEFAULT NULL,
    "additional_information" varchar(4096) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_oauth2_client"."id" IS '编号';
COMMENT ON COLUMN "system_oauth2_client"."client_id" IS '客户端编号';
COMMENT ON COLUMN "system_oauth2_client"."secret" IS '客户端密钥';
COMMENT ON COLUMN "system_oauth2_client"."name" IS '应用名';
COMMENT ON COLUMN "system_oauth2_client"."logo" IS '应用图标';
COMMENT ON COLUMN "system_oauth2_client"."description" IS '应用描述';
COMMENT ON COLUMN "system_oauth2_client"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_oauth2_client"."access_token_validity_seconds" IS '访问令牌的有效期';
COMMENT ON COLUMN "system_oauth2_client"."refresh_token_validity_seconds" IS '刷新令牌的有效期';
COMMENT ON COLUMN "system_oauth2_client"."redirect_uris" IS '可重定向的 URI 地址';
COMMENT ON COLUMN "system_oauth2_client"."authorized_grant_types" IS '授权类型';
COMMENT ON COLUMN "system_oauth2_client"."scopes" IS '授权范围';
COMMENT ON COLUMN "system_oauth2_client"."auto_approve_scopes" IS '自动通过的授权范围';
COMMENT ON COLUMN "system_oauth2_client"."authorities" IS '权限';
COMMENT ON COLUMN "system_oauth2_client"."resource_ids" IS '资源';
COMMENT ON COLUMN "system_oauth2_client"."additional_information" IS '附加信息';
COMMENT ON COLUMN "system_oauth2_client"."creator" IS '创建者';
COMMENT ON COLUMN "system_oauth2_client"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_oauth2_client"."updater" IS '更新者';
COMMENT ON COLUMN "system_oauth2_client"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_oauth2_client"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "etl_schedule_job" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "workflow_id" BIGINT NOT NULL,
    "job_id" varchar(16),
    "job_status" varchar(16) NOT NULL,
    "last_job_time" timestamp,
    "last_success_time" timestamp,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_schedule_job"."id" IS '主键Id';
COMMENT ON COLUMN "etl_schedule_job"."application_id" IS '应用Id';
COMMENT ON COLUMN "etl_schedule_job"."workflow_id" IS '工作流Id';
COMMENT ON COLUMN "etl_schedule_job"."job_id" IS '工作流调度编码';
COMMENT ON COLUMN "etl_schedule_job"."job_status" IS '状态';
COMMENT ON COLUMN "etl_schedule_job"."last_job_time" IS '最近一次执行时间';
COMMENT ON COLUMN "etl_schedule_job"."last_success_time" IS '最近一次成功执行时间';
COMMENT ON COLUMN "etl_schedule_job"."deleted" IS '是否删除（逻辑删除）';
COMMENT ON COLUMN "etl_schedule_job"."creator" IS '创建人';
COMMENT ON COLUMN "etl_schedule_job"."create_time" IS '创建时间';
COMMENT ON COLUMN "etl_schedule_job"."updater" IS '更新人';
COMMENT ON COLUMN "etl_schedule_job"."update_time" IS '更新时间';
COMMENT ON COLUMN "etl_schedule_job"."lock_version" IS '乐观锁版本';
COMMENT ON COLUMN "etl_schedule_job"."tenant_id" IS '租户ID';

-- Table Definition
CREATE TABLE "system_oauth2_code" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "user_type" SMALLINT NOT NULL,
    "code" varchar(32) NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "scopes" varchar(255) DEFAULT '',
    "expires_time" timestamp NOT NULL,
    "redirect_uri" varchar(255) DEFAULT NULL,
    "state" varchar(255) NOT NULL DEFAULT '',
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 1,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_oauth2_code"."id" IS '编号';
COMMENT ON COLUMN "system_oauth2_code"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_oauth2_code"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_oauth2_code"."code" IS '授权码';
COMMENT ON COLUMN "system_oauth2_code"."client_id" IS '客户端编号';
COMMENT ON COLUMN "system_oauth2_code"."scopes" IS '授权范围';
COMMENT ON COLUMN "system_oauth2_code"."expires_time" IS '过期时间';
COMMENT ON COLUMN "system_oauth2_code"."redirect_uri" IS '可重定向的 URI 地址';
COMMENT ON COLUMN "system_oauth2_code"."state" IS '状态';
COMMENT ON COLUMN "system_oauth2_code"."creator" IS '创建者';
COMMENT ON COLUMN "system_oauth2_code"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_oauth2_code"."updater" IS '更新者';
COMMENT ON COLUMN "system_oauth2_code"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_oauth2_code"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_oauth2_code"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_oauth2_refresh_token" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "refresh_token" varchar(32) NOT NULL,
    "user_type" SMALLINT NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "scopes" varchar(255) DEFAULT NULL,
    "expires_time" timestamp NOT NULL,
    "creator" BIGINT,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_oauth2_refresh_token"."id" IS '编号';
COMMENT ON COLUMN "system_oauth2_refresh_token"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_oauth2_refresh_token"."refresh_token" IS '刷新令牌';
COMMENT ON COLUMN "system_oauth2_refresh_token"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_oauth2_refresh_token"."client_id" IS '客户端编号';
COMMENT ON COLUMN "system_oauth2_refresh_token"."scopes" IS '授权范围';
COMMENT ON COLUMN "system_oauth2_refresh_token"."expires_time" IS '过期时间';
COMMENT ON COLUMN "system_oauth2_refresh_token"."creator" IS '创建者';
COMMENT ON COLUMN "system_oauth2_refresh_token"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_oauth2_refresh_token"."updater" IS '更新者';
COMMENT ON COLUMN "system_oauth2_refresh_token"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_oauth2_refresh_token"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_oauth2_refresh_token"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_operate_log" (
    "id" BIGINT NOT NULL,
    "trace_id" varchar(64) NOT NULL DEFAULT '',
    "user_id" BIGINT NOT NULL,
    "user_type" SMALLINT NOT NULL DEFAULT 0,
    "type" varchar(50) NOT NULL,
    "sub_type" varchar(50) NOT NULL,
    "biz_id" BIGINT NOT NULL,
    "action" varchar(2000) NOT NULL DEFAULT '',
    "extra" varchar(2000) DEFAULT '',
    "request_method" varchar(16) DEFAULT '',
    "request_url" varchar(255) DEFAULT '',
    "user_ip" varchar(50) DEFAULT NULL,
    "user_agent" varchar(200) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_operate_log"."id" IS '日志主键';
COMMENT ON COLUMN "system_operate_log"."trace_id" IS '链路追踪编号';
COMMENT ON COLUMN "system_operate_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_operate_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_operate_log"."type" IS '操作模块类型';
COMMENT ON COLUMN "system_operate_log"."sub_type" IS '操作名';
COMMENT ON COLUMN "system_operate_log"."biz_id" IS '操作数据模块编号';
COMMENT ON COLUMN "system_operate_log"."action" IS '操作内容';
COMMENT ON COLUMN "system_operate_log"."extra" IS '拓展字段';
COMMENT ON COLUMN "system_operate_log"."request_method" IS '请求方法名';
COMMENT ON COLUMN "system_operate_log"."request_url" IS '请求地址';
COMMENT ON COLUMN "system_operate_log"."user_ip" IS '用户 IP';
COMMENT ON COLUMN "system_operate_log"."user_agent" IS '浏览器 UA';
COMMENT ON COLUMN "system_operate_log"."creator" IS '创建者';
COMMENT ON COLUMN "system_operate_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_operate_log"."updater" IS '更新者';
COMMENT ON COLUMN "system_operate_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_operate_log"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_operate_log"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_post" (
    "id" BIGINT NOT NULL,
    "code" varchar(64) NOT NULL,
    "name" varchar(50) NOT NULL,
    "sort" INTEGER NOT NULL,
    "status" SMALLINT NOT NULL,
    "remark" varchar(500) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_post"."id" IS '岗位ID';
COMMENT ON COLUMN "system_post"."code" IS '岗位编码';
COMMENT ON COLUMN "system_post"."name" IS '岗位名称';
COMMENT ON COLUMN "system_post"."sort" IS '显示顺序';
COMMENT ON COLUMN "system_post"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_post"."remark" IS '备注';
COMMENT ON COLUMN "system_post"."creator" IS '创建者';
COMMENT ON COLUMN "system_post"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_post"."updater" IS '更新者';
COMMENT ON COLUMN "system_post"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_post"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_post"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_role" (
    "id" BIGINT NOT NULL,
    "name" varchar(30) NOT NULL,
    "code" varchar(100) NOT NULL,
    "sort" INTEGER NOT NULL DEFAULT 0,
    "data_scope" SMALLINT NOT NULL DEFAULT 1,
    "data_scope_dept_ids" json,
    "status" SMALLINT NOT NULL DEFAULT 0,
    "type" SMALLINT NOT NULL DEFAULT 2,
    "remark" varchar(500) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_role"."id" IS '角色ID';
COMMENT ON COLUMN "system_role"."name" IS '角色名称';
COMMENT ON COLUMN "system_role"."code" IS '角色权限字符串';
COMMENT ON COLUMN "system_role"."sort" IS '显示顺序';
COMMENT ON COLUMN "system_role"."data_scope" IS '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）';
COMMENT ON COLUMN "system_role"."data_scope_dept_ids" IS '数据范围(指定部门数组)';
COMMENT ON COLUMN "system_role"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_role"."type" IS '角色类型(1内置，2普通)';
COMMENT ON COLUMN "system_role"."remark" IS '备注';
COMMENT ON COLUMN "system_role"."creator" IS '创建者';
COMMENT ON COLUMN "system_role"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_role"."updater" IS '更新者';
COMMENT ON COLUMN "system_role"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_role"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_role"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_sms_channel" (
    "id" BIGINT NOT NULL,
    "signature" varchar(12) NOT NULL,
    "code" varchar(63) NOT NULL,
    "status" SMALLINT NOT NULL,
    "remark" varchar(255) DEFAULT NULL,
    "api_key" varchar(128) NOT NULL,
    "api_secret" varchar(128) DEFAULT NULL,
    "callback_url" varchar(255) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_sms_channel"."id" IS '编号';
COMMENT ON COLUMN "system_sms_channel"."signature" IS '短信签名';
COMMENT ON COLUMN "system_sms_channel"."code" IS '渠道编码';
COMMENT ON COLUMN "system_sms_channel"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_sms_channel"."remark" IS '备注';
COMMENT ON COLUMN "system_sms_channel"."api_key" IS '短信 API 的账号';
COMMENT ON COLUMN "system_sms_channel"."api_secret" IS '短信 API 的秘钥';
COMMENT ON COLUMN "system_sms_channel"."callback_url" IS '短信发送回调 URL';
COMMENT ON COLUMN "system_sms_channel"."creator" IS '创建者';
COMMENT ON COLUMN "system_sms_channel"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_sms_channel"."updater" IS '更新者';
COMMENT ON COLUMN "system_sms_channel"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_sms_channel"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "system_sms_code" (
    "id" BIGINT NOT NULL,
    "mobile" varchar(16) NOT NULL,
    "code" varchar(6) NOT NULL,
    "create_ip" varchar(15) NOT NULL,
    "scene" SMALLINT NOT NULL,
    "today_index" SMALLINT NOT NULL,
    "used" SMALLINT NOT NULL,
    "used_time" timestamp,
    "used_ip" varchar(255) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_sms_code"."id" IS '编号';
COMMENT ON COLUMN "system_sms_code"."mobile" IS '手机号';
COMMENT ON COLUMN "system_sms_code"."code" IS '验证码';
COMMENT ON COLUMN "system_sms_code"."create_ip" IS '创建 IP';
COMMENT ON COLUMN "system_sms_code"."scene" IS '发送场景';
COMMENT ON COLUMN "system_sms_code"."today_index" IS '今日发送的第几条';
COMMENT ON COLUMN "system_sms_code"."used" IS '是否使用';
COMMENT ON COLUMN "system_sms_code"."used_time" IS '使用时间';
COMMENT ON COLUMN "system_sms_code"."used_ip" IS '使用 IP';
COMMENT ON COLUMN "system_sms_code"."creator" IS '创建者';
COMMENT ON COLUMN "system_sms_code"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_sms_code"."updater" IS '更新者';
COMMENT ON COLUMN "system_sms_code"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_sms_code"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_sms_code"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_sms_log" (
    "id" BIGINT NOT NULL,
    "channel_id" BIGINT NOT NULL,
    "channel_code" varchar(63) NOT NULL,
    "template_id" BIGINT NOT NULL,
    "template_code" varchar(63) NOT NULL,
    "template_type" SMALLINT NOT NULL,
    "template_content" varchar(255) NOT NULL,
    "template_params" varchar(255) NOT NULL,
    "api_template_id" varchar(63) NOT NULL,
    "mobile" varchar(16) NOT NULL,
    "user_id" BIGINT,
    "user_type" SMALLINT,
    "send_status" SMALLINT NOT NULL DEFAULT 0,
    "send_time" timestamp,
    "api_send_code" varchar(63) DEFAULT NULL,
    "api_send_msg" varchar(255) DEFAULT NULL,
    "api_request_id" varchar(255) DEFAULT NULL,
    "api_serial_no" varchar(255) DEFAULT NULL,
    "receive_status" SMALLINT NOT NULL DEFAULT 0,
    "receive_time" timestamp,
    "api_receive_code" varchar(63) DEFAULT NULL,
    "api_receive_msg" varchar(255) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_sms_log"."id" IS '编号';
COMMENT ON COLUMN "system_sms_log"."channel_id" IS '短信渠道编号';
COMMENT ON COLUMN "system_sms_log"."channel_code" IS '短信渠道编码';
COMMENT ON COLUMN "system_sms_log"."template_id" IS '模板编号';
COMMENT ON COLUMN "system_sms_log"."template_code" IS '模板编码';
COMMENT ON COLUMN "system_sms_log"."template_type" IS '短信类型';
COMMENT ON COLUMN "system_sms_log"."template_content" IS '短信内容';
COMMENT ON COLUMN "system_sms_log"."template_params" IS '短信参数';
COMMENT ON COLUMN "system_sms_log"."api_template_id" IS '短信 API 的模板编号';
COMMENT ON COLUMN "system_sms_log"."mobile" IS '手机号';
COMMENT ON COLUMN "system_sms_log"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_sms_log"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_sms_log"."send_status" IS '发送状态';
COMMENT ON COLUMN "system_sms_log"."send_time" IS '发送时间';
COMMENT ON COLUMN "system_sms_log"."api_send_code" IS '短信 API 发送结果的编码';
COMMENT ON COLUMN "system_sms_log"."api_send_msg" IS '短信 API 发送失败的提示';
COMMENT ON COLUMN "system_sms_log"."api_request_id" IS '短信 API 发送返回的唯一请求 ID';
COMMENT ON COLUMN "system_sms_log"."api_serial_no" IS '短信 API 发送返回的序号';
COMMENT ON COLUMN "system_sms_log"."receive_status" IS '接收状态';
COMMENT ON COLUMN "system_sms_log"."receive_time" IS '接收时间';
COMMENT ON COLUMN "system_sms_log"."api_receive_code" IS 'API 接收结果的编码';
COMMENT ON COLUMN "system_sms_log"."api_receive_msg" IS 'API 接收结果的说明';
COMMENT ON COLUMN "system_sms_log"."creator" IS '创建者';
COMMENT ON COLUMN "system_sms_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_sms_log"."updater" IS '更新者';
COMMENT ON COLUMN "system_sms_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_sms_log"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "system_sms_template" (
    "id" BIGINT NOT NULL,
    "type" SMALLINT NOT NULL,
    "status" SMALLINT NOT NULL,
    "code" varchar(63) NOT NULL,
    "name" varchar(63) NOT NULL,
    "content" varchar(255) NOT NULL,
    "params" varchar(255) NOT NULL,
    "remark" varchar(255) DEFAULT NULL,
    "api_template_id" varchar(63) NOT NULL,
    "channel_id" BIGINT NOT NULL,
    "channel_code" varchar(63) NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_sms_template"."id" IS '编号';
COMMENT ON COLUMN "system_sms_template"."type" IS '模板类型';
COMMENT ON COLUMN "system_sms_template"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_sms_template"."code" IS '模板编码';
COMMENT ON COLUMN "system_sms_template"."name" IS '模板名称';
COMMENT ON COLUMN "system_sms_template"."content" IS '模板内容';
COMMENT ON COLUMN "system_sms_template"."params" IS '参数数组';
COMMENT ON COLUMN "system_sms_template"."remark" IS '备注';
COMMENT ON COLUMN "system_sms_template"."api_template_id" IS '短信 API 的模板编号';
COMMENT ON COLUMN "system_sms_template"."channel_id" IS '短信渠道编号';
COMMENT ON COLUMN "system_sms_template"."channel_code" IS '短信渠道编码';
COMMENT ON COLUMN "system_sms_template"."creator" IS '创建者';
COMMENT ON COLUMN "system_sms_template"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_sms_template"."updater" IS '更新者';
COMMENT ON COLUMN "system_sms_template"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_sms_template"."deleted" IS '是否删除';

-- Table Definition
CREATE TABLE "system_social_client" (
    "id" BIGINT NOT NULL,
    "name" varchar(255) NOT NULL,
    "social_type" SMALLINT NOT NULL,
    "user_type" SMALLINT NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "client_secret" varchar(255) NOT NULL,
    "agent_id" varchar(255) DEFAULT NULL,
    "status" SMALLINT NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_social_client"."id" IS '编号';
COMMENT ON COLUMN "system_social_client"."name" IS '应用名';
COMMENT ON COLUMN "system_social_client"."social_type" IS '社交平台的类型';
COMMENT ON COLUMN "system_social_client"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_social_client"."client_id" IS '客户端编号';
COMMENT ON COLUMN "system_social_client"."client_secret" IS '客户端密钥';
COMMENT ON COLUMN "system_social_client"."agent_id" IS '代理编号';
COMMENT ON COLUMN "system_social_client"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_social_client"."creator" IS '创建者';
COMMENT ON COLUMN "system_social_client"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_social_client"."updater" IS '更新者';
COMMENT ON COLUMN "system_social_client"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_social_client"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_social_client"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "etl_workflow" (
    "id" BIGINT NOT NULL,
    "application_id" BIGINT NOT NULL,
    "workflow_name" varchar(256) NOT NULL,
    "config" text NOT NULL,
    "is_enabled" INTEGER NOT NULL DEFAULT 0,
    "schedule_strategy" varchar(16) DEFAULT NULL,
    "schedule_config" text,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "creator" BIGINT NOT NULL DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lock_version" INTEGER NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    "declaration" varchar(256),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "etl_workflow"."id" IS '主键Id';
COMMENT ON COLUMN "etl_workflow"."application_id" IS '应用Id';
COMMENT ON COLUMN "etl_workflow"."workflow_name" IS '名称';
COMMENT ON COLUMN "etl_workflow"."config" IS '配置信息';
COMMENT ON COLUMN "etl_workflow"."is_enabled" IS '启用状态,默认为关闭(0)';
COMMENT ON COLUMN "etl_workflow"."schedule_strategy" IS '调度策略';
COMMENT ON COLUMN "etl_workflow"."schedule_config" IS '调度配置';
COMMENT ON COLUMN "etl_workflow"."deleted" IS '是否删除（逻辑删除）';
COMMENT ON COLUMN "etl_workflow"."creator" IS '创建人';
COMMENT ON COLUMN "etl_workflow"."create_time" IS '创建时间';
COMMENT ON COLUMN "etl_workflow"."updater" IS '更新人';
COMMENT ON COLUMN "etl_workflow"."update_time" IS '更新时间';
COMMENT ON COLUMN "etl_workflow"."lock_version" IS '乐观锁版本';
COMMENT ON COLUMN "etl_workflow"."tenant_id" IS '租户ID';
COMMENT ON COLUMN "etl_workflow"."declaration" IS 'ETL描述';

-- Table Definition
CREATE TABLE "system_users" (
    "id" BIGINT NOT NULL,
    "username" varchar(64) NOT NULL,
    "password" varchar(128) NOT NULL DEFAULT '',
    "nickname" varchar(64) NOT NULL,
    "remark" varchar(512) DEFAULT NULL,
    "dept_id" BIGINT,
    "post_ids" json,
    "email" varchar(64) DEFAULT '',
    "mobile" varchar(16) DEFAULT '',
    "sex" SMALLINT DEFAULT 0,
    "avatar" varchar(512) DEFAULT '',
    "status" SMALLINT NOT NULL DEFAULT 0,
    "login_ip" varchar(64) DEFAULT '',
    "login_date" timestamp,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL,
    "user_type" SMALLINT,
    "admin_type" SMALLINT,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_users"."id" IS '用户ID';
COMMENT ON COLUMN "system_users"."username" IS '用户账号';
COMMENT ON COLUMN "system_users"."password" IS '密码';
COMMENT ON COLUMN "system_users"."nickname" IS '用户昵称';
COMMENT ON COLUMN "system_users"."remark" IS '备注';
COMMENT ON COLUMN "system_users"."dept_id" IS '部门ID';
COMMENT ON COLUMN "system_users"."post_ids" IS '岗位编号数组';
COMMENT ON COLUMN "system_users"."email" IS '用户邮箱';
COMMENT ON COLUMN "system_users"."mobile" IS '手机号码';
COMMENT ON COLUMN "system_users"."sex" IS '用户性别';
COMMENT ON COLUMN "system_users"."avatar" IS '头像地址';
COMMENT ON COLUMN "system_users"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_users"."login_ip" IS '最后登录IP';
COMMENT ON COLUMN "system_users"."login_date" IS '最后登录时间';
COMMENT ON COLUMN "system_users"."creator" IS '创建者';
COMMENT ON COLUMN "system_users"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_users"."updater" IS '更新者';
COMMENT ON COLUMN "system_users"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_users"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_users"."tenant_id" IS '租户编号';
COMMENT ON COLUMN "system_users"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_users"."admin_type" IS '管理员类型';

-- Table Definition
CREATE TABLE "app_resource_pageset" (
    "pageset_code" varchar(255) NOT NULL,
    "menu_id" BIGINT NOT NULL,
    "main_metadata" varchar(255) NOT NULL,
    "pageset_name" varchar(255) NOT NULL,
    "display_name" varchar(255) NOT NULL,
    "description" text,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT,
    "pageset_type" INTEGER NOT NULL DEFAULT 1
);

-- Table Definition
CREATE TABLE "app_resource_page" (
    "pageset_id" BIGINT NOT NULL,
    "page_name" varchar(255) NOT NULL,
    "page_type" varchar(64) NOT NULL,
    "title" varchar(255) NOT NULL,
    "layout" varchar(255) NOT NULL,
    "width" varchar(255) NOT NULL,
    "margin" varchar(255) NOT NULL,
    "background_color" varchar(255) NOT NULL,
    "main_metadata" varchar(255) NOT NULL,
    "router_path" varchar(255) NOT NULL,
    "router_name" varchar(255) NOT NULL,
    "router_meta_title" varchar(255) NOT NULL,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT,
    "edit_view_mode" SMALLINT NOT NULL DEFAULT 0,
    "detail_view_mode" SMALLINT NOT NULL DEFAULT 0,
    "is_default_edit_view_mode" SMALLINT NOT NULL DEFAULT 0,
    "is_default_detail_view_mode" SMALLINT NOT NULL DEFAULT 0,
    "is_latest_updated" SMALLINT NOT NULL DEFAULT 0
);

-- Column Comment
COMMENT ON COLUMN "app_resource_page"."edit_view_mode" IS '编辑模式';
COMMENT ON COLUMN "app_resource_page"."detail_view_mode" IS '详情模式';
COMMENT ON COLUMN "app_resource_page"."is_default_edit_view_mode" IS '是否默认编辑视图';
COMMENT ON COLUMN "app_resource_page"."is_default_detail_view_mode" IS '是否默认详情视图';
COMMENT ON COLUMN "app_resource_page"."is_latest_updated" IS '最新更新的视图';

-- Table Definition
CREATE TABLE "system_social_user" (
    "id" BIGINT NOT NULL,
    "type" SMALLINT NOT NULL,
    "openid" varchar(32) NOT NULL,
    "token" varchar(256) DEFAULT NULL,
    "raw_token_info" varchar(1024) NOT NULL,
    "nickname" varchar(32) NOT NULL,
    "avatar" varchar(255) DEFAULT NULL,
    "raw_user_info" varchar(1024) NOT NULL,
    "code" varchar(256) NOT NULL,
    "state" varchar(256) DEFAULT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_social_user"."id" IS '主键(自增策略)';
COMMENT ON COLUMN "system_social_user"."type" IS '社交平台的类型';
COMMENT ON COLUMN "system_social_user"."openid" IS '社交 openid';
COMMENT ON COLUMN "system_social_user"."token" IS '社交 token';
COMMENT ON COLUMN "system_social_user"."raw_token_info" IS '原始 Token 数据，一般是 JSON 格式';
COMMENT ON COLUMN "system_social_user"."nickname" IS '用户昵称';
COMMENT ON COLUMN "system_social_user"."avatar" IS '用户头像';
COMMENT ON COLUMN "system_social_user"."raw_user_info" IS '原始用户数据，一般是 JSON 格式';
COMMENT ON COLUMN "system_social_user"."code" IS '最后一次的认证 code';
COMMENT ON COLUMN "system_social_user"."state" IS '最后一次的认证 state';
COMMENT ON COLUMN "system_social_user"."creator" IS '创建者';
COMMENT ON COLUMN "system_social_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_social_user"."updater" IS '更新者';
COMMENT ON COLUMN "system_social_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_social_user"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_social_user"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "system_social_user_bind" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "user_type" SMALLINT NOT NULL,
    "social_type" SMALLINT NOT NULL,
    "social_user_id" BIGINT NOT NULL,
    "creator" BIGINT DEFAULT 0,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT DEFAULT 0,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_social_user_bind"."id" IS '主键(自增策略)';
COMMENT ON COLUMN "system_social_user_bind"."user_id" IS '用户编号';
COMMENT ON COLUMN "system_social_user_bind"."user_type" IS '用户类型';
COMMENT ON COLUMN "system_social_user_bind"."social_type" IS '社交平台的类型';
COMMENT ON COLUMN "system_social_user_bind"."social_user_id" IS '社交用户的编号';
COMMENT ON COLUMN "system_social_user_bind"."creator" IS '创建者';
COMMENT ON COLUMN "system_social_user_bind"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_social_user_bind"."updater" IS '更新者';
COMMENT ON COLUMN "system_social_user_bind"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_social_user_bind"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_social_user_bind"."tenant_id" IS '租户编号';

-- Table Definition
CREATE TABLE "app_resource_pageset_page" (
    "pageset_id" BIGINT NOT NULL,
    "page_id" BIGINT NOT NULL,
    "page_type" varchar(64) NOT NULL,
    "default_seq" INTEGER NOT NULL,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT,
    "is_default" SMALLINT NOT NULL DEFAULT 1
);

-- Table Definition
CREATE TABLE "system_tenant" (
    "id" BIGINT NOT NULL,
    "name" varchar(64) NOT NULL,
    "admin_user_id" BIGINT,
    "status" SMALLINT NOT NULL DEFAULT 0,
    "website" varchar(256) DEFAULT '',
    "package_id" BIGINT NOT NULL,
    "expire_time" timestamp NOT NULL,
    "account_count" INTEGER NOT NULL,
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT NOT NULL,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "tenant_code" varchar(64),
    "website_h5" varchar(256),
    "tenant_key" varchar(256),
    "tenant_secret" varchar(256),
    "access_url" varchar(256),
    "saas_enabled" INTEGER,
    "logo_url" varchar,
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_tenant"."id" IS '租户编号';
COMMENT ON COLUMN "system_tenant"."name" IS '租户名';
COMMENT ON COLUMN "system_tenant"."admin_user_id" IS '联系人的用户编号';
COMMENT ON COLUMN "system_tenant"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_tenant"."website" IS '绑定域名';
COMMENT ON COLUMN "system_tenant"."package_id" IS '租户套餐编号';
COMMENT ON COLUMN "system_tenant"."expire_time" IS '过期时间';
COMMENT ON COLUMN "system_tenant"."account_count" IS '账号数量/人员数量';
COMMENT ON COLUMN "system_tenant"."creator" IS '创建者';
COMMENT ON COLUMN "system_tenant"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_tenant"."updater" IS '更新者';
COMMENT ON COLUMN "system_tenant"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_tenant"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_tenant"."tenant_code" IS '租户编码';
COMMENT ON COLUMN "system_tenant"."website_h5" IS '移动端访问地址';
COMMENT ON COLUMN "system_tenant"."tenant_key" IS '租户key';
COMMENT ON COLUMN "system_tenant"."tenant_secret" IS '租住secret';
COMMENT ON COLUMN "system_tenant"."access_url" IS '访问地址';
COMMENT ON COLUMN "system_tenant"."saas_enabled" IS 'saas功能是否开启默认0，开启1';
COMMENT ON COLUMN "system_tenant"."logo_url" IS '用户logo';

-- Table Definition
CREATE TABLE "app_resource_page_ref_router" (
    "page_id" BIGINT NOT NULL,
    "router_name" varchar(255) NOT NULL,
    "router_type" varchar(255) NOT NULL,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT
);

-- Table Definition
CREATE TABLE "app_resource_component" (
    "component_code" varchar(255) NOT NULL,
    "page_id" BIGINT NOT NULL,
    "component_type" varchar(64) NOT NULL,
    "config" text NOT NULL,
    "edit_data" text NOT NULL,
    "parent_code" varchar(255),
    "block_index" BIGINT NOT NULL DEFAULT 0,
    "container_index" BIGINT NOT NULL DEFAULT 0,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT,
    "component_index" BIGINT NOT NULL DEFAULT 0
);

-- Column Comment
COMMENT ON COLUMN "app_resource_component"."component_index" IS '组件索引';

-- Table Definition
CREATE TABLE "system_tenant_package" (
    "id" BIGINT NOT NULL,
    "name" varchar(30) NOT NULL,
    "status" SMALLINT NOT NULL DEFAULT 0,
    "remark" varchar(256) DEFAULT '',
    "creator" BIGINT NOT NULL,
    "create_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" BIGINT,
    "update_time" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "menu_ids" json,
    "code" varchar(100),
    PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "system_tenant_package"."id" IS '套餐编号';
COMMENT ON COLUMN "system_tenant_package"."name" IS '套餐名';
COMMENT ON COLUMN "system_tenant_package"."status" IS '状态（0停用，1启用）';
COMMENT ON COLUMN "system_tenant_package"."remark" IS '备注';
COMMENT ON COLUMN "system_tenant_package"."creator" IS '创建者';
COMMENT ON COLUMN "system_tenant_package"."create_time" IS '创建时间';
COMMENT ON COLUMN "system_tenant_package"."updater" IS '更新者';
COMMENT ON COLUMN "system_tenant_package"."update_time" IS '更新时间';
COMMENT ON COLUMN "system_tenant_package"."deleted" IS '是否删除';
COMMENT ON COLUMN "system_tenant_package"."menu_ids" IS '关联的菜单IDS';
COMMENT ON COLUMN "system_tenant_package"."code" IS '租户套餐编码';

-- Table Definition
CREATE TABLE "app_resource_page_metadata" (
    "page_id" BIGINT NOT NULL,
    "metadata" varchar(255) NOT NULL,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT
);

-- Table Definition
CREATE TABLE "app_resource_pageset_label" (
    "pageset_id" BIGINT NOT NULL,
    "label_name" varchar(255) NOT NULL,
    "label_value" varchar(255) NOT NULL,
    "tenant_id" BIGINT,
    "id" BIGINT,
    "create_time" timestamp,
    "update_time" timestamp,
    "creator" BIGINT,
    "updater" BIGINT,
    "deleted" BIGINT NOT NULL DEFAULT 0,
    "lock_version" BIGINT
);



-- Comments
COMMENT ON TABLE "app_application_tag" IS '应用标签管理表';


-- Indices
CREATE UNIQUE INDEX uk_app_application_tag ON "app_application_tag" ("application_id", "tag_id", "deleted");
CREATE UNIQUE INDEX pk_app_application_tag ON "app_application_tag" ("id");


-- Comments
COMMENT ON TABLE "app_auth_field" IS '应用权限-字段权限';


-- Indices
CREATE UNIQUE INDEX uk_app_auth_field ON "app_auth_field" ("application_id", "role_id", "menu_id", "field_id", "deleted");
CREATE UNIQUE INDEX pk_app_auth_field ON "app_auth_field" ("id");


-- Comments
COMMENT ON TABLE "metadata_datasource" IS '数据源表';


-- Indices
CREATE UNIQUE INDEX uk_datasource_code ON "metadata_datasource" ("code", "app_id", "tenant_id");
CREATE INDEX idx_datasource_tenant_app ON "metadata_datasource" ("app_id", "tenant_id", "deleted");
CREATE INDEX idx_datasource_type ON "metadata_datasource" ("datasource_type", "deleted");


-- Indices
CREATE INDEX idx_config_tenant ON "infra_security_config" ("tenant_id", "deleted");
CREATE INDEX idx_config_template ON "infra_security_config" ("tenant_id", "config_key", "deleted");
CREATE UNIQUE INDEX infra_security_config_tenant_id_config_key_deleted_key ON "infra_security_config" ("tenant_id", "config_key", "deleted");


-- Comments
COMMENT ON TABLE "app_auth_role" IS '应用角色';


-- Indices
CREATE UNIQUE INDEX uk_app_auth_role ON "app_auth_role" ("application_id", "role_code", "deleted");
CREATE UNIQUE INDEX pk_app_auth_role ON "app_auth_role" ("id");


-- Comments
COMMENT ON TABLE "app_auth_role_user" IS '应用角色关联表';


-- Indices
CREATE UNIQUE INDEX uk_app_auth_role_user ON "app_auth_role_user" ("user_id", "role_id", "deleted");
CREATE UNIQUE INDEX pk_app_auth_role_user ON "app_auth_role_user" ("id");


-- Comments
COMMENT ON TABLE "app_menu" IS '应用菜单表';


-- Indices
CREATE UNIQUE INDEX pk_app_menu ON "app_menu" ("id");


-- Indices
CREATE UNIQUE INDEX pk_app_application_version_resource ON "app_version_resource" ("id");


-- Indices
CREATE INDEX idx_category_code ON "infra_security_config_category" ("category_code", "deleted");
CREATE UNIQUE INDEX infra_security_config_category_category_code_key ON "infra_security_config_category" ("category_code");


-- Comments
COMMENT ON TABLE "app_application" IS '应用管理表';


-- Indices
CREATE UNIQUE INDEX uk_app_application_uid ON "app_application" ("app_uid", "deleted");
CREATE UNIQUE INDEX pk_app_application ON "app_application" ("id");


-- Comments
COMMENT ON TABLE "metadata_entity_relationship" IS '实体关系表';


-- Indices
CREATE INDEX idx_relationship_source ON "metadata_entity_relationship" ("source_entity_id", "deleted");
CREATE INDEX idx_relationship_target ON "metadata_entity_relationship" ("target_entity_id", "deleted");
CREATE INDEX idx_relationship_tenant_app ON "metadata_entity_relationship" ("app_id", "tenant_id", "deleted");


-- Indices
CREATE INDEX idx_template_category ON "infra_security_config_template" ("category_id", "deleted");
CREATE INDEX idx_template_key ON "infra_security_config_template" ("config_key", "deleted");
CREATE UNIQUE INDEX infra_security_config_template_config_key_key ON "infra_security_config_template" ("config_key");


-- Indices
CREATE UNIQUE INDEX uk_flink_mapping ON "etl_flink_mapping" ("datasource_type", "origin_type", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_flink_mapping ON "etl_flink_mapping" ("id");


-- Comments
COMMENT ON TABLE "metadata_app_and_datasource" IS '应用与数据源关联表';


-- Indices
CREATE INDEX idx_metadata_app_and_datasource_app_uid ON "metadata_app_and_datasource" ("app_uid");
CREATE INDEX idx_metadata_app_and_datasource_type ON "metadata_app_and_datasource" ("datasource_type");
CREATE INDEX idx_metadata_app_and_datasource_tenant_id ON "metadata_app_and_datasource" ("tenant_id");
CREATE INDEX idx_metadata_app_and_datasource_datasource_id ON "metadata_app_and_datasource" ("datasource_id");
CREATE INDEX idx_metadata_app_and_datasource_app_id ON "metadata_app_and_datasource" ("application_id");
CREATE UNIQUE INDEX uk_metadata_app_and_datasource ON "metadata_app_and_datasource" ("application_id", "datasource_id", "tenant_id");
CREATE UNIQUE INDEX pk_metadata_app_and_datasource ON "metadata_app_and_datasource" ("id");
ALTER TABLE "metadata_validation_rule_definition" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "metadata_validation_rule_definition" ADD FOREIGN KEY ("parent_rule_id") REFERENCES "metadata_validation_rule_definition"("id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "metadata_validation_rule_definition" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "metadata_validation_rule_definition" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id") ON DELETE SET NULL ON UPDATE CASCADE;


-- Comments
COMMENT ON TABLE "metadata_validation_rule_definition" IS '规则定义表';


-- Indices
CREATE INDEX idx_metadata_validation_rule_definition_tenant ON "metadata_validation_rule_definition" ("tenant_id");
CREATE INDEX idx_metadata_validation_rule_definition_logic_type ON "metadata_validation_rule_definition" ("logic_type");
CREATE INDEX idx_metadata_validation_rule_definition_field_id ON "metadata_validation_rule_definition" ("field_id");
CREATE INDEX idx_metadata_validation_rule_definition_entity_id ON "metadata_validation_rule_definition" ("entity_id");
CREATE INDEX idx_metadata_validation_rule_definition_parent_rule_id ON "metadata_validation_rule_definition" ("parent_rule_id");
CREATE INDEX idx_metadata_validation_rule_definition_group_id ON "metadata_validation_rule_definition" ("group_id");


-- Comments
COMMENT ON TABLE "metadata_entity_field_option" IS '实体字段-选项值（用于单选/多选）';


-- Indices
CREATE UNIQUE INDEX pk_metadata_entity_field_option ON "metadata_entity_field_option" ("id");
CREATE UNIQUE INDEX uk_field_option_value ON "metadata_entity_field_option" ("field_id", "option_value");
CREATE INDEX idx_field_option_field ON "metadata_entity_field_option" ("field_id", "deleted");


-- Comments
COMMENT ON TABLE "metadata_entity_field_constraint" IS '实体字段-约束定义（长度范围/正则校验）';


-- Indices
CREATE UNIQUE INDEX pk_metadata_entity_field_constraint ON "metadata_entity_field_constraint" ("id");
CREATE UNIQUE INDEX uk_field_constraint_type ON "metadata_entity_field_constraint" ("field_id", "constraint_type");
CREATE INDEX idx_field_constraint_field ON "metadata_entity_field_constraint" ("field_id", "deleted");


-- Comments
COMMENT ON TABLE "system_corp" IS '企业基本信息表';


-- Indices
CREATE UNIQUE INDEX system_enterprise_pkey ON "system_corp" ("id");


-- Comments
COMMENT ON TABLE "system_corp_app_relation" IS '企业应用关联表';


-- Indices
CREATE UNIQUE INDEX uk_flow_process_time_unique ON "flow_process_time" ("process_id", "deleted");
CREATE UNIQUE INDEX pk_flow_process_time ON "flow_process_time" ("id");


-- Indices
CREATE UNIQUE INDEX pk_flow_process_date_field ON "flow_process_date_field" ("id");


-- Comments
COMMENT ON TABLE "dual" IS '数据库连接的表';


-- Comments
COMMENT ON TABLE "metadata_auto_number_rule_item" IS '自动编号-规则项定义';


-- Indices
CREATE UNIQUE INDEX pk_metadata_auto_number_rule_item ON "metadata_auto_number_rule_item" ("id");
CREATE INDEX idx_auto_number_rule_config ON "metadata_auto_number_rule_item" ("config_id", "deleted");
CREATE UNIQUE INDEX uk_auto_number_rule_order ON "metadata_auto_number_rule_item" ("config_id", "item_order");


-- Comments
COMMENT ON TABLE "metadata_auto_number_state" IS '自动编号-周期计数状态';


-- Indices
CREATE UNIQUE INDEX pk_metadata_auto_number_state ON "metadata_auto_number_state" ("id");
CREATE INDEX idx_auto_number_state_config ON "metadata_auto_number_state" ("config_id", "deleted");
CREATE UNIQUE INDEX uk_auto_number_state_period ON "metadata_auto_number_state" ("config_id", "period_key");


-- Comments
COMMENT ON TABLE "metadata_auto_number_reset_log" IS '自动编号-手动重置日志';


-- Indices
CREATE UNIQUE INDEX pk_metadata_auto_number_reset_log ON "metadata_auto_number_reset_log" ("id");
CREATE INDEX idx_auto_number_reset_config ON "metadata_auto_number_reset_log" ("config_id", "period_key", "deleted");


-- Comments
COMMENT ON TABLE "metadata_auto_number_config" IS '自动编号-字段配置';


-- Indices
CREATE UNIQUE INDEX pk_metadata_auto_number_config ON "metadata_auto_number_config" ("id");
CREATE INDEX idx_auto_number_field ON "metadata_auto_number_config" ("field_id", "deleted");
CREATE UNIQUE INDEX uk_auto_number_field ON "metadata_auto_number_config" ("field_id");


-- Comments
COMMENT ON TABLE "app_auth_role_dept" IS '角色部门表';


-- Indices
CREATE UNIQUE INDEX uk_app_auth_role_dept ON "app_auth_role_dept" ("dept_id", "role_id", "deleted");
CREATE UNIQUE INDEX pk_app_auth_role_dept ON "app_auth_role_dept" ("id");


-- Comments
COMMENT ON TABLE "system_role_menu" IS '角色和菜单关联表';


-- Indices
CREATE UNIQUE INDEX uni_role_menu_deleted ON "system_role_menu" ("role_id", "menu_id", "deleted");
CREATE UNIQUE INDEX pk_system_role_menu ON "system_role_menu" ("id");


-- Indices
CREATE UNIQUE INDEX pk_app_application_version ON "app_version" ("id");


-- Comments
COMMENT ON TABLE "app_auth_view" IS '应用功能权限-视图权限';


-- Indices
CREATE UNIQUE INDEX uk_app_auth_entity ON "app_auth_view" ("application_id", "role_id", "menu_id", "view_id", "deleted");
CREATE UNIQUE INDEX pk_app_auth_entity ON "app_auth_view" ("id");


-- Comments
COMMENT ON TABLE "system_user_post" IS '用户岗位表';


-- Indices
CREATE UNIQUE INDEX pk_system_user_post ON "system_user_post" ("id");


-- Comments
COMMENT ON TABLE "system_user_role" IS '用户和角色关联表';


-- Indices
CREATE UNIQUE INDEX uni_user_role_deleted ON "system_user_role" ("user_id", "role_id", "deleted");
CREATE UNIQUE INDEX pk_system_user_role ON "system_user_role" ("id");


-- Indices
CREATE UNIQUE INDEX flow_execution_log_pk ON "flow_execution_log" ("id");


-- Comments
COMMENT ON TABLE "system_license" IS '平台License信息';


-- Indices
CREATE INDEX idx_system_license_enterprise_name ON "system_license" ("enterprise_name");
CREATE INDEX idx_system_license_expire_time ON "system_license" ("expire_time");


-- Comments
COMMENT ON TABLE "bpm_flow_instance_biz_ext" IS '流程实例扩展信息表';


-- Indices
CREATE UNIQUE INDEX uk_bpm_flow_instance_biz_ext_instance_id ON "bpm_flow_instance_biz_ext" ("instance_id");
CREATE INDEX idx_bpm_flow_instance_biz_ext_deleted ON "bpm_flow_instance_biz_ext" ("deleted");
CREATE INDEX idx_bpm_flow_instance_biz_ext_tenant_id ON "bpm_flow_instance_biz_ext" ("tenant_id");
CREATE INDEX idx_bpm_flow_instance_biz_ext_business_id ON "bpm_flow_instance_biz_ext" ("business_id");
CREATE INDEX idx_bpm_flow_instance_biz_ext_instance_id ON "bpm_flow_instance_biz_ext" ("instance_id");


-- Comments
COMMENT ON TABLE "bpm_flow_definition" IS '流程定义表';


-- Comments
COMMENT ON TABLE "metadata_data_system_method" IS '系统数据方法表';


-- Indices
CREATE UNIQUE INDEX uk_metadata_data_system_method_code ON "metadata_data_system_method" ("method_code");
CREATE INDEX idx_metadata_data_system_method_create_time ON "metadata_data_system_method" ("create_time");
CREATE INDEX idx_metadata_data_system_method_deleted ON "metadata_data_system_method" ("deleted");
CREATE INDEX idx_metadata_data_system_method_is_enabled ON "metadata_data_system_method" ("is_enabled");
CREATE INDEX idx_metadata_data_system_method_method_type ON "metadata_data_system_method" ("method_type");
CREATE INDEX idx_metadata_data_system_method_method_code ON "metadata_data_system_method" ("method_code");


-- Comments
COMMENT ON TABLE "bpm_flow_node" IS '流程节点表';


-- Comments
COMMENT ON TABLE "bpm_flow_skip" IS '节点跳转关联表';


-- Comments
COMMENT ON TABLE "bpm_flow_instance" IS '流程实例表';


-- Comments
COMMENT ON TABLE "bpm_flow_task" IS '待办任务表';


-- Comments
COMMENT ON TABLE "bpm_flow_his_task" IS '历史任务记录表';


-- Comments
COMMENT ON TABLE "bpm_flow_user" IS '流程用户表';


-- Indices
CREATE INDEX bpm_user_associated_idx ON "bpm_flow_user" ("associated");
CREATE INDEX bpm_user_processed_type ON "bpm_flow_user" ("processed_by", "type");
CREATE UNIQUE INDEX bpm_flow_user_pk ON "bpm_flow_user" ("id");


-- Comments
COMMENT ON TABLE "app_auth_permission" IS '应用权限-基础总表';


-- Indices
CREATE UNIQUE INDEX uk_app_auth_permission ON "app_auth_permission" ("application_id", "role_id", "menu_id", "deleted");
CREATE UNIQUE INDEX pk_app_auth_permission ON "app_auth_permission" ("id");


-- Comments
COMMENT ON TABLE "metadata_component_field_type" IS '元数据组件字段类型表';


-- Indices
CREATE INDEX idx_field_type_status ON "metadata_component_field_type" ("status");
CREATE INDEX idx_field_type_code ON "metadata_component_field_type" ("field_type_code");
CREATE UNIQUE INDEX metadata_component_field_type_field_type_code_key ON "metadata_component_field_type" ("field_type_code");


-- Comments
COMMENT ON TABLE "metadata_validation_type" IS '元数据校验类型表';


-- Indices
CREATE INDEX idx_validation_type_status ON "metadata_validation_type" ("status");
CREATE INDEX idx_validation_type_code ON "metadata_validation_type" ("validation_code");
CREATE UNIQUE INDEX metadata_validation_type_validation_code_key ON "metadata_validation_type" ("validation_code");


-- Comments
COMMENT ON TABLE "app_auth_data_group" IS '数据权限-权限组配置表';


-- Indices
CREATE UNIQUE INDEX pk_app_auth_data_group ON "app_auth_data_group" ("id");


-- Comments
COMMENT ON TABLE "system_mail_account" IS '邮箱账号表';


-- Indices
CREATE UNIQUE INDEX pk_system_mail_account ON "system_mail_account" ("id");


-- Indices
CREATE UNIQUE INDEX uk_flow_process_form_unique ON "flow_process_form" ("process_id", "deleted");
CREATE UNIQUE INDEX pk_flow_process_form ON "flow_process_form" ("id");


-- Indices
CREATE UNIQUE INDEX uk_flow_process_entity_unique ON "flow_process_entity" ("process_id", "deleted");
CREATE UNIQUE INDEX pk_flow_process_entity ON "flow_process_entity" ("id");


-- Comments
COMMENT ON TABLE "metadata_system_fields" IS '元数据系统字段维护表';
ALTER TABLE "metadata_validation_required" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE;
ALTER TABLE "metadata_validation_required" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id");
ALTER TABLE "metadata_validation_required" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id");


-- Comments
COMMENT ON TABLE "metadata_validation_required" IS '字段校验-必填规则';


-- Indices
CREATE UNIQUE INDEX uq_mvr_active ON "metadata_validation_required" ("tenant_id", "entity_id", "field_id");
CREATE INDEX idx_mvr_field ON "metadata_validation_required" ("entity_id", "field_id");
CREATE INDEX idx_mvr_group ON "metadata_validation_required" ("group_id");
ALTER TABLE "metadata_validation_unique" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE;
ALTER TABLE "metadata_validation_unique" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id");
ALTER TABLE "metadata_validation_unique" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id");


-- Comments
COMMENT ON TABLE "metadata_validation_unique" IS '字段校验-唯一规则';


-- Indices
CREATE UNIQUE INDEX uq_mvu_active ON "metadata_validation_unique" ("tenant_id", "entity_id", "field_id");
CREATE INDEX idx_mvu_field ON "metadata_validation_unique" ("entity_id", "field_id");
CREATE INDEX idx_mvu_group ON "metadata_validation_unique" ("group_id");


-- Indices
CREATE UNIQUE INDEX app_tag_pk ON "app_tag" ("id");


-- Comments
COMMENT ON TABLE "metadata_permit_ref_otft" IS '数据权限-操作符号与字段类型的关联表';
ALTER TABLE "metadata_validation_length" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id");
ALTER TABLE "metadata_validation_length" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE;
ALTER TABLE "metadata_validation_length" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id");


-- Comments
COMMENT ON TABLE "metadata_validation_length" IS '字段校验-长度规则';


-- Indices
CREATE UNIQUE INDEX uq_mvl_active ON "metadata_validation_length" ("tenant_id", "entity_id", "field_id");
CREATE INDEX idx_mvl_field ON "metadata_validation_length" ("entity_id", "field_id");
CREATE INDEX idx_mvl_group ON "metadata_validation_length" ("group_id");


-- Indices
CREATE UNIQUE INDEX pk_flow_process ON "flow_process" ("id");


-- Comments
COMMENT ON TABLE "system_menu" IS '菜单权限表';


-- Indices
CREATE UNIQUE INDEX uni_permission_code ON "system_menu" ("permission");
CREATE UNIQUE INDEX pk_system_menu ON "system_menu" ("id");


-- Comments
COMMENT ON TABLE "infra_api_access_log" IS 'API 访问日志表';


-- Indices
CREATE INDEX idx_infra_api_access_log_01 ON "infra_api_access_log" ("create_time");
CREATE UNIQUE INDEX pk_infra_api_access_log ON "infra_api_access_log" ("id");


-- Comments
COMMENT ON TABLE "infra_api_error_log" IS '系统异常日志';


-- Indices
CREATE UNIQUE INDEX pk_infra_api_error_log ON "infra_api_error_log" ("id");
ALTER TABLE "metadata_validation_range" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id");
ALTER TABLE "metadata_validation_range" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE;
ALTER TABLE "metadata_validation_range" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id");


-- Comments
COMMENT ON TABLE "metadata_validation_range" IS '字段校验-范围规则（数值/日期）';


-- Indices
CREATE UNIQUE INDEX uq_mvrg_active ON "metadata_validation_range" ("tenant_id", "entity_id", "field_id");
CREATE INDEX idx_mvrg_field ON "metadata_validation_range" ("entity_id", "field_id");
CREATE INDEX idx_mvrg_group ON "metadata_validation_range" ("group_id");


-- Comments
COMMENT ON TABLE "metadata_entity_field" IS '实体字段表';


-- Indices
CREATE UNIQUE INDEX uk_field_name ON "metadata_entity_field" ("entity_id", "field_name");
CREATE INDEX idx_field_entity ON "metadata_entity_field" ("entity_id", "deleted");
CREATE INDEX idx_field_tenant_app ON "metadata_entity_field" ("tenant_id", "app_id", "deleted");


-- Comments
COMMENT ON TABLE "infra_config" IS '参数配置表';


-- Indices
CREATE UNIQUE INDEX pk_infra_config ON "infra_config" ("id");


-- Comments
COMMENT ON TABLE "infra_data_source_config" IS '数据源配置表';


-- Indices
CREATE UNIQUE INDEX pk_infra_data_source_config ON "infra_data_source_config" ("id");


-- Comments
COMMENT ON TABLE "infra_file" IS '文件表';


-- Indices
CREATE UNIQUE INDEX pk_infra_file ON "infra_file" ("id");


-- Comments
COMMENT ON TABLE "infra_file_config" IS '文件配置表';


-- Indices
CREATE UNIQUE INDEX pk_infra_file_config ON "infra_file_config" ("id");


-- Comments
COMMENT ON TABLE "metadata_validation_rule_group" IS '规则组表';


-- Indices
CREATE INDEX idx_metadata_validation_rule_group_tenant ON "metadata_validation_rule_group" ("tenant_id");
ALTER TABLE "metadata_validation_format" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE;
ALTER TABLE "metadata_validation_format" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id");
ALTER TABLE "metadata_validation_format" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id");


-- Comments
COMMENT ON TABLE "metadata_validation_format" IS '字段校验-格式规则（内置格式与自定义正则）';


-- Indices
CREATE UNIQUE INDEX uq_mvfmt_active ON "metadata_validation_format" ("tenant_id", "entity_id", "field_id", "format_code");
CREATE INDEX idx_mvfmt_field ON "metadata_validation_format" ("entity_id", "field_id");
CREATE INDEX idx_mvfmt_group ON "metadata_validation_format" ("group_id");


-- Comments
COMMENT ON TABLE "metadata_business_entity" IS '业务实体表';


-- Indices
CREATE INDEX idx_entity_datasource ON "metadata_business_entity" ("datasource_id", "deleted");
CREATE INDEX idx_entity_tenant_app ON "metadata_business_entity" ("app_id", "tenant_id", "deleted");


-- Comments
COMMENT ON TABLE "system_dict_data" IS '字典数据表';


-- Indices
CREATE UNIQUE INDEX pk_system_dict_data ON "system_dict_data" ("id");


-- Comments
COMMENT ON TABLE "system_dept" IS '部门表';


-- Indices
CREATE UNIQUE INDEX pk_system_dept ON "system_dept" ("id");
ALTER TABLE "metadata_validation_child_not_empty" ADD FOREIGN KEY ("entity_id") REFERENCES "metadata_business_entity"("id");
ALTER TABLE "metadata_validation_child_not_empty" ADD FOREIGN KEY ("group_id") REFERENCES "metadata_validation_rule_group"("id") ON DELETE CASCADE;
ALTER TABLE "metadata_validation_child_not_empty" ADD FOREIGN KEY ("field_id") REFERENCES "metadata_entity_field"("id");
ALTER TABLE "metadata_validation_child_not_empty" ADD FOREIGN KEY ("child_entity_id") REFERENCES "metadata_business_entity"("id");


-- Comments
COMMENT ON TABLE "metadata_validation_child_not_empty" IS '字段校验-子表非空规则（一对多/聚合子表最少行数）';


-- Indices
CREATE UNIQUE INDEX uq_mvcn_active ON "metadata_validation_child_not_empty" ("tenant_id", "entity_id", "field_id");
CREATE INDEX idx_mvcn_field ON "metadata_validation_child_not_empty" ("entity_id", "field_id");
CREATE INDEX idx_mvcn_group ON "metadata_validation_child_not_empty" ("group_id");


-- Comments
COMMENT ON TABLE "metadata_field_type_mapping" IS '字段类型映射表';


-- Indices
CREATE UNIQUE INDEX field_type_mapping_pkey ON "metadata_field_type_mapping" ("id");


-- Comments
COMMENT ON TABLE "infra_file_content" IS '文件表';


-- Indices
CREATE UNIQUE INDEX pk_infra_file_content ON "infra_file_content" ("id");


-- Comments
COMMENT ON TABLE "infra_job" IS '定时任务表';


-- Indices
CREATE UNIQUE INDEX pk_infra_job ON "infra_job" ("id");


-- Comments
COMMENT ON TABLE "infra_job_log" IS '定时任务日志表';


-- Indices
CREATE UNIQUE INDEX pk_infra_job_log ON "infra_job_log" ("id");


-- Comments
COMMENT ON TABLE "system_dict_type" IS '字典类型表';


-- Indices
CREATE INDEX idx_dict_owner ON "system_dict_type" ("dict_owner_type", "dict_owner_id", "deleted");
CREATE UNIQUE INDEX uk_constraint_name_type_deleted ON "system_dict_type" ("name", "type", "deleted");
CREATE UNIQUE INDEX pk_system_dict_id ON "system_dict_type" ("id");


-- Comments
COMMENT ON TABLE "system_login_log" IS '系统访问记录';


-- Indices
CREATE UNIQUE INDEX pk_system_login_log ON "system_login_log" ("id");


-- Comments
COMMENT ON TABLE "system_uid_worker_node" IS '数据库主键生成UID算法节点分配表';


-- Comments
COMMENT ON TABLE "system_mail_log" IS '邮件日志表';


-- Indices
CREATE UNIQUE INDEX pk_system_mail_log ON "system_mail_log" ("id");


-- Comments
COMMENT ON TABLE "system_mail_template" IS '邮件模版表';


-- Indices
CREATE UNIQUE INDEX pk_system_mail_template ON "system_mail_template" ("id");


-- Comments
COMMENT ON TABLE "etl_workflow_table" IS 'ETL工作流与表关联关系表';


-- Indices
CREATE INDEX idx_workflow_table_datasource ON "etl_workflow_table" ("datasource_id", "tenant_id", "deleted");
CREATE INDEX idx_workflow_table_table ON "etl_workflow_table" ("table_id", "tenant_id", "deleted");
CREATE INDEX idx_workflow_table_workflow ON "etl_workflow_table" ("workflow_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_workflow_table ON "etl_workflow_table" ("id");


-- Comments
COMMENT ON TABLE "etl_catalog" IS '元数据采集-Catalog信息';


-- Indices
CREATE INDEX idx_etl_catalog_datasource_tenant ON "etl_catalog" ("application_id", "datasource_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_catalog ON "etl_catalog" ("id");


-- Indices
CREATE INDEX idx_etl_datasource_catalog ON "etl_schema" ("application_id", "datasource_id", "catalog_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_schema ON "etl_schema" ("id");


-- Comments
COMMENT ON TABLE "etl_table" IS 'ETL采集的表信息';


-- Indices
CREATE INDEX idx_etl_table_catalog_schema ON "etl_table" ("application_id", "datasource_id", "catalog_id", "schema_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_table ON "etl_table" ("id");


-- Comments
COMMENT ON TABLE "etl_datasource" IS 'ETL数据源配置';


-- Indices
CREATE UNIQUE INDEX uk_etl_datasource_code ON "etl_datasource" ("datasource_code", "application_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_datasource ON "etl_datasource" ("id");


-- Comments
COMMENT ON TABLE "etl_execution_log" IS 'ETL实例日志表';


-- Indices
CREATE INDEX idx_etl_log_workflow_status ON "etl_execution_log" ("workflow_id", "task_status");
CREATE INDEX idx_etl_log_workflow_trigger ON "etl_execution_log" ("workflow_id", "trigger_type");
CREATE INDEX idx_etl_log_workflow ON "etl_execution_log" ("application_id", "workflow_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_execution_log ON "etl_execution_log" ("id");


-- Comments
COMMENT ON TABLE "formula_formula" IS '公式表';


-- Indices
CREATE INDEX idx_formula_formula_tenant ON "formula_formula" ("tenant_id", "deleted");
CREATE INDEX idx_formula_formula_scene ON "formula_formula" ("use_scene");
CREATE INDEX idx_formula_formula_type ON "formula_formula" ("type");
CREATE INDEX idx_formula_formula_name ON "formula_formula" ("name");
CREATE UNIQUE INDEX pk_formula_formula ON "formula_formula" ("id");


-- Comments
COMMENT ON TABLE "system_oauth2_approve" IS 'OAuth2 批准表';


-- Indices
CREATE UNIQUE INDEX pk_system_oauth2_approve ON "system_oauth2_approve" ("id");


-- Comments
COMMENT ON TABLE "system_oauth2_access_token" IS 'OAuth2 访问令牌';


-- Indices
CREATE INDEX idx_system_oauth2_access_token_02 ON "system_oauth2_access_token" ("refresh_token");
CREATE INDEX idx_system_oauth2_access_token_01 ON "system_oauth2_access_token" ("access_token");
CREATE UNIQUE INDEX pk_system_oauth2_access_token ON "system_oauth2_access_token" ("id");


-- Comments
COMMENT ON TABLE "formula_function" IS '函数表';


-- Indices
CREATE INDEX idx_formula_function_tenant ON "formula_function" ("tenant_id", "deleted");
CREATE INDEX idx_formula_function_status ON "formula_function" ("status");
CREATE INDEX idx_formula_function_type ON "formula_function" ("type");
CREATE INDEX idx_formula_function_name ON "formula_function" ("name");
CREATE UNIQUE INDEX pk_formula_function ON "formula_function" ("id");


-- Comments
COMMENT ON TABLE "system_oauth2_client" IS 'OAuth2 客户端表';


-- Indices
CREATE UNIQUE INDEX pk_system_oauth2_client ON "system_oauth2_client" ("id");


-- Comments
COMMENT ON TABLE "etl_schedule_job" IS 'ETL任务作业表';


-- Indices
CREATE UNIQUE INDEX uk_schedule_job_workflow ON "etl_schedule_job" ("workflow_id", "application_id", "tenant_id", "deleted");
CREATE INDEX idx_schedule_job_status ON "etl_schedule_job" ("workflow_id", "job_status", "tenant_id", "deleted");
CREATE INDEX idx_schedule_job_workflow ON "etl_schedule_job" ("workflow_id", "tenant_id", "deleted");
CREATE UNIQUE INDEX pk_etl_schedule_job ON "etl_schedule_job" ("id");


-- Comments
COMMENT ON TABLE "system_oauth2_code" IS 'OAuth2 授权码表';


-- Indices
CREATE UNIQUE INDEX pk_system_oauth2_code ON "system_oauth2_code" ("id");


-- Comments
COMMENT ON TABLE "system_oauth2_refresh_token" IS 'OAuth2 刷新令牌';


-- Indices
CREATE UNIQUE INDEX pk_system_oauth2_refresh_token ON "system_oauth2_refresh_token" ("id");


-- Comments
COMMENT ON TABLE "system_operate_log" IS '操作日志记录 V2 版本';


-- Indices
CREATE UNIQUE INDEX pk_system_operate_log ON "system_operate_log" ("id");


-- Comments
COMMENT ON TABLE "system_post" IS '岗位信息表';


-- Indices
CREATE UNIQUE INDEX pk_system_post ON "system_post" ("id");


-- Comments
COMMENT ON TABLE "system_role" IS '角色信息表';


-- Indices
CREATE UNIQUE INDEX pk_system_role ON "system_role" ("id");


-- Comments
COMMENT ON TABLE "system_sms_channel" IS '短信渠道';


-- Indices
CREATE UNIQUE INDEX pk_system_sms_channel ON "system_sms_channel" ("id");


-- Comments
COMMENT ON TABLE "system_sms_code" IS '手机验证码';


-- Indices
CREATE INDEX idx_system_sms_code_01 ON "system_sms_code" ("mobile");
CREATE UNIQUE INDEX pk_system_sms_code ON "system_sms_code" ("id");


-- Comments
COMMENT ON TABLE "system_sms_log" IS '短信日志';


-- Indices
CREATE UNIQUE INDEX pk_system_sms_log ON "system_sms_log" ("id");


-- Comments
COMMENT ON TABLE "system_sms_template" IS '短信模板';


-- Indices
CREATE UNIQUE INDEX pk_system_sms_template ON "system_sms_template" ("id");


-- Comments
COMMENT ON TABLE "system_social_client" IS '社交客户端表';


-- Indices
CREATE UNIQUE INDEX pk_system_social_client ON "system_social_client" ("id");


-- Indices
CREATE UNIQUE INDEX pk_etl_workflow ON "etl_workflow" ("id");


-- Comments
COMMENT ON TABLE "system_users" IS '用户信息表';


-- Indices
CREATE UNIQUE INDEX pk_system_users ON "system_users" ("id");


-- Comments
COMMENT ON TABLE "system_social_user" IS '社交用户表';


-- Indices
CREATE UNIQUE INDEX pk_system_social_user ON "system_social_user" ("id");


-- Comments
COMMENT ON TABLE "system_social_user_bind" IS '社交绑定表';


-- Indices
CREATE UNIQUE INDEX pk_system_social_user_bind ON "system_social_user_bind" ("id");


-- Comments
COMMENT ON TABLE "system_tenant" IS '租户/空间表';


-- Indices
CREATE UNIQUE INDEX uni_website_deleted ON "system_tenant" ("website", "deleted");
CREATE UNIQUE INDEX uni_code_deleted ON "system_tenant" ("tenant_code", "deleted");
CREATE UNIQUE INDEX pk_system_tenant ON "system_tenant" ("id");


-- Comments
COMMENT ON TABLE "system_tenant_package" IS '租户套餐表';


-- Indices
CREATE UNIQUE INDEX pk_system_tenant_package ON "system_tenant_package" ("id");
