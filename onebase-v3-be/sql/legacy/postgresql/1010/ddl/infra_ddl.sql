-- public.infra_api_access_log definition

-- Drop table

-- DROP TABLE public.infra_api_access_log;

CREATE TABLE public.infra_api_access_log (
	id int8 NOT NULL, -- 日志主键
	trace_id varchar(64) DEFAULT ''::character varying NOT NULL, -- 链路追踪编号
	user_id int8 DEFAULT 0 NOT NULL, -- 用户编号
	user_type int2 DEFAULT 0 NOT NULL, -- 用户类型
	application_name varchar(50) NOT NULL, -- 应用名
	request_method varchar(16) DEFAULT ''::character varying NOT NULL, -- 请求方法名
	request_url varchar(255) DEFAULT ''::character varying NOT NULL, -- 请求地址
	request_params text NULL, -- 请求参数
	response_body text NULL, -- 响应结果
	user_ip varchar(50) NOT NULL, -- 用户 IP
	user_agent varchar(512) NOT NULL, -- 浏览器 UA
	operate_module varchar(50) DEFAULT NULL::character varying NULL, -- 操作模块
	operate_name varchar(50) DEFAULT NULL::character varying NULL, -- 操作名
	operate_type int2 DEFAULT 0 NULL, -- 操作分类
	begin_time timestamp NOT NULL, -- 开始请求时间
	end_time timestamp NOT NULL, -- 结束请求时间
	duration int4 NOT NULL, -- 执行时长
	result_code int4 DEFAULT 0 NOT NULL, -- 结果码
	result_msg varchar(512) DEFAULT ''::character varying NULL, -- 结果提示
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 NOT NULL, -- 租户编号
	CONSTRAINT pk_infra_api_access_log PRIMARY KEY (id)
);
CREATE INDEX idx_infra_api_access_log_01 ON public.infra_api_access_log USING btree (create_time);
COMMENT ON TABLE public.infra_api_access_log IS 'API 访问日志表';

-- Column comments

COMMENT ON COLUMN public.infra_api_access_log.id IS '日志主键';
COMMENT ON COLUMN public.infra_api_access_log.trace_id IS '链路追踪编号';
COMMENT ON COLUMN public.infra_api_access_log.user_id IS '用户编号';
COMMENT ON COLUMN public.infra_api_access_log.user_type IS '用户类型';
COMMENT ON COLUMN public.infra_api_access_log.application_name IS '应用名';
COMMENT ON COLUMN public.infra_api_access_log.request_method IS '请求方法名';
COMMENT ON COLUMN public.infra_api_access_log.request_url IS '请求地址';
COMMENT ON COLUMN public.infra_api_access_log.request_params IS '请求参数';
COMMENT ON COLUMN public.infra_api_access_log.response_body IS '响应结果';
COMMENT ON COLUMN public.infra_api_access_log.user_ip IS '用户 IP';
COMMENT ON COLUMN public.infra_api_access_log.user_agent IS '浏览器 UA';
COMMENT ON COLUMN public.infra_api_access_log.operate_module IS '操作模块';
COMMENT ON COLUMN public.infra_api_access_log.operate_name IS '操作名';
COMMENT ON COLUMN public.infra_api_access_log.operate_type IS '操作分类';
COMMENT ON COLUMN public.infra_api_access_log.begin_time IS '开始请求时间';
COMMENT ON COLUMN public.infra_api_access_log.end_time IS '结束请求时间';
COMMENT ON COLUMN public.infra_api_access_log.duration IS '执行时长';
COMMENT ON COLUMN public.infra_api_access_log.result_code IS '结果码';
COMMENT ON COLUMN public.infra_api_access_log.result_msg IS '结果提示';
COMMENT ON COLUMN public.infra_api_access_log.creator IS '创建者';
COMMENT ON COLUMN public.infra_api_access_log.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_api_access_log.updater IS '更新者';
COMMENT ON COLUMN public.infra_api_access_log.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_api_access_log.deleted IS '是否删除';
COMMENT ON COLUMN public.infra_api_access_log.tenant_id IS '租户编号';


