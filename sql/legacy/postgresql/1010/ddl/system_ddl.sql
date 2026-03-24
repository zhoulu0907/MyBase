-- public.system_dept definition

-- Drop table

-- DROP TABLE public.system_dept;

CREATE TABLE public.system_dept (
	id int8 NOT NULL, -- 部门id
	"name" varchar(30) DEFAULT ''::character varying NOT NULL, -- 部门名称
	parent_id int8 DEFAULT 0 NOT NULL, -- 父部门id
	sort int4 DEFAULT 0 NOT NULL, -- 显示顺序
	leader_user_id int8 NULL, -- 负责人
	phone varchar(11) DEFAULT NULL::character varying NULL, -- 联系电话
	email varchar(50) DEFAULT NULL::character varying NULL, -- 邮箱
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	tenant_id int8 NOT NULL, -- 租户编号
	deleted int8 DEFAULT 0 NOT NULL,
	remark varchar(512) NULL, -- 简介和备注
	CONSTRAINT pk_system_dept PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_dept IS '部门表';

-- Column comments

COMMENT ON COLUMN public.system_dept.id IS '部门id';
COMMENT ON COLUMN public.system_dept."name" IS '部门名称';
COMMENT ON COLUMN public.system_dept.parent_id IS '父部门id';
COMMENT ON COLUMN public.system_dept.sort IS '显示顺序';
COMMENT ON COLUMN public.system_dept.leader_user_id IS '负责人';
COMMENT ON COLUMN public.system_dept.phone IS '联系电话';
COMMENT ON COLUMN public.system_dept.email IS '邮箱';
COMMENT ON COLUMN public.system_dept.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_dept.creator IS '创建者';
COMMENT ON COLUMN public.system_dept.create_time IS '创建时间';
COMMENT ON COLUMN public.system_dept.updater IS '更新者';
COMMENT ON COLUMN public.system_dept.update_time IS '更新时间';
COMMENT ON COLUMN public.system_dept.tenant_id IS '租户编号';
COMMENT ON COLUMN public.system_dept.remark IS '简介和备注';


-- public.system_dict_data definition

-- Drop table

-- DROP TABLE public.system_dict_data;

CREATE TABLE public.system_dict_data (
	id int8 NOT NULL, -- 字典编码
	sort int4 DEFAULT 0 NOT NULL, -- 字典排序
	"label" varchar(100) DEFAULT ''::character varying NOT NULL, -- 字典标签
	value varchar(100) DEFAULT ''::character varying NOT NULL, -- 字典键值
	dict_type varchar(100) DEFAULT ''::character varying NOT NULL, -- 字典类型
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	color_type varchar(100) DEFAULT ''::character varying NULL, -- 颜色类型
	css_class varchar(100) DEFAULT ''::character varying NULL, -- css 样式
	remark varchar(500) DEFAULT NULL::character varying NULL, -- 备注
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_dict_data PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_dict_data IS '字典数据表';

-- Column comments

COMMENT ON COLUMN public.system_dict_data.id IS '字典编码';
COMMENT ON COLUMN public.system_dict_data.sort IS '字典排序';
COMMENT ON COLUMN public.system_dict_data."label" IS '字典标签';
COMMENT ON COLUMN public.system_dict_data.value IS '字典键值';
COMMENT ON COLUMN public.system_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN public.system_dict_data.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_dict_data.color_type IS '颜色类型';
COMMENT ON COLUMN public.system_dict_data.css_class IS 'css 样式';
COMMENT ON COLUMN public.system_dict_data.remark IS '备注';
COMMENT ON COLUMN public.system_dict_data.creator IS '创建者';
COMMENT ON COLUMN public.system_dict_data.create_time IS '创建时间';
COMMENT ON COLUMN public.system_dict_data.updater IS '更新者';
COMMENT ON COLUMN public.system_dict_data.update_time IS '更新时间';
COMMENT ON COLUMN public.system_dict_data.deleted IS '是否删除';


-- public.system_dict_type definition

-- Drop table

-- DROP TABLE public.system_dict_type;

CREATE TABLE public.system_dict_type (
	id int8 NOT NULL, -- 字典主键
	"name" varchar(100) DEFAULT ''::character varying NOT NULL, -- 字典名称
	"type" varchar(100) DEFAULT ''::character varying NOT NULL, -- 字典类型
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	remark varchar(500) DEFAULT NULL::character varying NULL, -- 备注
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	deleted_time timestamp NULL, -- 删除时间
	CONSTRAINT pk_system_dict_id PRIMARY KEY (id),
	CONSTRAINT uk_constraint_name_type_deleted UNIQUE (name, type, deleted)
);
COMMENT ON TABLE public.system_dict_type IS '字典类型表';

-- Column comments

COMMENT ON COLUMN public.system_dict_type.id IS '字典主键';
COMMENT ON COLUMN public.system_dict_type."name" IS '字典名称';
COMMENT ON COLUMN public.system_dict_type."type" IS '字典类型';
COMMENT ON COLUMN public.system_dict_type.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_dict_type.remark IS '备注';
COMMENT ON COLUMN public.system_dict_type.creator IS '创建者';
COMMENT ON COLUMN public.system_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN public.system_dict_type.updater IS '更新者';
COMMENT ON COLUMN public.system_dict_type.update_time IS '更新时间';
COMMENT ON COLUMN public.system_dict_type.deleted IS '是否删除';
COMMENT ON COLUMN public.system_dict_type.deleted_time IS '删除时间';


-- public.system_license definition

-- Drop table

-- DROP TABLE public.system_license;

CREATE TABLE public.system_license (
	id int8 NOT NULL, -- 主键
	enterprise_name varchar(128) NOT NULL, -- 企业名称
	enterprise_code varchar(128) NOT NULL, -- 企业编号
	enterprise_address varchar(1024) NULL, -- 企业地址
	platform_type varchar(64) NULL, -- 平台类型
	tenant_limit int4 NOT NULL, -- 租户数量限制
	user_limit int4 NOT NULL, -- 用户数量限制
	expire_time timestamp NOT NULL, -- 到期时间
	status varchar(16) DEFAULT 'enable'::character varying NOT NULL, -- 状态：enable,disable
	is_trial bool DEFAULT false NOT NULL, -- 是否为试用License
	license_file text NOT NULL, -- License文件
	creator int8 NOT NULL, -- 创建者ID
	updater int8 NOT NULL, -- 更新人ID
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 软删标识：非0即删除
	CONSTRAINT system_license_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_system_license_enterprise_name ON public.system_license USING btree (enterprise_name);
CREATE INDEX idx_system_license_expire_time ON public.system_license USING btree (expire_time);
COMMENT ON TABLE public.system_license IS '平台License信息';

-- Column comments

COMMENT ON COLUMN public.system_license.id IS '主键';
COMMENT ON COLUMN public.system_license.enterprise_name IS '企业名称';
COMMENT ON COLUMN public.system_license.enterprise_code IS '企业编号';
COMMENT ON COLUMN public.system_license.enterprise_address IS '企业地址';
COMMENT ON COLUMN public.system_license.platform_type IS '平台类型';
COMMENT ON COLUMN public.system_license.tenant_limit IS '租户数量限制';
COMMENT ON COLUMN public.system_license.user_limit IS '用户数量限制';
COMMENT ON COLUMN public.system_license.expire_time IS '到期时间';
COMMENT ON COLUMN public.system_license.status IS '状态：enable,disable';
COMMENT ON COLUMN public.system_license.is_trial IS '是否为试用License';
COMMENT ON COLUMN public.system_license.license_file IS 'License文件';
COMMENT ON COLUMN public.system_license.creator IS '创建者ID';
COMMENT ON COLUMN public.system_license.updater IS '更新人ID';
COMMENT ON COLUMN public.system_license.create_time IS '创建时间';
COMMENT ON COLUMN public.system_license.update_time IS '更新时间';
COMMENT ON COLUMN public.system_license.deleted IS '软删标识：非0即删除';


-- public.system_login_log definition

-- Drop table

-- DROP TABLE public.system_login_log;

CREATE TABLE public.system_login_log (
	id int8 NOT NULL, -- 访问ID
	log_type int8 NOT NULL, -- 日志类型
	trace_id varchar(64) DEFAULT ''::character varying NOT NULL, -- 链路追踪编号
	user_id int8 DEFAULT 0 NOT NULL, -- 用户编号
	user_type int2 DEFAULT 0 NOT NULL, -- 用户类型
	username varchar(50) DEFAULT ''::character varying NOT NULL, -- 用户账号
	"result" int2 NOT NULL, -- 登陆结果
	user_ip varchar(50) NOT NULL, -- 用户 IP
	user_agent varchar(512) NOT NULL, -- 浏览器 UA
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_login_log PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_login_log IS '系统访问记录';

-- Column comments

COMMENT ON COLUMN public.system_login_log.id IS '访问ID';
COMMENT ON COLUMN public.system_login_log.log_type IS '日志类型';
COMMENT ON COLUMN public.system_login_log.trace_id IS '链路追踪编号';
COMMENT ON COLUMN public.system_login_log.user_id IS '用户编号';
COMMENT ON COLUMN public.system_login_log.user_type IS '用户类型';
COMMENT ON COLUMN public.system_login_log.username IS '用户账号';
COMMENT ON COLUMN public.system_login_log."result" IS '登陆结果';
COMMENT ON COLUMN public.system_login_log.user_ip IS '用户 IP';
COMMENT ON COLUMN public.system_login_log.user_agent IS '浏览器 UA';
COMMENT ON COLUMN public.system_login_log.creator IS '创建者';
COMMENT ON COLUMN public.system_login_log.create_time IS '创建时间';
COMMENT ON COLUMN public.system_login_log.updater IS '更新者';
COMMENT ON COLUMN public.system_login_log.update_time IS '更新时间';
COMMENT ON COLUMN public.system_login_log.deleted IS '是否删除';
COMMENT ON COLUMN public.system_login_log.tenant_id IS '租户编号';


-- public.system_mail_account definition

-- Drop table

-- DROP TABLE public.system_mail_account;

CREATE TABLE public.system_mail_account (
	id int8 NOT NULL, -- 主键
	mail varchar(255) NOT NULL, -- 邮箱
	username varchar(255) NOT NULL, -- 用户名
	"password" varchar(255) NOT NULL, -- 密码
	host varchar(255) NOT NULL, -- SMTP 服务器域名
	port int4 NOT NULL, -- SMTP 服务器端口
	ssl_enable bool DEFAULT false NOT NULL, -- 是否开启 SSL
	starttls_enable bool DEFAULT false NOT NULL, -- 是否开启 STARTTLS
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_mail_account PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_mail_account IS '邮箱账号表';

-- Column comments

COMMENT ON COLUMN public.system_mail_account.id IS '主键';
COMMENT ON COLUMN public.system_mail_account.mail IS '邮箱';
COMMENT ON COLUMN public.system_mail_account.username IS '用户名';
COMMENT ON COLUMN public.system_mail_account."password" IS '密码';
COMMENT ON COLUMN public.system_mail_account.host IS 'SMTP 服务器域名';
COMMENT ON COLUMN public.system_mail_account.port IS 'SMTP 服务器端口';
COMMENT ON COLUMN public.system_mail_account.ssl_enable IS '是否开启 SSL';
COMMENT ON COLUMN public.system_mail_account.starttls_enable IS '是否开启 STARTTLS';
COMMENT ON COLUMN public.system_mail_account.creator IS '创建者';
COMMENT ON COLUMN public.system_mail_account.create_time IS '创建时间';
COMMENT ON COLUMN public.system_mail_account.updater IS '更新者';
COMMENT ON COLUMN public.system_mail_account.update_time IS '更新时间';
COMMENT ON COLUMN public.system_mail_account.deleted IS '是否删除';


-- public.system_mail_log definition

-- Drop table

-- DROP TABLE public.system_mail_log;

CREATE TABLE public.system_mail_log (
	id int8 NOT NULL, -- 编号
	user_id int8 NULL, -- 用户编号
	user_type int2 NULL, -- 用户类型
	to_mail varchar(255) NOT NULL, -- 接收邮箱地址
	account_id int8 NOT NULL, -- 邮箱账号编号
	from_mail varchar(255) NOT NULL, -- 发送邮箱地址
	template_id int8 NOT NULL, -- 模板编号
	template_code varchar(63) NOT NULL, -- 模板编码
	template_nickname varchar(255) DEFAULT NULL::character varying NULL, -- 模版发送人名称
	template_title varchar(255) NOT NULL, -- 邮件标题
	template_content varchar(10240) NOT NULL, -- 邮件内容
	template_params varchar(255) NOT NULL, -- 邮件参数
	send_status int2 DEFAULT 0 NOT NULL, -- 发送状态
	send_time timestamp NULL, -- 发送时间
	send_message_id varchar(255) DEFAULT NULL::character varying NULL, -- 发送返回的消息 ID
	send_exception varchar(4096) DEFAULT NULL::character varying NULL, -- 发送异常
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_mail_log PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_mail_log IS '邮件日志表';

-- Column comments

COMMENT ON COLUMN public.system_mail_log.id IS '编号';
COMMENT ON COLUMN public.system_mail_log.user_id IS '用户编号';
COMMENT ON COLUMN public.system_mail_log.user_type IS '用户类型';
COMMENT ON COLUMN public.system_mail_log.to_mail IS '接收邮箱地址';
COMMENT ON COLUMN public.system_mail_log.account_id IS '邮箱账号编号';
COMMENT ON COLUMN public.system_mail_log.from_mail IS '发送邮箱地址';
COMMENT ON COLUMN public.system_mail_log.template_id IS '模板编号';
COMMENT ON COLUMN public.system_mail_log.template_code IS '模板编码';
COMMENT ON COLUMN public.system_mail_log.template_nickname IS '模版发送人名称';
COMMENT ON COLUMN public.system_mail_log.template_title IS '邮件标题';
COMMENT ON COLUMN public.system_mail_log.template_content IS '邮件内容';
COMMENT ON COLUMN public.system_mail_log.template_params IS '邮件参数';
COMMENT ON COLUMN public.system_mail_log.send_status IS '发送状态';
COMMENT ON COLUMN public.system_mail_log.send_time IS '发送时间';
COMMENT ON COLUMN public.system_mail_log.send_message_id IS '发送返回的消息 ID';
COMMENT ON COLUMN public.system_mail_log.send_exception IS '发送异常';
COMMENT ON COLUMN public.system_mail_log.creator IS '创建者';
COMMENT ON COLUMN public.system_mail_log.create_time IS '创建时间';
COMMENT ON COLUMN public.system_mail_log.updater IS '更新者';
COMMENT ON COLUMN public.system_mail_log.update_time IS '更新时间';
COMMENT ON COLUMN public.system_mail_log.deleted IS '是否删除';


-- public.system_mail_template definition

-- Drop table

-- DROP TABLE public.system_mail_template;

CREATE TABLE public.system_mail_template (
	id int8 NOT NULL, -- 编号
	"name" varchar(63) NOT NULL, -- 模板名称
	code varchar(63) NOT NULL, -- 模板编码
	account_id int8 NOT NULL, -- 发送的邮箱账号编号
	nickname varchar(255) DEFAULT NULL::character varying NULL, -- 发送人名称
	title varchar(255) NOT NULL, -- 模板标题
	"content" varchar(10240) NOT NULL, -- 模板内容
	params varchar(255) NOT NULL, -- 参数数组
	status int2 NOT NULL, -- 状态（0停用，1启用）
	remark varchar(255) DEFAULT NULL::character varying NULL, -- 备注
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_mail_template PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_mail_template IS '邮件模版表';

-- Column comments

COMMENT ON COLUMN public.system_mail_template.id IS '编号';
COMMENT ON COLUMN public.system_mail_template."name" IS '模板名称';
COMMENT ON COLUMN public.system_mail_template.code IS '模板编码';
COMMENT ON COLUMN public.system_mail_template.account_id IS '发送的邮箱账号编号';
COMMENT ON COLUMN public.system_mail_template.nickname IS '发送人名称';
COMMENT ON COLUMN public.system_mail_template.title IS '模板标题';
COMMENT ON COLUMN public.system_mail_template."content" IS '模板内容';
COMMENT ON COLUMN public.system_mail_template.params IS '参数数组';
COMMENT ON COLUMN public.system_mail_template.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_mail_template.remark IS '备注';
COMMENT ON COLUMN public.system_mail_template.creator IS '创建者';
COMMENT ON COLUMN public.system_mail_template.create_time IS '创建时间';
COMMENT ON COLUMN public.system_mail_template.updater IS '更新者';
COMMENT ON COLUMN public.system_mail_template.update_time IS '更新时间';
COMMENT ON COLUMN public.system_mail_template.deleted IS '是否删除';


-- public.system_menu definition

-- Drop table

-- DROP TABLE public.system_menu;

CREATE TABLE public.system_menu (
	id int8 NOT NULL, -- 菜单ID
	"name" varchar(50) NOT NULL, -- 菜单名称
	"permission" varchar(100) DEFAULT ''::character varying NOT NULL, -- 权限标识
	"type" int2 NOT NULL, -- 菜单类型
	sort int4 DEFAULT 0 NOT NULL, -- 显示顺序
	parent_id int8 DEFAULT 0 NOT NULL, -- 父菜单ID
	"path" varchar(200) DEFAULT ''::character varying NULL, -- 路由地址
	icon varchar(100) DEFAULT '#'::character varying NULL, -- 菜单图标
	component varchar(255) DEFAULT NULL::character varying NULL, -- 组件路径
	component_name varchar(255) DEFAULT NULL::character varying NULL, -- 组件名
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	visible bool DEFAULT true NOT NULL, -- 是否可见
	keep_alive bool DEFAULT true NOT NULL, -- 是否缓存
	always_show bool DEFAULT true NOT NULL, -- 是否总是显示
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_menu PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uni_permission_code ON public.system_menu USING btree (permission);
COMMENT ON INDEX public.uni_permission_code IS 'permission_code唯一';
COMMENT ON TABLE public.system_menu IS '菜单权限表';

-- Column comments

COMMENT ON COLUMN public.system_menu.id IS '菜单ID';
COMMENT ON COLUMN public.system_menu."name" IS '菜单名称';
COMMENT ON COLUMN public.system_menu."permission" IS '权限标识';
COMMENT ON COLUMN public.system_menu."type" IS '菜单类型';
COMMENT ON COLUMN public.system_menu.sort IS '显示顺序';
COMMENT ON COLUMN public.system_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN public.system_menu."path" IS '路由地址';
COMMENT ON COLUMN public.system_menu.icon IS '菜单图标';
COMMENT ON COLUMN public.system_menu.component IS '组件路径';
COMMENT ON COLUMN public.system_menu.component_name IS '组件名';
COMMENT ON COLUMN public.system_menu.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_menu.visible IS '是否可见';
COMMENT ON COLUMN public.system_menu.keep_alive IS '是否缓存';
COMMENT ON COLUMN public.system_menu.always_show IS '是否总是显示';
COMMENT ON COLUMN public.system_menu.creator IS '创建者';
COMMENT ON COLUMN public.system_menu.create_time IS '创建时间';
COMMENT ON COLUMN public.system_menu.updater IS '更新者';
COMMENT ON COLUMN public.system_menu.update_time IS '更新时间';
COMMENT ON COLUMN public.system_menu.deleted IS '是否删除';


-- public.system_oauth2_access_token definition

-- Drop table

-- DROP TABLE public.system_oauth2_access_token;

CREATE TABLE public.system_oauth2_access_token (
	id int8 NOT NULL, -- 编号
	user_id int8 NOT NULL, -- 用户编号
	user_type int2 NOT NULL, -- 用户类型
	user_info varchar(512) NOT NULL, -- 用户信息
	access_token varchar(255) NOT NULL, -- 访问令牌
	refresh_token varchar(32) NOT NULL, -- 刷新令牌
	client_id varchar(255) NOT NULL, -- 客户端编号
	scopes varchar(255) DEFAULT NULL::character varying NULL, -- 授权范围
	expires_time timestamp NOT NULL, -- 过期时间
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_oauth2_access_token PRIMARY KEY (id)
);
CREATE INDEX idx_system_oauth2_access_token_01 ON public.system_oauth2_access_token USING btree (access_token);
CREATE INDEX idx_system_oauth2_access_token_02 ON public.system_oauth2_access_token USING btree (refresh_token);
COMMENT ON TABLE public.system_oauth2_access_token IS 'OAuth2 访问令牌';

-- Column comments

COMMENT ON COLUMN public.system_oauth2_access_token.id IS '编号';
COMMENT ON COLUMN public.system_oauth2_access_token.user_id IS '用户编号';
COMMENT ON COLUMN public.system_oauth2_access_token.user_type IS '用户类型';
COMMENT ON COLUMN public.system_oauth2_access_token.user_info IS '用户信息';
COMMENT ON COLUMN public.system_oauth2_access_token.access_token IS '访问令牌';
COMMENT ON COLUMN public.system_oauth2_access_token.refresh_token IS '刷新令牌';
COMMENT ON COLUMN public.system_oauth2_access_token.client_id IS '客户端编号';
COMMENT ON COLUMN public.system_oauth2_access_token.scopes IS '授权范围';
COMMENT ON COLUMN public.system_oauth2_access_token.expires_time IS '过期时间';
COMMENT ON COLUMN public.system_oauth2_access_token.creator IS '创建者';
COMMENT ON COLUMN public.system_oauth2_access_token.create_time IS '创建时间';
COMMENT ON COLUMN public.system_oauth2_access_token.updater IS '更新者';
COMMENT ON COLUMN public.system_oauth2_access_token.update_time IS '更新时间';
COMMENT ON COLUMN public.system_oauth2_access_token.deleted IS '是否删除';
COMMENT ON COLUMN public.system_oauth2_access_token.tenant_id IS '租户编号';


-- public.system_oauth2_approve definition

-- Drop table

-- DROP TABLE public.system_oauth2_approve;

CREATE TABLE public.system_oauth2_approve (
	id int8 NOT NULL, -- 编号
	user_id int8 NOT NULL, -- 用户编号
	user_type int2 NOT NULL, -- 用户类型
	client_id varchar(255) NOT NULL, -- 客户端编号
	"scope" varchar(255) DEFAULT ''::character varying NOT NULL, -- 授权范围
	approved bool DEFAULT false NOT NULL, -- 是否接受
	expires_time timestamp NOT NULL, -- 过期时间
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_oauth2_approve PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_oauth2_approve IS 'OAuth2 批准表';

-- Column comments

COMMENT ON COLUMN public.system_oauth2_approve.id IS '编号';
COMMENT ON COLUMN public.system_oauth2_approve.user_id IS '用户编号';
COMMENT ON COLUMN public.system_oauth2_approve.user_type IS '用户类型';
COMMENT ON COLUMN public.system_oauth2_approve.client_id IS '客户端编号';
COMMENT ON COLUMN public.system_oauth2_approve."scope" IS '授权范围';
COMMENT ON COLUMN public.system_oauth2_approve.approved IS '是否接受';
COMMENT ON COLUMN public.system_oauth2_approve.expires_time IS '过期时间';
COMMENT ON COLUMN public.system_oauth2_approve.creator IS '创建者';
COMMENT ON COLUMN public.system_oauth2_approve.create_time IS '创建时间';
COMMENT ON COLUMN public.system_oauth2_approve.updater IS '更新者';
COMMENT ON COLUMN public.system_oauth2_approve.update_time IS '更新时间';
COMMENT ON COLUMN public.system_oauth2_approve.deleted IS '是否删除';
COMMENT ON COLUMN public.system_oauth2_approve.tenant_id IS '租户编号';


-- public.system_oauth2_client definition

-- Drop table

-- DROP TABLE public.system_oauth2_client;

CREATE TABLE public.system_oauth2_client (
	id int8 NOT NULL, -- 编号
	client_id varchar(255) NOT NULL, -- 客户端编号
	secret varchar(255) NOT NULL, -- 客户端密钥
	"name" varchar(255) NOT NULL, -- 应用名
	logo varchar(255) NOT NULL, -- 应用图标
	description varchar(255) DEFAULT NULL::character varying NULL, -- 应用描述
	status int2 NOT NULL, -- 状态（0停用，1启用）
	access_token_validity_seconds int4 NOT NULL, -- 访问令牌的有效期
	refresh_token_validity_seconds int4 NOT NULL, -- 刷新令牌的有效期
	redirect_uris varchar(255) NOT NULL, -- 可重定向的 URI 地址
	authorized_grant_types varchar(255) NOT NULL, -- 授权类型
	scopes varchar(255) DEFAULT NULL::character varying NULL, -- 授权范围
	auto_approve_scopes varchar(255) DEFAULT NULL::character varying NULL, -- 自动通过的授权范围
	authorities varchar(255) DEFAULT NULL::character varying NULL, -- 权限
	resource_ids varchar(255) DEFAULT NULL::character varying NULL, -- 资源
	additional_information varchar(4096) DEFAULT NULL::character varying NULL, -- 附加信息
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_oauth2_client PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_oauth2_client IS 'OAuth2 客户端表';

-- Column comments

COMMENT ON COLUMN public.system_oauth2_client.id IS '编号';
COMMENT ON COLUMN public.system_oauth2_client.client_id IS '客户端编号';
COMMENT ON COLUMN public.system_oauth2_client.secret IS '客户端密钥';
COMMENT ON COLUMN public.system_oauth2_client."name" IS '应用名';
COMMENT ON COLUMN public.system_oauth2_client.logo IS '应用图标';
COMMENT ON COLUMN public.system_oauth2_client.description IS '应用描述';
COMMENT ON COLUMN public.system_oauth2_client.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_oauth2_client.access_token_validity_seconds IS '访问令牌的有效期';
COMMENT ON COLUMN public.system_oauth2_client.refresh_token_validity_seconds IS '刷新令牌的有效期';
COMMENT ON COLUMN public.system_oauth2_client.redirect_uris IS '可重定向的 URI 地址';
COMMENT ON COLUMN public.system_oauth2_client.authorized_grant_types IS '授权类型';
COMMENT ON COLUMN public.system_oauth2_client.scopes IS '授权范围';
COMMENT ON COLUMN public.system_oauth2_client.auto_approve_scopes IS '自动通过的授权范围';
COMMENT ON COLUMN public.system_oauth2_client.authorities IS '权限';
COMMENT ON COLUMN public.system_oauth2_client.resource_ids IS '资源';
COMMENT ON COLUMN public.system_oauth2_client.additional_information IS '附加信息';
COMMENT ON COLUMN public.system_oauth2_client.creator IS '创建者';
COMMENT ON COLUMN public.system_oauth2_client.create_time IS '创建时间';
COMMENT ON COLUMN public.system_oauth2_client.updater IS '更新者';
COMMENT ON COLUMN public.system_oauth2_client.update_time IS '更新时间';
COMMENT ON COLUMN public.system_oauth2_client.deleted IS '是否删除';


-- public.system_oauth2_code definition

-- Drop table

-- DROP TABLE public.system_oauth2_code;

CREATE TABLE public.system_oauth2_code (
	id int8 NOT NULL, -- 编号
	user_id int8 NOT NULL, -- 用户编号
	user_type int2 NOT NULL, -- 用户类型
	code varchar(32) NOT NULL, -- 授权码
	client_id varchar(255) NOT NULL, -- 客户端编号
	scopes varchar(255) DEFAULT ''::character varying NULL, -- 授权范围
	expires_time timestamp NOT NULL, -- 过期时间
	redirect_uri varchar(255) DEFAULT NULL::character varying NULL, -- 可重定向的 URI 地址
	state varchar(255) DEFAULT ''::character varying NOT NULL, -- 状态
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 1 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_oauth2_code PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_oauth2_code IS 'OAuth2 授权码表';

-- Column comments

COMMENT ON COLUMN public.system_oauth2_code.id IS '编号';
COMMENT ON COLUMN public.system_oauth2_code.user_id IS '用户编号';
COMMENT ON COLUMN public.system_oauth2_code.user_type IS '用户类型';
COMMENT ON COLUMN public.system_oauth2_code.code IS '授权码';
COMMENT ON COLUMN public.system_oauth2_code.client_id IS '客户端编号';
COMMENT ON COLUMN public.system_oauth2_code.scopes IS '授权范围';
COMMENT ON COLUMN public.system_oauth2_code.expires_time IS '过期时间';
COMMENT ON COLUMN public.system_oauth2_code.redirect_uri IS '可重定向的 URI 地址';
COMMENT ON COLUMN public.system_oauth2_code.state IS '状态';
COMMENT ON COLUMN public.system_oauth2_code.creator IS '创建者';
COMMENT ON COLUMN public.system_oauth2_code.create_time IS '创建时间';
COMMENT ON COLUMN public.system_oauth2_code.updater IS '更新者';
COMMENT ON COLUMN public.system_oauth2_code.update_time IS '更新时间';
COMMENT ON COLUMN public.system_oauth2_code.deleted IS '是否删除';
COMMENT ON COLUMN public.system_oauth2_code.tenant_id IS '租户编号';


-- public.system_oauth2_refresh_token definition

-- Drop table

-- DROP TABLE public.system_oauth2_refresh_token;

CREATE TABLE public.system_oauth2_refresh_token (
	id int8 NOT NULL, -- 编号
	user_id int8 NOT NULL, -- 用户编号
	refresh_token varchar(32) NOT NULL, -- 刷新令牌
	user_type int2 NOT NULL, -- 用户类型
	client_id varchar(255) NOT NULL, -- 客户端编号
	scopes varchar(255) DEFAULT NULL::character varying NULL, -- 授权范围
	expires_time timestamp NOT NULL, -- 过期时间
	creator int8 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_oauth2_refresh_token PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_oauth2_refresh_token IS 'OAuth2 刷新令牌';

-- Column comments

COMMENT ON COLUMN public.system_oauth2_refresh_token.id IS '编号';
COMMENT ON COLUMN public.system_oauth2_refresh_token.user_id IS '用户编号';
COMMENT ON COLUMN public.system_oauth2_refresh_token.refresh_token IS '刷新令牌';
COMMENT ON COLUMN public.system_oauth2_refresh_token.user_type IS '用户类型';
COMMENT ON COLUMN public.system_oauth2_refresh_token.client_id IS '客户端编号';
COMMENT ON COLUMN public.system_oauth2_refresh_token.scopes IS '授权范围';
COMMENT ON COLUMN public.system_oauth2_refresh_token.expires_time IS '过期时间';
COMMENT ON COLUMN public.system_oauth2_refresh_token.creator IS '创建者';
COMMENT ON COLUMN public.system_oauth2_refresh_token.create_time IS '创建时间';
COMMENT ON COLUMN public.system_oauth2_refresh_token.updater IS '更新者';
COMMENT ON COLUMN public.system_oauth2_refresh_token.update_time IS '更新时间';
COMMENT ON COLUMN public.system_oauth2_refresh_token.deleted IS '是否删除';
COMMENT ON COLUMN public.system_oauth2_refresh_token.tenant_id IS '租户编号';


-- public.system_operate_log definition

-- Drop table

-- DROP TABLE public.system_operate_log;

CREATE TABLE public.system_operate_log (
	id int8 NOT NULL, -- 日志主键
	trace_id varchar(64) DEFAULT ''::character varying NOT NULL, -- 链路追踪编号
	user_id int8 NOT NULL, -- 用户编号
	user_type int2 DEFAULT 0 NOT NULL, -- 用户类型
	"type" varchar(50) NOT NULL, -- 操作模块类型
	sub_type varchar(50) NOT NULL, -- 操作名
	biz_id int8 NOT NULL, -- 操作数据模块编号
	"action" varchar(2000) DEFAULT ''::character varying NOT NULL, -- 操作内容
	extra varchar(2000) DEFAULT ''::character varying NULL, -- 拓展字段
	request_method varchar(16) DEFAULT ''::character varying NULL, -- 请求方法名
	request_url varchar(255) DEFAULT ''::character varying NULL, -- 请求地址
	user_ip varchar(50) DEFAULT NULL::character varying NULL, -- 用户 IP
	user_agent varchar(200) DEFAULT NULL::character varying NULL, -- 浏览器 UA
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_operate_log PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_operate_log IS '操作日志记录 V2 版本';

-- Column comments

COMMENT ON COLUMN public.system_operate_log.id IS '日志主键';
COMMENT ON COLUMN public.system_operate_log.trace_id IS '链路追踪编号';
COMMENT ON COLUMN public.system_operate_log.user_id IS '用户编号';
COMMENT ON COLUMN public.system_operate_log.user_type IS '用户类型';
COMMENT ON COLUMN public.system_operate_log."type" IS '操作模块类型';
COMMENT ON COLUMN public.system_operate_log.sub_type IS '操作名';
COMMENT ON COLUMN public.system_operate_log.biz_id IS '操作数据模块编号';
COMMENT ON COLUMN public.system_operate_log."action" IS '操作内容';
COMMENT ON COLUMN public.system_operate_log.extra IS '拓展字段';
COMMENT ON COLUMN public.system_operate_log.request_method IS '请求方法名';
COMMENT ON COLUMN public.system_operate_log.request_url IS '请求地址';
COMMENT ON COLUMN public.system_operate_log.user_ip IS '用户 IP';
COMMENT ON COLUMN public.system_operate_log.user_agent IS '浏览器 UA';
COMMENT ON COLUMN public.system_operate_log.creator IS '创建者';
COMMENT ON COLUMN public.system_operate_log.create_time IS '创建时间';
COMMENT ON COLUMN public.system_operate_log.updater IS '更新者';
COMMENT ON COLUMN public.system_operate_log.update_time IS '更新时间';
COMMENT ON COLUMN public.system_operate_log.deleted IS '是否删除';
COMMENT ON COLUMN public.system_operate_log.tenant_id IS '租户编号';


-- public.system_post definition

-- Drop table

-- DROP TABLE public.system_post;

CREATE TABLE public.system_post (
	id int8 NOT NULL, -- 岗位ID
	code varchar(64) NOT NULL, -- 岗位编码
	"name" varchar(50) NOT NULL, -- 岗位名称
	sort int4 NOT NULL, -- 显示顺序
	status int2 NOT NULL, -- 状态（0停用，1启用）
	remark varchar(500) DEFAULT NULL::character varying NULL, -- 备注
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_post PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_post IS '岗位信息表';

-- Column comments

COMMENT ON COLUMN public.system_post.id IS '岗位ID';
COMMENT ON COLUMN public.system_post.code IS '岗位编码';
COMMENT ON COLUMN public.system_post."name" IS '岗位名称';
COMMENT ON COLUMN public.system_post.sort IS '显示顺序';
COMMENT ON COLUMN public.system_post.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_post.remark IS '备注';
COMMENT ON COLUMN public.system_post.creator IS '创建者';
COMMENT ON COLUMN public.system_post.create_time IS '创建时间';
COMMENT ON COLUMN public.system_post.updater IS '更新者';
COMMENT ON COLUMN public.system_post.update_time IS '更新时间';
COMMENT ON COLUMN public.system_post.deleted IS '是否删除';
COMMENT ON COLUMN public.system_post.tenant_id IS '租户编号';


-- public.system_role definition

-- Drop table

-- DROP TABLE public.system_role;

CREATE TABLE public.system_role (
	id int8 NOT NULL, -- 角色ID
	"name" varchar(30) NOT NULL, -- 角色名称
	code varchar(100) NOT NULL, -- 角色权限字符串
	sort int4 DEFAULT 0 NOT NULL, -- 显示顺序
	data_scope int2 DEFAULT 1 NOT NULL, -- 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
	data_scope_dept_ids json NULL, -- 数据范围(指定部门数组)
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	"type" int2 DEFAULT 2 NOT NULL, -- 角色类型(1内置，2普通)
	remark varchar(500) DEFAULT NULL::character varying NULL, -- 备注
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_role PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_role IS '角色信息表';

-- Column comments

COMMENT ON COLUMN public.system_role.id IS '角色ID';
COMMENT ON COLUMN public.system_role."name" IS '角色名称';
COMMENT ON COLUMN public.system_role.code IS '角色权限字符串';
COMMENT ON COLUMN public.system_role.sort IS '显示顺序';
COMMENT ON COLUMN public.system_role.data_scope IS '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）';
COMMENT ON COLUMN public.system_role.data_scope_dept_ids IS '数据范围(指定部门数组)';
COMMENT ON COLUMN public.system_role.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_role."type" IS '角色类型(1内置，2普通)';
COMMENT ON COLUMN public.system_role.remark IS '备注';
COMMENT ON COLUMN public.system_role.creator IS '创建者';
COMMENT ON COLUMN public.system_role.create_time IS '创建时间';
COMMENT ON COLUMN public.system_role.updater IS '更新者';
COMMENT ON COLUMN public.system_role.update_time IS '更新时间';
COMMENT ON COLUMN public.system_role.deleted IS '是否删除';
COMMENT ON COLUMN public.system_role.tenant_id IS '租户编号';


-- public.system_role_menu definition

-- Drop table

-- DROP TABLE public.system_role_menu;

CREATE TABLE public.system_role_menu (
	id int8 DEFAULT nextval('system_role_menu_seq'::regclass) NOT NULL, -- 自增编号
	role_id int8 NOT NULL, -- 角色ID
	menu_id int8 NOT NULL, -- 菜单ID
	creator varchar(64) DEFAULT ''::character varying NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater varchar(64) DEFAULT ''::character varying NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_role_menu PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uni_role_menu_deleted ON public.system_role_menu USING btree (role_id, menu_id, deleted);
COMMENT ON TABLE public.system_role_menu IS '角色和菜单关联表';

-- Column comments

COMMENT ON COLUMN public.system_role_menu.id IS '自增编号';
COMMENT ON COLUMN public.system_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN public.system_role_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN public.system_role_menu.creator IS '创建者';
COMMENT ON COLUMN public.system_role_menu.create_time IS '创建时间';
COMMENT ON COLUMN public.system_role_menu.updater IS '更新者';
COMMENT ON COLUMN public.system_role_menu.update_time IS '更新时间';
COMMENT ON COLUMN public.system_role_menu.deleted IS '是否删除';
COMMENT ON COLUMN public.system_role_menu.tenant_id IS '租户编号';


-- public.system_sms_channel definition

-- Drop table

-- DROP TABLE public.system_sms_channel;

CREATE TABLE public.system_sms_channel (
	id int8 NOT NULL, -- 编号
	signature varchar(12) NOT NULL, -- 短信签名
	code varchar(63) NOT NULL, -- 渠道编码
	status int2 NOT NULL, -- 状态（0停用，1启用）
	remark varchar(255) DEFAULT NULL::character varying NULL, -- 备注
	api_key varchar(128) NOT NULL, -- 短信 API 的账号
	api_secret varchar(128) DEFAULT NULL::character varying NULL, -- 短信 API 的秘钥
	callback_url varchar(255) DEFAULT NULL::character varying NULL, -- 短信发送回调 URL
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_sms_channel PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_sms_channel IS '短信渠道';

-- Column comments

COMMENT ON COLUMN public.system_sms_channel.id IS '编号';
COMMENT ON COLUMN public.system_sms_channel.signature IS '短信签名';
COMMENT ON COLUMN public.system_sms_channel.code IS '渠道编码';
COMMENT ON COLUMN public.system_sms_channel.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_sms_channel.remark IS '备注';
COMMENT ON COLUMN public.system_sms_channel.api_key IS '短信 API 的账号';
COMMENT ON COLUMN public.system_sms_channel.api_secret IS '短信 API 的秘钥';
COMMENT ON COLUMN public.system_sms_channel.callback_url IS '短信发送回调 URL';
COMMENT ON COLUMN public.system_sms_channel.creator IS '创建者';
COMMENT ON COLUMN public.system_sms_channel.create_time IS '创建时间';
COMMENT ON COLUMN public.system_sms_channel.updater IS '更新者';
COMMENT ON COLUMN public.system_sms_channel.update_time IS '更新时间';
COMMENT ON COLUMN public.system_sms_channel.deleted IS '是否删除';


-- public.system_sms_code definition

-- Drop table

-- DROP TABLE public.system_sms_code;

CREATE TABLE public.system_sms_code (
	id int8 NOT NULL, -- 编号
	mobile varchar(16) NOT NULL, -- 手机号
	code varchar(6) NOT NULL, -- 验证码
	create_ip varchar(15) NOT NULL, -- 创建 IP
	scene int2 NOT NULL, -- 发送场景
	today_index int2 NOT NULL, -- 今日发送的第几条
	used int2 NOT NULL, -- 是否使用
	used_time timestamp NULL, -- 使用时间
	used_ip varchar(255) DEFAULT NULL::character varying NULL, -- 使用 IP
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_sms_code PRIMARY KEY (id)
);
CREATE INDEX idx_system_sms_code_01 ON public.system_sms_code USING btree (mobile);
COMMENT ON TABLE public.system_sms_code IS '手机验证码';

-- Column comments

COMMENT ON COLUMN public.system_sms_code.id IS '编号';
COMMENT ON COLUMN public.system_sms_code.mobile IS '手机号';
COMMENT ON COLUMN public.system_sms_code.code IS '验证码';
COMMENT ON COLUMN public.system_sms_code.create_ip IS '创建 IP';
COMMENT ON COLUMN public.system_sms_code.scene IS '发送场景';
COMMENT ON COLUMN public.system_sms_code.today_index IS '今日发送的第几条';
COMMENT ON COLUMN public.system_sms_code.used IS '是否使用';
COMMENT ON COLUMN public.system_sms_code.used_time IS '使用时间';
COMMENT ON COLUMN public.system_sms_code.used_ip IS '使用 IP';
COMMENT ON COLUMN public.system_sms_code.creator IS '创建者';
COMMENT ON COLUMN public.system_sms_code.create_time IS '创建时间';
COMMENT ON COLUMN public.system_sms_code.updater IS '更新者';
COMMENT ON COLUMN public.system_sms_code.update_time IS '更新时间';
COMMENT ON COLUMN public.system_sms_code.deleted IS '是否删除';
COMMENT ON COLUMN public.system_sms_code.tenant_id IS '租户编号';


-- public.system_sms_log definition

-- Drop table

-- DROP TABLE public.system_sms_log;

CREATE TABLE public.system_sms_log (
	id int8 NOT NULL, -- 编号
	channel_id int8 NOT NULL, -- 短信渠道编号
	channel_code varchar(63) NOT NULL, -- 短信渠道编码
	template_id int8 NOT NULL, -- 模板编号
	template_code varchar(63) NOT NULL, -- 模板编码
	template_type int2 NOT NULL, -- 短信类型
	template_content varchar(255) NOT NULL, -- 短信内容
	template_params varchar(255) NOT NULL, -- 短信参数
	api_template_id varchar(63) NOT NULL, -- 短信 API 的模板编号
	mobile varchar(16) NOT NULL, -- 手机号
	user_id int8 NULL, -- 用户编号
	user_type int2 NULL, -- 用户类型
	send_status int2 DEFAULT 0 NOT NULL, -- 发送状态
	send_time timestamp NULL, -- 发送时间
	api_send_code varchar(63) DEFAULT NULL::character varying NULL, -- 短信 API 发送结果的编码
	api_send_msg varchar(255) DEFAULT NULL::character varying NULL, -- 短信 API 发送失败的提示
	api_request_id varchar(255) DEFAULT NULL::character varying NULL, -- 短信 API 发送返回的唯一请求 ID
	api_serial_no varchar(255) DEFAULT NULL::character varying NULL, -- 短信 API 发送返回的序号
	receive_status int2 DEFAULT 0 NOT NULL, -- 接收状态
	receive_time timestamp NULL, -- 接收时间
	api_receive_code varchar(63) DEFAULT NULL::character varying NULL, -- API 接收结果的编码
	api_receive_msg varchar(255) DEFAULT NULL::character varying NULL, -- API 接收结果的说明
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_sms_log PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_sms_log IS '短信日志';

-- Column comments

COMMENT ON COLUMN public.system_sms_log.id IS '编号';
COMMENT ON COLUMN public.system_sms_log.channel_id IS '短信渠道编号';
COMMENT ON COLUMN public.system_sms_log.channel_code IS '短信渠道编码';
COMMENT ON COLUMN public.system_sms_log.template_id IS '模板编号';
COMMENT ON COLUMN public.system_sms_log.template_code IS '模板编码';
COMMENT ON COLUMN public.system_sms_log.template_type IS '短信类型';
COMMENT ON COLUMN public.system_sms_log.template_content IS '短信内容';
COMMENT ON COLUMN public.system_sms_log.template_params IS '短信参数';
COMMENT ON COLUMN public.system_sms_log.api_template_id IS '短信 API 的模板编号';
COMMENT ON COLUMN public.system_sms_log.mobile IS '手机号';
COMMENT ON COLUMN public.system_sms_log.user_id IS '用户编号';
COMMENT ON COLUMN public.system_sms_log.user_type IS '用户类型';
COMMENT ON COLUMN public.system_sms_log.send_status IS '发送状态';
COMMENT ON COLUMN public.system_sms_log.send_time IS '发送时间';
COMMENT ON COLUMN public.system_sms_log.api_send_code IS '短信 API 发送结果的编码';
COMMENT ON COLUMN public.system_sms_log.api_send_msg IS '短信 API 发送失败的提示';
COMMENT ON COLUMN public.system_sms_log.api_request_id IS '短信 API 发送返回的唯一请求 ID';
COMMENT ON COLUMN public.system_sms_log.api_serial_no IS '短信 API 发送返回的序号';
COMMENT ON COLUMN public.system_sms_log.receive_status IS '接收状态';
COMMENT ON COLUMN public.system_sms_log.receive_time IS '接收时间';
COMMENT ON COLUMN public.system_sms_log.api_receive_code IS 'API 接收结果的编码';
COMMENT ON COLUMN public.system_sms_log.api_receive_msg IS 'API 接收结果的说明';
COMMENT ON COLUMN public.system_sms_log.creator IS '创建者';
COMMENT ON COLUMN public.system_sms_log.create_time IS '创建时间';
COMMENT ON COLUMN public.system_sms_log.updater IS '更新者';
COMMENT ON COLUMN public.system_sms_log.update_time IS '更新时间';
COMMENT ON COLUMN public.system_sms_log.deleted IS '是否删除';


-- public.system_sms_template definition

-- Drop table

-- DROP TABLE public.system_sms_template;

CREATE TABLE public.system_sms_template (
	id int8 NOT NULL, -- 编号
	"type" int2 NOT NULL, -- 模板类型
	status int2 NOT NULL, -- 状态（0停用，1启用）
	code varchar(63) NOT NULL, -- 模板编码
	"name" varchar(63) NOT NULL, -- 模板名称
	"content" varchar(255) NOT NULL, -- 模板内容
	params varchar(255) NOT NULL, -- 参数数组
	remark varchar(255) DEFAULT NULL::character varying NULL, -- 备注
	api_template_id varchar(63) NOT NULL, -- 短信 API 的模板编号
	channel_id int8 NOT NULL, -- 短信渠道编号
	channel_code varchar(63) NOT NULL, -- 短信渠道编码
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_system_sms_template PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_sms_template IS '短信模板';

-- Column comments

COMMENT ON COLUMN public.system_sms_template.id IS '编号';
COMMENT ON COLUMN public.system_sms_template."type" IS '模板类型';
COMMENT ON COLUMN public.system_sms_template.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_sms_template.code IS '模板编码';
COMMENT ON COLUMN public.system_sms_template."name" IS '模板名称';
COMMENT ON COLUMN public.system_sms_template."content" IS '模板内容';
COMMENT ON COLUMN public.system_sms_template.params IS '参数数组';
COMMENT ON COLUMN public.system_sms_template.remark IS '备注';
COMMENT ON COLUMN public.system_sms_template.api_template_id IS '短信 API 的模板编号';
COMMENT ON COLUMN public.system_sms_template.channel_id IS '短信渠道编号';
COMMENT ON COLUMN public.system_sms_template.channel_code IS '短信渠道编码';
COMMENT ON COLUMN public.system_sms_template.creator IS '创建者';
COMMENT ON COLUMN public.system_sms_template.create_time IS '创建时间';
COMMENT ON COLUMN public.system_sms_template.updater IS '更新者';
COMMENT ON COLUMN public.system_sms_template.update_time IS '更新时间';
COMMENT ON COLUMN public.system_sms_template.deleted IS '是否删除';


-- public.system_social_client definition

-- Drop table

-- DROP TABLE public.system_social_client;

CREATE TABLE public.system_social_client (
	id int8 NOT NULL, -- 编号
	"name" varchar(255) NOT NULL, -- 应用名
	social_type int2 NOT NULL, -- 社交平台的类型
	user_type int2 NOT NULL, -- 用户类型
	client_id varchar(255) NOT NULL, -- 客户端编号
	client_secret varchar(255) NOT NULL, -- 客户端密钥
	agent_id varchar(255) DEFAULT NULL::character varying NULL, -- 代理编号
	status int2 NOT NULL, -- 状态（0停用，1启用）
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_social_client PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_social_client IS '社交客户端表';

-- Column comments

COMMENT ON COLUMN public.system_social_client.id IS '编号';
COMMENT ON COLUMN public.system_social_client."name" IS '应用名';
COMMENT ON COLUMN public.system_social_client.social_type IS '社交平台的类型';
COMMENT ON COLUMN public.system_social_client.user_type IS '用户类型';
COMMENT ON COLUMN public.system_social_client.client_id IS '客户端编号';
COMMENT ON COLUMN public.system_social_client.client_secret IS '客户端密钥';
COMMENT ON COLUMN public.system_social_client.agent_id IS '代理编号';
COMMENT ON COLUMN public.system_social_client.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_social_client.creator IS '创建者';
COMMENT ON COLUMN public.system_social_client.create_time IS '创建时间';
COMMENT ON COLUMN public.system_social_client.updater IS '更新者';
COMMENT ON COLUMN public.system_social_client.update_time IS '更新时间';
COMMENT ON COLUMN public.system_social_client.deleted IS '是否删除';
COMMENT ON COLUMN public.system_social_client.tenant_id IS '租户编号';


-- public.system_social_user definition

-- Drop table

-- DROP TABLE public.system_social_user;

CREATE TABLE public.system_social_user (
	id int8 NOT NULL, -- 主键(自增策略)
	"type" int2 NOT NULL, -- 社交平台的类型
	openid varchar(32) NOT NULL, -- 社交 openid
	"token" varchar(256) DEFAULT NULL::character varying NULL, -- 社交 token
	raw_token_info varchar(1024) NOT NULL, -- 原始 Token 数据，一般是 JSON 格式
	nickname varchar(32) NOT NULL, -- 用户昵称
	avatar varchar(255) DEFAULT NULL::character varying NULL, -- 用户头像
	raw_user_info varchar(1024) NOT NULL, -- 原始用户数据，一般是 JSON 格式
	code varchar(256) NOT NULL, -- 最后一次的认证 code
	state varchar(256) DEFAULT NULL::character varying NULL, -- 最后一次的认证 state
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_social_user PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_social_user IS '社交用户表';

-- Column comments

COMMENT ON COLUMN public.system_social_user.id IS '主键(自增策略)';
COMMENT ON COLUMN public.system_social_user."type" IS '社交平台的类型';
COMMENT ON COLUMN public.system_social_user.openid IS '社交 openid';
COMMENT ON COLUMN public.system_social_user."token" IS '社交 token';
COMMENT ON COLUMN public.system_social_user.raw_token_info IS '原始 Token 数据，一般是 JSON 格式';
COMMENT ON COLUMN public.system_social_user.nickname IS '用户昵称';
COMMENT ON COLUMN public.system_social_user.avatar IS '用户头像';
COMMENT ON COLUMN public.system_social_user.raw_user_info IS '原始用户数据，一般是 JSON 格式';
COMMENT ON COLUMN public.system_social_user.code IS '最后一次的认证 code';
COMMENT ON COLUMN public.system_social_user.state IS '最后一次的认证 state';
COMMENT ON COLUMN public.system_social_user.creator IS '创建者';
COMMENT ON COLUMN public.system_social_user.create_time IS '创建时间';
COMMENT ON COLUMN public.system_social_user.updater IS '更新者';
COMMENT ON COLUMN public.system_social_user.update_time IS '更新时间';
COMMENT ON COLUMN public.system_social_user.deleted IS '是否删除';
COMMENT ON COLUMN public.system_social_user.tenant_id IS '租户编号';


-- public.system_social_user_bind definition

-- Drop table

-- DROP TABLE public.system_social_user_bind;

CREATE TABLE public.system_social_user_bind (
	id int8 NOT NULL, -- 主键(自增策略)
	user_id int8 NOT NULL, -- 用户编号
	user_type int2 NOT NULL, -- 用户类型
	social_type int2 NOT NULL, -- 社交平台的类型
	social_user_id int8 NOT NULL, -- 社交用户的编号
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_social_user_bind PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_social_user_bind IS '社交绑定表';

-- Column comments

COMMENT ON COLUMN public.system_social_user_bind.id IS '主键(自增策略)';
COMMENT ON COLUMN public.system_social_user_bind.user_id IS '用户编号';
COMMENT ON COLUMN public.system_social_user_bind.user_type IS '用户类型';
COMMENT ON COLUMN public.system_social_user_bind.social_type IS '社交平台的类型';
COMMENT ON COLUMN public.system_social_user_bind.social_user_id IS '社交用户的编号';
COMMENT ON COLUMN public.system_social_user_bind.creator IS '创建者';
COMMENT ON COLUMN public.system_social_user_bind.create_time IS '创建时间';
COMMENT ON COLUMN public.system_social_user_bind.updater IS '更新者';
COMMENT ON COLUMN public.system_social_user_bind.update_time IS '更新时间';
COMMENT ON COLUMN public.system_social_user_bind.deleted IS '是否删除';
COMMENT ON COLUMN public.system_social_user_bind.tenant_id IS '租户编号';


-- public.system_tenant definition

-- Drop table

-- DROP TABLE public.system_tenant;

CREATE TABLE public.system_tenant (
	id int8 NOT NULL, -- 租户编号
	"name" varchar(64) NOT NULL, -- 租户名
	admin_user_id int8 NULL, -- 联系人的用户编号
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	website varchar(256) DEFAULT ''::character varying NULL, -- 绑定域名
	package_id int8 NOT NULL, -- 租户套餐编号
	expire_time timestamp NOT NULL, -- 过期时间
	account_count int4 NOT NULL, -- 账号数量
	creator int8 NOT NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_code varchar(64) NULL, -- 租户编码
	website_h5 varchar(256) NULL, -- 移动端访问地址
	tenant_key varchar(256) NULL, -- 租户key
	tenant_secret varchar(256) NULL, -- 租住secret
	CONSTRAINT pk_system_tenant PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uni_code_deleted ON public.system_tenant USING btree (tenant_code, deleted);
COMMENT ON INDEX public.uni_code_deleted IS 'code_delete唯一索引';
CREATE UNIQUE INDEX uni_website_deleted ON public.system_tenant USING btree (website, deleted);
COMMENT ON INDEX public.uni_website_deleted IS 'website唯一索引';
COMMENT ON TABLE public.system_tenant IS '租户表';

-- Column comments

COMMENT ON COLUMN public.system_tenant.id IS '租户编号';
COMMENT ON COLUMN public.system_tenant."name" IS '租户名';
COMMENT ON COLUMN public.system_tenant.admin_user_id IS '联系人的用户编号';
COMMENT ON COLUMN public.system_tenant.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_tenant.website IS '绑定域名';
COMMENT ON COLUMN public.system_tenant.package_id IS '租户套餐编号';
COMMENT ON COLUMN public.system_tenant.expire_time IS '过期时间';
COMMENT ON COLUMN public.system_tenant.account_count IS '账号数量';
COMMENT ON COLUMN public.system_tenant.creator IS '创建者';
COMMENT ON COLUMN public.system_tenant.create_time IS '创建时间';
COMMENT ON COLUMN public.system_tenant.updater IS '更新者';
COMMENT ON COLUMN public.system_tenant.update_time IS '更新时间';
COMMENT ON COLUMN public.system_tenant.deleted IS '是否删除';
COMMENT ON COLUMN public.system_tenant.tenant_code IS '租户编码';
COMMENT ON COLUMN public.system_tenant.website_h5 IS '移动端访问地址';
COMMENT ON COLUMN public.system_tenant.tenant_key IS '租户key';
COMMENT ON COLUMN public.system_tenant.tenant_secret IS '租住secret';


-- public.system_tenant_package definition

-- Drop table

-- DROP TABLE public.system_tenant_package;

CREATE TABLE public.system_tenant_package (
	id int8 NOT NULL, -- 套餐编号
	"name" varchar(30) NOT NULL, -- 套餐名
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	remark varchar(256) DEFAULT ''::character varying NULL, -- 备注
	creator int8 NOT NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	menu_ids json NULL, -- 关联的菜单IDS
	code varchar(100) NULL, -- 租户套餐编码
	CONSTRAINT pk_system_tenant_package PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_tenant_package IS '租户套餐表';

-- Column comments

COMMENT ON COLUMN public.system_tenant_package.id IS '套餐编号';
COMMENT ON COLUMN public.system_tenant_package."name" IS '套餐名';
COMMENT ON COLUMN public.system_tenant_package.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_tenant_package.remark IS '备注';
COMMENT ON COLUMN public.system_tenant_package.creator IS '创建者';
COMMENT ON COLUMN public.system_tenant_package.create_time IS '创建时间';
COMMENT ON COLUMN public.system_tenant_package.updater IS '更新者';
COMMENT ON COLUMN public.system_tenant_package.update_time IS '更新时间';
COMMENT ON COLUMN public.system_tenant_package.deleted IS '是否删除';
COMMENT ON COLUMN public.system_tenant_package.menu_ids IS '关联的菜单IDS';
COMMENT ON COLUMN public.system_tenant_package.code IS '租户套餐编码';


-- public.system_uid_worker_node definition

-- Drop table

-- DROP TABLE public.system_uid_worker_node;

CREATE TABLE public.system_uid_worker_node (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	worker_host varchar(64) NULL,
	worker_port varchar(64) NULL,
	node_type int2 NULL,
	launch_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	creator int8 DEFAULT 0 NULL,
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updater int8 DEFAULT 0 NULL,
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
	deleted int8 DEFAULT 0 NOT NULL,
	CONSTRAINT system_uid_worker_node_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_uid_worker_node IS '数据库主键生成UID算法节点分配表';


-- public.system_user_post definition

-- Drop table

-- DROP TABLE public.system_user_post;

CREATE TABLE public.system_user_post (
	id int8 DEFAULT nextval('system_user_post_seq'::regclass) NOT NULL, -- id
	user_id int8 DEFAULT 0 NOT NULL, -- 用户ID
	post_id int8 DEFAULT 0 NOT NULL, -- 岗位ID
	creator varchar(64) DEFAULT ''::character varying NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater varchar(64) DEFAULT ''::character varying NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_user_post PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_user_post IS '用户岗位表';

-- Column comments

COMMENT ON COLUMN public.system_user_post.id IS 'id';
COMMENT ON COLUMN public.system_user_post.user_id IS '用户ID';
COMMENT ON COLUMN public.system_user_post.post_id IS '岗位ID';
COMMENT ON COLUMN public.system_user_post.creator IS '创建者';
COMMENT ON COLUMN public.system_user_post.create_time IS '创建时间';
COMMENT ON COLUMN public.system_user_post.updater IS '更新者';
COMMENT ON COLUMN public.system_user_post.update_time IS '更新时间';
COMMENT ON COLUMN public.system_user_post.deleted IS '是否删除';
COMMENT ON COLUMN public.system_user_post.tenant_id IS '租户编号';


-- public.system_user_role definition

-- Drop table

-- DROP TABLE public.system_user_role;

CREATE TABLE public.system_user_role (
	id int8 DEFAULT nextval('system_user_role_seq'::regclass) NOT NULL, -- 自增编号
	user_id int8 NOT NULL, -- 用户ID
	role_id int8 NOT NULL, -- 角色ID
	creator int8 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 创建时间
	updater int8 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_system_user_role PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uni_user_role_deleted ON public.system_user_role USING btree (user_id, role_id, deleted);
COMMENT ON INDEX public.uni_user_role_deleted IS '联合唯一';
COMMENT ON TABLE public.system_user_role IS '用户和角色关联表';

-- Column comments

COMMENT ON COLUMN public.system_user_role.id IS '自增编号';
COMMENT ON COLUMN public.system_user_role.user_id IS '用户ID';
COMMENT ON COLUMN public.system_user_role.role_id IS '角色ID';
COMMENT ON COLUMN public.system_user_role.creator IS '创建者';
COMMENT ON COLUMN public.system_user_role.create_time IS '创建时间';
COMMENT ON COLUMN public.system_user_role.updater IS '更新者';
COMMENT ON COLUMN public.system_user_role.update_time IS '更新时间';
COMMENT ON COLUMN public.system_user_role.deleted IS '是否删除';
COMMENT ON COLUMN public.system_user_role.tenant_id IS '租户编号';


-- public.system_users definition

-- Drop table

-- DROP TABLE public.system_users;

CREATE TABLE public.system_users (
	id int8 NOT NULL, -- 用户ID
	username varchar(64) NOT NULL, -- 用户账号
	"password" varchar(128) DEFAULT ''::character varying NOT NULL, -- 密码
	nickname varchar(64) NOT NULL, -- 用户昵称
	remark varchar(512) DEFAULT NULL::character varying NULL, -- 备注
	dept_id int8 NULL, -- 部门ID
	post_ids json NULL, -- 岗位编号数组
	email varchar(64) DEFAULT ''::character varying NULL, -- 用户邮箱
	mobile varchar(16) DEFAULT ''::character varying NULL, -- 手机号码
	sex int2 DEFAULT 0 NULL, -- 用户性别
	avatar varchar(512) DEFAULT ''::character varying NULL, -- 头像地址
	status int2 DEFAULT 0 NOT NULL, -- 状态（0停用，1启用）
	login_ip varchar(64) DEFAULT ''::character varying NULL, -- 最后登录IP
	login_date timestamp NULL, -- 最后登录时间
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	user_type int2 NULL, -- 用户类型
	admin_type int2 NULL, -- 管理员类型
	CONSTRAINT pk_system_users PRIMARY KEY (id)
);
COMMENT ON TABLE public.system_users IS '用户信息表';

-- Column comments

COMMENT ON COLUMN public.system_users.id IS '用户ID';
COMMENT ON COLUMN public.system_users.username IS '用户账号';
COMMENT ON COLUMN public.system_users."password" IS '密码';
COMMENT ON COLUMN public.system_users.nickname IS '用户昵称';
COMMENT ON COLUMN public.system_users.remark IS '备注';
COMMENT ON COLUMN public.system_users.dept_id IS '部门ID';
COMMENT ON COLUMN public.system_users.post_ids IS '岗位编号数组';
COMMENT ON COLUMN public.system_users.email IS '用户邮箱';
COMMENT ON COLUMN public.system_users.mobile IS '手机号码';
COMMENT ON COLUMN public.system_users.sex IS '用户性别';
COMMENT ON COLUMN public.system_users.avatar IS '头像地址';
COMMENT ON COLUMN public.system_users.status IS '状态（0停用，1启用）';
COMMENT ON COLUMN public.system_users.login_ip IS '最后登录IP';
COMMENT ON COLUMN public.system_users.login_date IS '最后登录时间';
COMMENT ON COLUMN public.system_users.creator IS '创建者';
COMMENT ON COLUMN public.system_users.create_time IS '创建时间';
COMMENT ON COLUMN public.system_users.updater IS '更新者';
COMMENT ON COLUMN public.system_users.update_time IS '更新时间';
COMMENT ON COLUMN public.system_users.deleted IS '是否删除';
COMMENT ON COLUMN public.system_users.tenant_id IS '租户编号';
COMMENT ON COLUMN public.system_users.user_type IS '用户类型';
COMMENT ON COLUMN public.system_users.admin_type IS '管理员类型';