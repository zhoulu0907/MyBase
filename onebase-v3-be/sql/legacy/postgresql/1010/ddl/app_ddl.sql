-- public.app_application definition

-- Drop table

-- DROP TABLE public.app_application;

CREATE TABLE public.app_application (
	id int8 NOT NULL, -- 主键ID
	app_uid varchar(16) NULL, -- 应用uid(自动生成短码)
	app_name varchar(128) NOT NULL, -- 应用名称
	app_code varchar(64) NOT NULL, -- 应用编码(用户输入)
	app_mode varchar(64) NOT NULL, -- 应用模式
	theme_color varchar(64) NOT NULL, -- 主题颜色
	icon_name varchar(256) DEFAULT NULL::character varying NULL, -- 应用图标
	icon_color varchar(32) DEFAULT NULL::character varying NULL, -- 图标颜色
	version_number varchar(16) NOT NULL, -- 当前版本
	app_status int2 NOT NULL, -- 状态（编辑、发布）
	version_url varchar(1024) NULL,
	description varchar(1024) DEFAULT NULL::character varying NULL, -- 应用描述
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_application PRIMARY KEY (id),
	CONSTRAINT uk_app_application_uid UNIQUE (app_uid, deleted)
);
COMMENT ON TABLE public.app_application IS '应用管理表';

-- Column comments

COMMENT ON COLUMN public.app_application.id IS '主键ID';
COMMENT ON COLUMN public.app_application.app_uid IS '应用uid(自动生成短码)';
COMMENT ON COLUMN public.app_application.app_name IS '应用名称';
COMMENT ON COLUMN public.app_application.app_code IS '应用编码(用户输入)';
COMMENT ON COLUMN public.app_application.app_mode IS '应用模式';
COMMENT ON COLUMN public.app_application.theme_color IS '主题颜色';
COMMENT ON COLUMN public.app_application.icon_name IS '应用图标';
COMMENT ON COLUMN public.app_application.icon_color IS '图标颜色';
COMMENT ON COLUMN public.app_application.version_number IS '当前版本';
COMMENT ON COLUMN public.app_application.app_status IS '状态（编辑、发布）';
COMMENT ON COLUMN public.app_application.description IS '应用描述';


-- public.app_application_tag definition

-- Drop table

-- DROP TABLE public.app_application_tag;

CREATE TABLE public.app_application_tag (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用Id
	tag_id int8 NOT NULL, -- 标签Id
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_application_tag PRIMARY KEY (id),
	CONSTRAINT uk_app_application_tag UNIQUE (application_id, tag_id, deleted)
);
COMMENT ON TABLE public.app_application_tag IS '应用标签管理表';

-- Column comments

COMMENT ON COLUMN public.app_application_tag.id IS '主键Id';
COMMENT ON COLUMN public.app_application_tag.application_id IS '应用Id';
COMMENT ON COLUMN public.app_application_tag.tag_id IS '标签Id';


-- public.app_auth_data_filter definition

-- Drop table

-- DROP TABLE public.app_auth_data_filter;

CREATE TABLE public.app_auth_data_filter (
	id int8 NOT NULL, -- 主键Id
	group_id int8 NOT NULL, -- 数据权限组id
	condition_group int4 DEFAULT 0 NOT NULL, -- 条件组：用于将条件分组，如(1 and 2)中的1和2为同一组
	condition_order int4 DEFAULT 0 NOT NULL, -- 条件展示顺序：控制条件在界面上的显示顺序
	field_id int8 NOT NULL, -- 字段id
	field_operator varchar(20) NOT NULL, -- 比较操作符号：等于、大于、包含等
	field_value_type varchar(20) NOT NULL, -- 值类型：变量、值
	field_value varchar(100) NOT NULL, -- 字段值
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_data_filter PRIMARY KEY (id)
);
COMMENT ON TABLE public.app_auth_data_filter IS '数据权限配置-数据过滤条件';

-- Column comments

COMMENT ON COLUMN public.app_auth_data_filter.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_data_filter.group_id IS '数据权限组id';
COMMENT ON COLUMN public.app_auth_data_filter.condition_group IS '条件组：用于将条件分组，如(1 and 2)中的1和2为同一组';
COMMENT ON COLUMN public.app_auth_data_filter.condition_order IS '条件展示顺序：控制条件在界面上的显示顺序';
COMMENT ON COLUMN public.app_auth_data_filter.field_id IS '字段id';
COMMENT ON COLUMN public.app_auth_data_filter.field_operator IS '比较操作符号：等于、大于、包含等';
COMMENT ON COLUMN public.app_auth_data_filter.field_value_type IS '值类型：变量、值';
COMMENT ON COLUMN public.app_auth_data_filter.field_value IS '字段值';