-- public.infra_api_error_log definition

-- Drop table

-- DROP TABLE public.infra_api_error_log;

CREATE TABLE public.infra_api_error_log (
	id int8 NOT NULL, -- 编号
	trace_id varchar(64) NOT NULL, -- 链路追踪编号¶     *¶     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。
	user_id int4 DEFAULT 0 NOT NULL, -- 用户编号
	user_type int2 DEFAULT 0 NOT NULL, -- 用户类型
	application_name varchar(50) NOT NULL, -- 应用名¶     *¶     * 目前读取 spring.application.name
	request_method varchar(16) NOT NULL, -- 请求方法名
	request_url varchar(255) NOT NULL, -- 请求地址
	request_params varchar(8000) NOT NULL, -- 请求参数
	user_ip varchar(50) NOT NULL, -- 用户 IP
	user_agent varchar(512) NOT NULL, -- 浏览器 UA
	exception_time timestamp NOT NULL, -- 异常发生时间
	exception_name varchar(128) DEFAULT ''::character varying NOT NULL, -- 异常名¶     *¶     * {@link Throwable#getClass()} 的类全名
	exception_message text NOT NULL, -- 异常导致的消息¶     *¶     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getMessage(Throwable)}
	exception_root_cause_message text NOT NULL, -- 异常导致的根消息¶     *¶     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getRootCauseMessage(Throwable)}
	exception_stack_trace text NOT NULL, -- 异常的栈轨迹¶     *¶     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getServiceException(Exception)}
	exception_class_name varchar(512) NOT NULL, -- 异常发生的类全名¶     *¶     * {@link StackTraceElement#getClassName()}
	exception_file_name varchar(512) NOT NULL, -- 异常发生的类文件¶     *¶     * {@link StackTraceElement#getFileName()}
	exception_method_name varchar(512) NOT NULL, -- 异常发生的方法名¶     *¶     * {@link StackTraceElement#getMethodName()}
	exception_line_number int4 NOT NULL, -- 异常发生的方法所在行¶     *¶     * {@link StackTraceElement#getLineNumber()}
	process_status int2 NOT NULL, -- 处理状态
	process_time timestamp NULL, -- 处理时间
	process_user_id int4 DEFAULT 0 NULL, -- 处理用户编号
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户编号
	CONSTRAINT pk_infra_api_error_log PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_api_error_log IS '系统异常日志';

-- Column comments

COMMENT ON COLUMN public.infra_api_error_log.id IS '编号';
COMMENT ON COLUMN public.infra_api_error_log.trace_id IS '链路追踪编号
     *
     * 一般来说，通过链路追踪编号，可以将访问日志，错误日志，链路追踪日志，logger 打印日志等，结合在一起，从而进行排错。';
COMMENT ON COLUMN public.infra_api_error_log.user_id IS '用户编号';
COMMENT ON COLUMN public.infra_api_error_log.user_type IS '用户类型';
COMMENT ON COLUMN public.infra_api_error_log.application_name IS '应用名
     *
     * 目前读取 spring.application.name';
COMMENT ON COLUMN public.infra_api_error_log.request_method IS '请求方法名';
COMMENT ON COLUMN public.infra_api_error_log.request_url IS '请求地址';
COMMENT ON COLUMN public.infra_api_error_log.request_params IS '请求参数';
COMMENT ON COLUMN public.infra_api_error_log.user_ip IS '用户 IP';
COMMENT ON COLUMN public.infra_api_error_log.user_agent IS '浏览器 UA';
COMMENT ON COLUMN public.infra_api_error_log.exception_time IS '异常发生时间';
COMMENT ON COLUMN public.infra_api_error_log.exception_name IS '异常名
     *
     * {@link Throwable#getClass()} 的类全名';
COMMENT ON COLUMN public.infra_api_error_log.exception_message IS '异常导致的消息
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getMessage(Throwable)}';
COMMENT ON COLUMN public.infra_api_error_log.exception_root_cause_message IS '异常导致的根消息
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getRootCauseMessage(Throwable)}';
COMMENT ON COLUMN public.infra_api_error_log.exception_stack_trace IS '异常的栈轨迹
     *
     * {@link cn.iocoder.common.framework.util.ExceptionUtil#getServiceException(Exception)}';
COMMENT ON COLUMN public.infra_api_error_log.exception_class_name IS '异常发生的类全名
     *
     * {@link StackTraceElement#getClassName()}';
COMMENT ON COLUMN public.infra_api_error_log.exception_file_name IS '异常发生的类文件
     *
     * {@link StackTraceElement#getFileName()}';
COMMENT ON COLUMN public.infra_api_error_log.exception_method_name IS '异常发生的方法名
     *
     * {@link StackTraceElement#getMethodName()}';
COMMENT ON COLUMN public.infra_api_error_log.exception_line_number IS '异常发生的方法所在行
     *
     * {@link StackTraceElement#getLineNumber()}';
COMMENT ON COLUMN public.infra_api_error_log.process_status IS '处理状态';
COMMENT ON COLUMN public.infra_api_error_log.process_time IS '处理时间';
COMMENT ON COLUMN public.infra_api_error_log.process_user_id IS '处理用户编号';
COMMENT ON COLUMN public.infra_api_error_log.creator IS '创建者';
COMMENT ON COLUMN public.infra_api_error_log.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_api_error_log.updater IS '更新者';
COMMENT ON COLUMN public.infra_api_error_log.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_api_error_log.deleted IS '是否删除';
COMMENT ON COLUMN public.infra_api_error_log.tenant_id IS '租户编号';


-- public.infra_config definition

-- Drop table

-- DROP TABLE public.infra_config;

CREATE TABLE public.infra_config (
	id int4 NOT NULL, -- 参数主键
	category varchar(50) NOT NULL, -- 参数分组
	"type" int2 NOT NULL, -- 参数类型
	"name" varchar(100) DEFAULT ''::character varying NOT NULL, -- 参数名称
	config_key varchar(100) DEFAULT ''::character varying NOT NULL, -- 参数键名
	value varchar(500) DEFAULT ''::character varying NOT NULL, -- 参数键值
	visible bool NOT NULL, -- 是否可见
	remark varchar(500) DEFAULT NULL::character varying NULL, -- 备注
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_config PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_config IS '参数配置表';

-- Column comments

COMMENT ON COLUMN public.infra_config.id IS '参数主键';
COMMENT ON COLUMN public.infra_config.category IS '参数分组';
COMMENT ON COLUMN public.infra_config."type" IS '参数类型';
COMMENT ON COLUMN public.infra_config."name" IS '参数名称';
COMMENT ON COLUMN public.infra_config.config_key IS '参数键名';
COMMENT ON COLUMN public.infra_config.value IS '参数键值';
COMMENT ON COLUMN public.infra_config.visible IS '是否可见';
COMMENT ON COLUMN public.infra_config.remark IS '备注';
COMMENT ON COLUMN public.infra_config.creator IS '创建者';
COMMENT ON COLUMN public.infra_config.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_config.updater IS '更新者';
COMMENT ON COLUMN public.infra_config.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_config.deleted IS '是否删除';


-- public.infra_data_source_config definition

-- Drop table

-- DROP TABLE public.infra_data_source_config;

CREATE TABLE public.infra_data_source_config (
	id int8 NOT NULL, -- 主键编号
	"name" varchar(100) DEFAULT ''::character varying NOT NULL, -- 参数名称
	url varchar(1024) NOT NULL, -- 数据源连接
	username varchar(255) NOT NULL, -- 用户名
	"password" varchar(255) DEFAULT ''::character varying NOT NULL, -- 密码
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_data_source_config PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_data_source_config IS '数据源配置表';

-- Column comments

COMMENT ON COLUMN public.infra_data_source_config.id IS '主键编号';
COMMENT ON COLUMN public.infra_data_source_config."name" IS '参数名称';
COMMENT ON COLUMN public.infra_data_source_config.url IS '数据源连接';
COMMENT ON COLUMN public.infra_data_source_config.username IS '用户名';
COMMENT ON COLUMN public.infra_data_source_config."password" IS '密码';
COMMENT ON COLUMN public.infra_data_source_config.creator IS '创建者';
COMMENT ON COLUMN public.infra_data_source_config.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_data_source_config.updater IS '更新者';
COMMENT ON COLUMN public.infra_data_source_config.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_data_source_config.deleted IS '是否删除';


-- public.infra_file definition

-- Drop table

-- DROP TABLE public.infra_file;

CREATE TABLE public.infra_file (
	id int8 NOT NULL, -- 文件编号
	config_id int8 NULL, -- 配置编号
	"name" varchar(256) DEFAULT NULL::character varying NULL, -- 文件名
	"path" varchar(512) NOT NULL, -- 文件路径
	url varchar(1024) NOT NULL, -- 文件 URL
	"type" varchar(128) DEFAULT NULL::character varying NULL, -- 文件类型
	"size" int4 NOT NULL, -- 文件大小
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_file PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_file IS '文件表';

-- Column comments

COMMENT ON COLUMN public.infra_file.id IS '文件编号';
COMMENT ON COLUMN public.infra_file.config_id IS '配置编号';
COMMENT ON COLUMN public.infra_file."name" IS '文件名';
COMMENT ON COLUMN public.infra_file."path" IS '文件路径';
COMMENT ON COLUMN public.infra_file.url IS '文件 URL';
COMMENT ON COLUMN public.infra_file."type" IS '文件类型';
COMMENT ON COLUMN public.infra_file."size" IS '文件大小';
COMMENT ON COLUMN public.infra_file.creator IS '创建者';
COMMENT ON COLUMN public.infra_file.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_file.updater IS '更新者';
COMMENT ON COLUMN public.infra_file.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_file.deleted IS '是否删除';


-- public.infra_file_config definition

-- Drop table

-- DROP TABLE public.infra_file_config;

CREATE TABLE public.infra_file_config (
	id int8 NOT NULL, -- 编号
	"name" varchar(63) NOT NULL, -- 配置名
	"storage" int2 NOT NULL, -- 存储器
	remark varchar(255) DEFAULT NULL::character varying NULL, -- 备注
	master bool NOT NULL, -- 是否为主配置
	config json NOT NULL, -- 存储配置
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_file_config PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_file_config IS '文件配置表';

-- Column comments

COMMENT ON COLUMN public.infra_file_config.id IS '编号';
COMMENT ON COLUMN public.infra_file_config."name" IS '配置名';
COMMENT ON COLUMN public.infra_file_config."storage" IS '存储器';
COMMENT ON COLUMN public.infra_file_config.remark IS '备注';
COMMENT ON COLUMN public.infra_file_config.master IS '是否为主配置';
COMMENT ON COLUMN public.infra_file_config.config IS '存储配置';
COMMENT ON COLUMN public.infra_file_config.creator IS '创建者';
COMMENT ON COLUMN public.infra_file_config.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_file_config.updater IS '更新者';
COMMENT ON COLUMN public.infra_file_config.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_file_config.deleted IS '是否删除';


-- public.infra_file_content definition

-- Drop table

-- DROP TABLE public.infra_file_content;

CREATE TABLE public.infra_file_content (
	id int8 NOT NULL, -- 编号
	config_id int8 NOT NULL, -- 配置编号
	"path" varchar(512) NOT NULL, -- 文件路径
	"content" bytea NOT NULL, -- 文件内容
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_file_content PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_file_content IS '文件表';

-- Column comments

COMMENT ON COLUMN public.infra_file_content.id IS '编号';
COMMENT ON COLUMN public.infra_file_content.config_id IS '配置编号';
COMMENT ON COLUMN public.infra_file_content."path" IS '文件路径';
COMMENT ON COLUMN public.infra_file_content."content" IS '文件内容';
COMMENT ON COLUMN public.infra_file_content.creator IS '创建者';
COMMENT ON COLUMN public.infra_file_content.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_file_content.updater IS '更新者';
COMMENT ON COLUMN public.infra_file_content.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_file_content.deleted IS '是否删除';


-- public.infra_job definition

-- Drop table

-- DROP TABLE public.infra_job;

CREATE TABLE public.infra_job (
	id int8 NOT NULL, -- 任务编号
	"name" varchar(32) NOT NULL, -- 任务名称
	status int2 NOT NULL, -- 任务状态
	handler_name varchar(64) NOT NULL, -- 处理器的名字
	handler_param varchar(255) DEFAULT NULL::character varying NULL, -- 处理器的参数
	cron_expression varchar(32) NOT NULL, -- CRON 表达式
	retry_count int4 DEFAULT 0 NOT NULL, -- 重试次数
	retry_interval int4 DEFAULT 0 NOT NULL, -- 重试间隔
	monitor_timeout int4 DEFAULT 0 NOT NULL, -- 监控超时时间
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_job PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_job IS '定时任务表';

-- Column comments

COMMENT ON COLUMN public.infra_job.id IS '任务编号';
COMMENT ON COLUMN public.infra_job."name" IS '任务名称';
COMMENT ON COLUMN public.infra_job.status IS '任务状态';
COMMENT ON COLUMN public.infra_job.handler_name IS '处理器的名字';
COMMENT ON COLUMN public.infra_job.handler_param IS '处理器的参数';
COMMENT ON COLUMN public.infra_job.cron_expression IS 'CRON 表达式';
COMMENT ON COLUMN public.infra_job.retry_count IS '重试次数';
COMMENT ON COLUMN public.infra_job.retry_interval IS '重试间隔';
COMMENT ON COLUMN public.infra_job.monitor_timeout IS '监控超时时间';
COMMENT ON COLUMN public.infra_job.creator IS '创建者';
COMMENT ON COLUMN public.infra_job.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_job.updater IS '更新者';
COMMENT ON COLUMN public.infra_job.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_job.deleted IS '是否删除';


-- public.infra_job_log definition

-- Drop table

-- DROP TABLE public.infra_job_log;

CREATE TABLE public.infra_job_log (
	id int8 NOT NULL, -- 日志编号
	job_id int8 NOT NULL, -- 任务编号
	handler_name varchar(64) NOT NULL, -- 处理器的名字
	handler_param varchar(255) DEFAULT NULL::character varying NULL, -- 处理器的参数
	execute_index int2 DEFAULT 1 NOT NULL, -- 第几次执行
	begin_time timestamp NOT NULL, -- 开始执行时间
	end_time timestamp NULL, -- 结束执行时间
	duration int4 NULL, -- 执行时长
	status int2 NOT NULL, -- 任务状态
	"result" varchar(4000) DEFAULT ''::character varying NULL, -- 结果数据
	creator int8 DEFAULT 0 NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 DEFAULT 0 NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 是否删除
	CONSTRAINT pk_infra_job_log PRIMARY KEY (id)
);
COMMENT ON TABLE public.infra_job_log IS '定时任务日志表';

-- Column comments

COMMENT ON COLUMN public.infra_job_log.id IS '日志编号';
COMMENT ON COLUMN public.infra_job_log.job_id IS '任务编号';
COMMENT ON COLUMN public.infra_job_log.handler_name IS '处理器的名字';
COMMENT ON COLUMN public.infra_job_log.handler_param IS '处理器的参数';
COMMENT ON COLUMN public.infra_job_log.execute_index IS '第几次执行';
COMMENT ON COLUMN public.infra_job_log.begin_time IS '开始执行时间';
COMMENT ON COLUMN public.infra_job_log.end_time IS '结束执行时间';
COMMENT ON COLUMN public.infra_job_log.duration IS '执行时长';
COMMENT ON COLUMN public.infra_job_log.status IS '任务状态';
COMMENT ON COLUMN public.infra_job_log."result" IS '结果数据';
COMMENT ON COLUMN public.infra_job_log.creator IS '创建者';
COMMENT ON COLUMN public.infra_job_log.create_time IS '创建时间';
COMMENT ON COLUMN public.infra_job_log.updater IS '更新者';
COMMENT ON COLUMN public.infra_job_log.update_time IS '更新时间';
COMMENT ON COLUMN public.infra_job_log.deleted IS '是否删除';