-- public.app_auth_data_group definition

-- Drop table

-- DROP TABLE public.app_auth_data_group;

CREATE TABLE public.app_auth_data_group (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用id
	role_id int8 NOT NULL, -- 角色id
	menu_id int8 DEFAULT 0 NOT NULL, -- 菜单id
	group_name varchar(100) NOT NULL, -- 组名称
	group_order int4 NULL, -- 组排序
	description varchar(256) DEFAULT NULL::character varying NULL, -- 描述
	scope_field_id int8 NOT NULL, -- 关联业务实体字段id
	scope_level varchar(32) NOT NULL, -- 关联业务实体字段对应的权限范围
	scope_value varchar(256) NULL, -- 关联业务实体字段对应的权限值
	is_operable int2 DEFAULT 0 NOT NULL, -- 是否可以操作
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_data_group PRIMARY KEY (id)
);
COMMENT ON TABLE public.app_auth_data_group IS '数据权限组配置表';

-- Column comments

COMMENT ON COLUMN public.app_auth_data_group.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_data_group.application_id IS '应用id';
COMMENT ON COLUMN public.app_auth_data_group.role_id IS '角色id';
COMMENT ON COLUMN public.app_auth_data_group.menu_id IS '菜单id';
COMMENT ON COLUMN public.app_auth_data_group.group_name IS '组名称';
COMMENT ON COLUMN public.app_auth_data_group.group_order IS '组排序';
COMMENT ON COLUMN public.app_auth_data_group.description IS '描述';
COMMENT ON COLUMN public.app_auth_data_group.scope_field_id IS '关联业务实体字段id';
COMMENT ON COLUMN public.app_auth_data_group.scope_level IS '关联业务实体字段对应的权限范围';
COMMENT ON COLUMN public.app_auth_data_group.scope_value IS '关联业务实体字段对应的权限值';
COMMENT ON COLUMN public.app_auth_data_group.is_operable IS '是否可以操作';


-- public.app_auth_field definition

-- Drop table

-- DROP TABLE public.app_auth_field;

CREATE TABLE public.app_auth_field (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用id
	role_id int8 NOT NULL, -- 角色id
	menu_id int8 NOT NULL, -- 菜单id
	field_id int8 NOT NULL, -- 字段id
	is_can_read int2 DEFAULT 0 NULL, -- 是否可阅读
	is_can_edit int2 DEFAULT 0 NULL, -- 是否可编辑
	is_can_download int2 DEFAULT 0 NULL, -- 是否可下载
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_field PRIMARY KEY (id),
	CONSTRAINT uk_app_auth_field UNIQUE (application_id, role_id, menu_id, field_id, deleted)
);
COMMENT ON TABLE public.app_auth_field IS '应用字段权限';

-- Column comments

COMMENT ON COLUMN public.app_auth_field.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_field.application_id IS '应用id';
COMMENT ON COLUMN public.app_auth_field.role_id IS '角色id';
COMMENT ON COLUMN public.app_auth_field.menu_id IS '菜单id';
COMMENT ON COLUMN public.app_auth_field.field_id IS '字段id';
COMMENT ON COLUMN public.app_auth_field.is_can_read IS '是否可阅读';
COMMENT ON COLUMN public.app_auth_field.is_can_edit IS '是否可编辑';
COMMENT ON COLUMN public.app_auth_field.is_can_download IS '是否可下载';


-- public.app_auth_operation definition

-- Drop table

-- DROP TABLE public.app_auth_operation;

CREATE TABLE public.app_auth_operation (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用id
	role_id int8 NOT NULL, -- 角色id
	menu_id int8 NOT NULL, -- 菜单id
	operation_code varchar(64) NOT NULL, -- 操作编码
	is_allowed int2 DEFAULT 0 NOT NULL, -- 是否允许
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_operation PRIMARY KEY (id),
	CONSTRAINT uk_app_auth_operation UNIQUE (application_id, role_id, menu_id, operation_code, deleted)
);
COMMENT ON TABLE public.app_auth_operation IS '应用操作权限';

-- Column comments

COMMENT ON COLUMN public.app_auth_operation.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_operation.application_id IS '应用id';
COMMENT ON COLUMN public.app_auth_operation.role_id IS '角色id';
COMMENT ON COLUMN public.app_auth_operation.menu_id IS '菜单id';
COMMENT ON COLUMN public.app_auth_operation.operation_code IS '操作编码';
COMMENT ON COLUMN public.app_auth_operation.is_allowed IS '是否允许';


-- public.app_auth_permission definition

-- Drop table

-- DROP TABLE public.app_auth_permission;

CREATE TABLE public.app_auth_permission (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用id
	role_id int8 NOT NULL, -- 角色id
	menu_id int8 NOT NULL, -- 菜单id
	is_page_allowed int2 DEFAULT 0 NOT NULL, -- 页面是否可访问
	is_all_views_allowed int2 DEFAULT 0 NOT NULL, -- 所有视图可访问
	is_all_fields_allowed int2 DEFAULT 0 NOT NULL, -- 所有字段可操作
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_permission PRIMARY KEY (id),
	CONSTRAINT uk_app_auth_permission UNIQUE (application_id, role_id, menu_id, deleted)
);
COMMENT ON TABLE public.app_auth_permission IS '应用权限';

-- Column comments

COMMENT ON COLUMN public.app_auth_permission.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_permission.application_id IS '应用id';
COMMENT ON COLUMN public.app_auth_permission.role_id IS '角色id';
COMMENT ON COLUMN public.app_auth_permission.menu_id IS '菜单id';
COMMENT ON COLUMN public.app_auth_permission.is_page_allowed IS '页面是否可访问';
COMMENT ON COLUMN public.app_auth_permission.is_all_views_allowed IS '所有视图可访问';
COMMENT ON COLUMN public.app_auth_permission.is_all_fields_allowed IS '所有字段可操作';


-- public.app_auth_role definition

-- Drop table

-- DROP TABLE public.app_auth_role;

CREATE TABLE public.app_auth_role (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用Id
	role_code varchar(64) NOT NULL, -- 角色编码
	role_name varchar(64) NOT NULL, -- 角色名称
	role_type int2 DEFAULT 3 NOT NULL, -- 角色类型，1系统管理员2系统默认用户3用户定义
	description varchar(256) DEFAULT NULL::character varying NULL, -- 描述
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_role PRIMARY KEY (id),
	CONSTRAINT uk_app_auth_role UNIQUE (application_id, role_code, deleted)
);
COMMENT ON TABLE public.app_auth_role IS '应用角色';

-- Column comments

COMMENT ON COLUMN public.app_auth_role.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_role.application_id IS '应用Id';
COMMENT ON COLUMN public.app_auth_role.role_code IS '角色编码';
COMMENT ON COLUMN public.app_auth_role.role_name IS '角色名称';
COMMENT ON COLUMN public.app_auth_role.role_type IS '角色类型，1系统管理员2系统默认用户3用户定义';
COMMENT ON COLUMN public.app_auth_role.description IS '描述';


-- public.app_auth_role_user definition

-- Drop table

-- DROP TABLE public.app_auth_role_user;

CREATE TABLE public.app_auth_role_user (
	id int8 NOT NULL, -- 主键Id
	role_id int8 NOT NULL, -- 角色id
	user_id int8 DEFAULT 0 NOT NULL, -- 用户Id
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_role_user PRIMARY KEY (id),
	CONSTRAINT uk_app_auth_role_user UNIQUE (user_id, role_id, deleted)
);
COMMENT ON TABLE public.app_auth_role_user IS '应用角色关联表';

-- Column comments

COMMENT ON COLUMN public.app_auth_role_user.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_role_user.role_id IS '角色id';
COMMENT ON COLUMN public.app_auth_role_user.user_id IS '用户Id';


-- public.app_auth_view definition

-- Drop table

-- DROP TABLE public.app_auth_view;

CREATE TABLE public.app_auth_view (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用Id
	role_id int8 NOT NULL, -- 角色id
	menu_id int8 NOT NULL, -- 菜单id
	view_id int8 NOT NULL, -- 实体id
	is_allowed int2 DEFAULT 0 NOT NULL, -- 是否可访问
	lock_version int8 DEFAULT 0 NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_auth_entity PRIMARY KEY (id),
	CONSTRAINT uk_app_auth_entity UNIQUE (application_id, role_id, menu_id, view_id, deleted)
);
COMMENT ON TABLE public.app_auth_view IS '应用功能权限-视图权限';

-- Column comments

COMMENT ON COLUMN public.app_auth_view.id IS '主键Id';
COMMENT ON COLUMN public.app_auth_view.application_id IS '应用Id';
COMMENT ON COLUMN public.app_auth_view.role_id IS '角色id';
COMMENT ON COLUMN public.app_auth_view.menu_id IS '菜单id';
COMMENT ON COLUMN public.app_auth_view.view_id IS '实体id';
COMMENT ON COLUMN public.app_auth_view.is_allowed IS '是否可访问';


-- public.app_menu definition

-- Drop table

-- DROP TABLE public.app_menu;

CREATE TABLE public.app_menu (
	id int8 NOT NULL, -- 主键Id
	application_id int8 NOT NULL, -- 应用Id
	entity_id int8 NULL, -- 实体Id
	parent_id int8 NOT NULL, -- 父菜单id
	menu_code varchar(64) NOT NULL, -- 菜单编码
	menu_sort int4 DEFAULT 0 NOT NULL, -- 菜单排序
	menu_type int2 NOT NULL, -- 菜单类型
	menu_name varchar(64) NOT NULL, -- 菜单名称
	menu_icon varchar(64) NOT NULL, -- 菜单图标
	action_target varchar(256) NULL, -- 菜单动作
	is_visible int2 DEFAULT 1 NOT NULL, -- 是否可见
	lock_version int8 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_menu PRIMARY KEY (id)
);
COMMENT ON TABLE public.app_menu IS '应用菜单表';

-- Column comments

COMMENT ON COLUMN public.app_menu.id IS '主键Id';
COMMENT ON COLUMN public.app_menu.application_id IS '应用Id';
COMMENT ON COLUMN public.app_menu.entity_id IS '实体Id';
COMMENT ON COLUMN public.app_menu.parent_id IS '父菜单id';
COMMENT ON COLUMN public.app_menu.menu_code IS '菜单编码';
COMMENT ON COLUMN public.app_menu.menu_sort IS '菜单排序';
COMMENT ON COLUMN public.app_menu.menu_type IS '菜单类型';
COMMENT ON COLUMN public.app_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN public.app_menu.menu_icon IS '菜单图标';
COMMENT ON COLUMN public.app_menu.action_target IS '菜单动作';
COMMENT ON COLUMN public.app_menu.is_visible IS '是否可见';


-- public.app_resource_component definition

-- Drop table

-- DROP TABLE public.app_resource_component;

CREATE TABLE public.app_resource_component (
	component_code varchar(255) NOT NULL,
	page_id int8 NOT NULL,
	in_table bool NOT NULL,
	component_type varchar(64) NOT NULL,
	config text NOT NULL,
	edit_data text NOT NULL,
	parent_code varchar(255) NULL,
	block_index int8 DEFAULT 0 NOT NULL,
	container_index int8 DEFAULT 0 NOT NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_resource_page definition

-- Drop table

-- DROP TABLE public.app_resource_page;

CREATE TABLE public.app_resource_page (
	pageset_id int8 NOT NULL,
	page_name varchar(255) NOT NULL,
	page_type varchar(64) NOT NULL,
	title varchar(255) NOT NULL,
	layout varchar(255) NOT NULL,
	width varchar(255) NOT NULL,
	margin varchar(255) NOT NULL,
	background_color varchar(255) NOT NULL,
	main_metadata varchar(255) NOT NULL,
	bpm_enabled bool NOT NULL,
	router_path varchar(255) NOT NULL,
	router_name varchar(255) NOT NULL,
	router_meta_auth_required bool NOT NULL,
	router_meta_title varchar(255) NOT NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_resource_page_metadata definition

-- Drop table

-- DROP TABLE public.app_resource_page_metadata;

CREATE TABLE public.app_resource_page_metadata (
	page_id int8 NOT NULL,
	metadata varchar(255) NOT NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_resource_page_ref_router definition

-- Drop table

-- DROP TABLE public.app_resource_page_ref_router;

CREATE TABLE public.app_resource_page_ref_router (
	page_id int8 NOT NULL,
	router_name varchar(255) NOT NULL,
	router_type varchar(255) NOT NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_resource_pageset definition

-- Drop table

-- DROP TABLE public.app_resource_pageset;

CREATE TABLE public.app_resource_pageset (
	pageset_code varchar(255) NOT NULL,
	menu_id int8 NOT NULL,
	main_metadata varchar(255) NOT NULL,
	pageset_name varchar(255) NOT NULL,
	display_name varchar(255) NOT NULL,
	data_title_type int4 NOT NULL DEFAULT 0,
	data_title varchar(255),
	redirect_type int4 NOT NULL DEFAULT 0,
	description text NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_resource_pageset_label definition

-- Drop table

-- DROP TABLE public.app_resource_pageset_label;

CREATE TABLE public.app_resource_pageset_label (
	pageset_id int8 NOT NULL,
	label_name varchar(255) NOT NULL,
	label_value varchar(255) NOT NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_resource_pageset_page definition

-- Drop table

-- DROP TABLE public.app_resource_pageset_page;

CREATE TABLE public.app_resource_pageset_page (
	pageset_id int8 NOT NULL,
	page_id int8 NOT NULL,
	page_type varchar(64) NOT NULL,
	is_default bool NOT NULL,
	default_seq int4 NOT NULL,
	tenant_id int8 NULL,
	id int8 NULL,
	create_time timestamp NULL,
	update_time timestamp NULL,
	creator int8 NULL,
	updater int8 NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	lock_version int8 NULL
);


-- public.app_tag definition

-- Drop table

-- DROP TABLE public.app_tag;

CREATE TABLE public.app_tag (
	id int8 NOT NULL,
	tag_name varchar(128) NOT NULL,
	lock_version int8 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT app_tag_pk PRIMARY KEY (id)
);


-- public.app_version definition

-- Drop table

-- DROP TABLE public.app_version;

CREATE TABLE public.app_version (
	id int8 NOT NULL, -- 主键ID
	application_id int8 NOT NULL, -- 应用ID
	version_name varchar(128) NOT NULL, -- 版本名称
	version_number varchar(64) NOT NULL, -- 版本编号
	lock_version int8 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	version_description text NULL,
	environment varchar(128) NULL,
	operation_type int4 NULL,
	version_url varchar(1024) NULL,
	CONSTRAINT pk_app_application_version PRIMARY KEY (id)
);

-- Column comments

COMMENT ON COLUMN public.app_version.id IS '主键ID';
COMMENT ON COLUMN public.app_version.application_id IS '应用ID';
COMMENT ON COLUMN public.app_version.version_name IS '版本名称';
COMMENT ON COLUMN public.app_version.version_number IS '版本编号';


-- public.app_version_resource definition

-- Drop table

-- DROP TABLE public.app_version_resource;

CREATE TABLE public.app_version_resource (
	id int8 NOT NULL, -- 主键ID
	application_id int8 NOT NULL, -- 应用ID
	version_id int8 NOT NULL, -- 版本ID
	res_type varchar(64) NOT NULL, -- 协议类型
	res_data text NOT NULL, -- 资源数据
	lock_version int8 DEFAULT 0 NOT NULL,
	creator int8 DEFAULT 0 NOT NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NOT NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	tenant_id int8 DEFAULT 0 NOT NULL,
	CONSTRAINT pk_app_application_version_resource PRIMARY KEY (id)
);

-- Column comments

COMMENT ON COLUMN public.app_version_resource.id IS '主键ID';
COMMENT ON COLUMN public.app_version_resource.application_id IS '应用ID';
COMMENT ON COLUMN public.app_version_resource.version_id IS '版本ID';
COMMENT ON COLUMN public.app_version_resource.res_type IS '协议类型';
COMMENT ON COLUMN public.app_version_resource.res_data IS '资源数据';

CREATE TABLE public.app_export (
    id int8 NOT NULL, -- 主键ID
    application_id int8 NOT NULL, -- 应用ID
    object_id varchar(64), -- 资源ID
    version_id int8 NOT NULL, -- 版本ID
    export_status int2 NOT NULL, -- 导出状态 0-未知 1-导出中 2-导出成功 3-导出失败
    lock_version int8 DEFAULT 0 NOT NULL,
    creator int8 DEFAULT 0 NOT NULL,
    create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater int8 DEFAULT 0 NOT NULL,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted int8 DEFAULT 0 NOT NULL,
    tenant_id int8 DEFAULT 0 NOT NULL,
    CONSTRAINT pk_app_application_version_resource PRIMARY KEY (id)
);

COMMENT ON COLUMN public.app_export.id IS '主键ID';
COMMENT ON COLUMN public.app_export.application_id IS '应用ID';
COMMENT ON COLUMN public.app_export.version_id IS '版本ID';
COMMENT ON COLUMN public.app_export.object_id IS '资源ID';
COMMENT ON COLUMN public.app_export.export_status IS '导出状态 0-未知 1-导出中 2-导出成功 3-导出失败';
