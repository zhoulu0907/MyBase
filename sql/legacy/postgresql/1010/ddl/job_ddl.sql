-- public.sj_distributed_lock definition

-- Drop table

-- DROP TABLE public.sj_distributed_lock;

CREATE TABLE public.sj_distributed_lock (
	"name" varchar(64) NOT NULL, -- 锁名称
	lock_until timestamp(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL, -- 锁定时长
	locked_at timestamp(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL, -- 锁定时间
	locked_by varchar(255) NOT NULL, -- 锁定者
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_distributed_lock_pkey PRIMARY KEY (name)
);
COMMENT ON TABLE public.sj_distributed_lock IS '锁定表';

-- Column comments

COMMENT ON COLUMN public.sj_distributed_lock."name" IS '锁名称';
COMMENT ON COLUMN public.sj_distributed_lock.lock_until IS '锁定时长';
COMMENT ON COLUMN public.sj_distributed_lock.locked_at IS '锁定时间';
COMMENT ON COLUMN public.sj_distributed_lock.locked_by IS '锁定者';
COMMENT ON COLUMN public.sj_distributed_lock.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_distributed_lock.update_dt IS '修改时间';


-- public.sj_group_config definition

-- Drop table

-- DROP TABLE public.sj_group_config;

CREATE TABLE public.sj_group_config (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) DEFAULT ''::character varying NOT NULL, -- 组名称
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 组描述
	"token" varchar(64) DEFAULT 'SJ_cKqBTPzCsWA3VyuCfFoccmuIEGXjr5KT'::character varying NOT NULL, -- token
	group_status int2 DEFAULT 0 NOT NULL, -- 组状态 0、未启用 1、启用
	"version" int4 NOT NULL, -- 版本号
	group_partition int4 NOT NULL, -- 分区
	id_generator_mode int2 DEFAULT 1 NOT NULL, -- 唯一id生成模式 默认号段模式
	init_scene int2 DEFAULT 0 NOT NULL, -- 是否初始化场景 0:否 1:是
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_group_config_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uk_sj_group_config_01 ON public.sj_group_config USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_group_config IS '组配置';

-- Column comments

COMMENT ON COLUMN public.sj_group_config.id IS '主键';
COMMENT ON COLUMN public.sj_group_config.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_group_config.group_name IS '组名称';
COMMENT ON COLUMN public.sj_group_config.description IS '组描述';
COMMENT ON COLUMN public.sj_group_config."token" IS 'token';
COMMENT ON COLUMN public.sj_group_config.group_status IS '组状态 0、未启用 1、启用';
COMMENT ON COLUMN public.sj_group_config."version" IS '版本号';
COMMENT ON COLUMN public.sj_group_config.group_partition IS '分区';
COMMENT ON COLUMN public.sj_group_config.id_generator_mode IS '唯一id生成模式 默认号段模式';
COMMENT ON COLUMN public.sj_group_config.init_scene IS '是否初始化场景 0:否 1:是';
COMMENT ON COLUMN public.sj_group_config.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_group_config.update_dt IS '修改时间';


-- public.sj_job definition

-- Drop table

-- DROP TABLE public.sj_job;

CREATE TABLE public.sj_job (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	job_name varchar(64) NOT NULL, -- 名称
	args_str text NULL, -- 执行方法参数
	args_type int2 DEFAULT 1 NOT NULL, -- 参数类型
	next_trigger_at int8 NOT NULL, -- 下次触发时间
	job_status int2 DEFAULT 1 NOT NULL, -- 任务状态 0、关闭、1、开启
	task_type int2 DEFAULT 1 NOT NULL, -- 任务类型 1、集群 2、广播 3、切片
	route_key int2 DEFAULT 4 NOT NULL, -- 路由策略
	executor_type int2 DEFAULT 1 NOT NULL, -- 执行器类型
	executor_info varchar(255) DEFAULT NULL::character varying NULL, -- 执行器名称
	trigger_type int2 NOT NULL, -- 触发类型 1.CRON 表达式 2. 固定时间
	trigger_interval varchar(255) NOT NULL, -- 间隔时长
	block_strategy int2 DEFAULT 1 NOT NULL, -- 阻塞策略 1、丢弃 2、覆盖 3、并行 4、恢复
	executor_timeout int4 DEFAULT 0 NOT NULL, -- 任务执行超时时间，单位秒
	max_retry_times int4 DEFAULT 0 NOT NULL, -- 最大重试次数
	parallel_num int4 DEFAULT 1 NOT NULL, -- 并行数
	retry_interval int4 DEFAULT 0 NOT NULL, -- 重试间隔 ( s)
	bucket_index int4 DEFAULT 0 NOT NULL, -- bucket
	resident int2 DEFAULT 0 NOT NULL, -- 是否是常驻任务
	notify_ids varchar(128) DEFAULT ''::character varying NOT NULL, -- 通知告警场景配置id列表
	owner_id int8 NULL, -- 负责人id
	labels varchar(512) DEFAULT ''::character varying NULL, -- 标签
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 描述
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	deleted int2 DEFAULT 0 NOT NULL, -- 逻辑删除 1、删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_job_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_job_01 ON public.sj_job USING btree (namespace_id, group_name);
CREATE INDEX idx_sj_job_02 ON public.sj_job USING btree (job_status, bucket_index);
CREATE INDEX idx_sj_job_03 ON public.sj_job USING btree (create_dt);
COMMENT ON TABLE public.sj_job IS '任务信息';

-- Column comments

COMMENT ON COLUMN public.sj_job.id IS '主键';
COMMENT ON COLUMN public.sj_job.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_job.group_name IS '组名称';
COMMENT ON COLUMN public.sj_job.job_name IS '名称';
COMMENT ON COLUMN public.sj_job.args_str IS '执行方法参数';
COMMENT ON COLUMN public.sj_job.args_type IS '参数类型 ';
COMMENT ON COLUMN public.sj_job.next_trigger_at IS '下次触发时间';
COMMENT ON COLUMN public.sj_job.job_status IS '任务状态 0、关闭、1、开启';
COMMENT ON COLUMN public.sj_job.task_type IS '任务类型 1、集群 2、广播 3、切片';
COMMENT ON COLUMN public.sj_job.route_key IS '路由策略';
COMMENT ON COLUMN public.sj_job.executor_type IS '执行器类型';
COMMENT ON COLUMN public.sj_job.executor_info IS '执行器名称';
COMMENT ON COLUMN public.sj_job.trigger_type IS '触发类型 1.CRON 表达式 2. 固定时间';
COMMENT ON COLUMN public.sj_job.trigger_interval IS '间隔时长';
COMMENT ON COLUMN public.sj_job.block_strategy IS '阻塞策略 1、丢弃 2、覆盖 3、并行 4、恢复';
COMMENT ON COLUMN public.sj_job.executor_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN public.sj_job.max_retry_times IS '最大重试次数';
COMMENT ON COLUMN public.sj_job.parallel_num IS '并行数';
COMMENT ON COLUMN public.sj_job.retry_interval IS '重试间隔 ( s)';
COMMENT ON COLUMN public.sj_job.bucket_index IS 'bucket';
COMMENT ON COLUMN public.sj_job.resident IS '是否是常驻任务';
COMMENT ON COLUMN public.sj_job.notify_ids IS '通知告警场景配置id列表';
COMMENT ON COLUMN public.sj_job.owner_id IS '负责人id';
COMMENT ON COLUMN public.sj_job.labels IS '标签';
COMMENT ON COLUMN public.sj_job.description IS '描述';
COMMENT ON COLUMN public.sj_job.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_job.deleted IS '逻辑删除 1、删除';
COMMENT ON COLUMN public.sj_job.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_job.update_dt IS '修改时间';


-- public.sj_job_executor definition

-- Drop table

-- DROP TABLE public.sj_job_executor;

CREATE TABLE public.sj_job_executor (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	executor_info varchar(256) NOT NULL, -- 任务执行器名称
	executor_type varchar(3) NOT NULL, -- 1:java 2:python 3:go
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_job_executor_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_job_executor_01 ON public.sj_job_executor USING btree (namespace_id, group_name);
CREATE INDEX idx_sj_job_executor_02 ON public.sj_job_executor USING btree (create_dt);
COMMENT ON TABLE public.sj_job_executor IS '任务执行器信息';

-- Column comments

COMMENT ON COLUMN public.sj_job_executor.id IS '主键';
COMMENT ON COLUMN public.sj_job_executor.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_job_executor.group_name IS '组名称';
COMMENT ON COLUMN public.sj_job_executor.executor_info IS '任务执行器名称';
COMMENT ON COLUMN public.sj_job_executor.executor_type IS '1:java 2:python 3:go';
COMMENT ON COLUMN public.sj_job_executor.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_job_executor.update_dt IS '修改时间';


-- public.sj_job_log_message definition

-- Drop table

-- DROP TABLE public.sj_job_log_message;

CREATE TABLE public.sj_job_log_message (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	job_id int8 NOT NULL, -- 任务信息id
	task_batch_id int8 NOT NULL, -- 任务批次id
	task_id int8 NOT NULL, -- 调度任务id
	message text NOT NULL, -- 调度信息
	log_num int4 DEFAULT 1 NOT NULL, -- 日志数量
	real_time int8 DEFAULT 0 NOT NULL, -- 上报时间
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	CONSTRAINT sj_job_log_message_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_job_log_message_01 ON public.sj_job_log_message USING btree (task_batch_id, task_id);
CREATE INDEX idx_sj_job_log_message_02 ON public.sj_job_log_message USING btree (create_dt);
CREATE INDEX idx_sj_job_log_message_03 ON public.sj_job_log_message USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_job_log_message IS '调度日志';

-- Column comments

COMMENT ON COLUMN public.sj_job_log_message.id IS '主键';
COMMENT ON COLUMN public.sj_job_log_message.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_job_log_message.group_name IS '组名称';
COMMENT ON COLUMN public.sj_job_log_message.job_id IS '任务信息id';
COMMENT ON COLUMN public.sj_job_log_message.task_batch_id IS '任务批次id';
COMMENT ON COLUMN public.sj_job_log_message.task_id IS '调度任务id';
COMMENT ON COLUMN public.sj_job_log_message.message IS '调度信息';
COMMENT ON COLUMN public.sj_job_log_message.log_num IS '日志数量';
COMMENT ON COLUMN public.sj_job_log_message.real_time IS '上报时间';
COMMENT ON COLUMN public.sj_job_log_message.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_job_log_message.create_dt IS '创建时间';


-- public.sj_job_summary definition

-- Drop table

-- DROP TABLE public.sj_job_summary;

CREATE TABLE public.sj_job_summary (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) DEFAULT ''::character varying NOT NULL, -- 组名称
	business_id int8 NOT NULL, -- 业务id  ( job_id或workflow_id)
	system_task_type int2 DEFAULT 3 NOT NULL, -- 任务类型 3、JOB任务 4、WORKFLOW任务
	trigger_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 统计时间
	success_num int4 DEFAULT 0 NOT NULL, -- 执行成功-日志数量
	fail_num int4 DEFAULT 0 NOT NULL, -- 执行失败-日志数量
	fail_reason varchar(512) DEFAULT ''::character varying NOT NULL, -- 失败原因
	stop_num int4 DEFAULT 0 NOT NULL, -- 执行失败-日志数量
	stop_reason varchar(512) DEFAULT ''::character varying NOT NULL, -- 失败原因
	cancel_num int4 DEFAULT 0 NOT NULL, -- 执行失败-日志数量
	cancel_reason varchar(512) DEFAULT ''::character varying NOT NULL, -- 失败原因
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_job_summary_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_job_summary_01 ON public.sj_job_summary USING btree (namespace_id, group_name, business_id);
CREATE UNIQUE INDEX uk_sj_job_summary_01 ON public.sj_job_summary USING btree (trigger_at, system_task_type, business_id);
COMMENT ON TABLE public.sj_job_summary IS 'DashBoard_Job';

-- Column comments

COMMENT ON COLUMN public.sj_job_summary.id IS '主键';
COMMENT ON COLUMN public.sj_job_summary.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_job_summary.group_name IS '组名称';
COMMENT ON COLUMN public.sj_job_summary.business_id IS '业务id  ( job_id或workflow_id)';
COMMENT ON COLUMN public.sj_job_summary.system_task_type IS '任务类型 3、JOB任务 4、WORKFLOW任务';
COMMENT ON COLUMN public.sj_job_summary.trigger_at IS '统计时间';
COMMENT ON COLUMN public.sj_job_summary.success_num IS '执行成功-日志数量';
COMMENT ON COLUMN public.sj_job_summary.fail_num IS '执行失败-日志数量';
COMMENT ON COLUMN public.sj_job_summary.fail_reason IS '失败原因';
COMMENT ON COLUMN public.sj_job_summary.stop_num IS '执行失败-日志数量';
COMMENT ON COLUMN public.sj_job_summary.stop_reason IS '失败原因';
COMMENT ON COLUMN public.sj_job_summary.cancel_num IS '执行失败-日志数量';
COMMENT ON COLUMN public.sj_job_summary.cancel_reason IS '失败原因';
COMMENT ON COLUMN public.sj_job_summary.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_job_summary.update_dt IS '修改时间';


-- public.sj_job_task definition

-- Drop table

-- DROP TABLE public.sj_job_task;

CREATE TABLE public.sj_job_task (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	job_id int8 NOT NULL, -- 任务信息id
	task_batch_id int8 NOT NULL, -- 调度任务id
	parent_id int8 DEFAULT 0 NOT NULL, -- 父执行器id
	task_status int2 DEFAULT 0 NOT NULL, -- 执行的状态 0、失败 1、成功
	retry_count int4 DEFAULT 0 NOT NULL, -- 重试次数
	mr_stage int2 NULL, -- 动态分片所处阶段 1:map 2:reduce 3:mergeReduce
	leaf int2 DEFAULT '1'::smallint NOT NULL, -- 叶子节点
	task_name varchar(255) DEFAULT ''::character varying NOT NULL, -- 任务名称
	client_info varchar(128) DEFAULT NULL::character varying NULL, -- 客户端地址 clientId#ip:port
	wf_context text NULL, -- 工作流全局上下文
	result_message text NOT NULL, -- 执行结果
	args_str text NULL, -- 执行方法参数
	args_type int2 DEFAULT 1 NOT NULL, -- 参数类型
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_job_task_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_job_task_01 ON public.sj_job_task USING btree (task_batch_id, task_status);
CREATE INDEX idx_sj_job_task_02 ON public.sj_job_task USING btree (create_dt);
CREATE INDEX idx_sj_job_task_03 ON public.sj_job_task USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_job_task IS '任务实例';

-- Column comments

COMMENT ON COLUMN public.sj_job_task.id IS '主键';
COMMENT ON COLUMN public.sj_job_task.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_job_task.group_name IS '组名称';
COMMENT ON COLUMN public.sj_job_task.job_id IS '任务信息id';
COMMENT ON COLUMN public.sj_job_task.task_batch_id IS '调度任务id';
COMMENT ON COLUMN public.sj_job_task.parent_id IS '父执行器id';
COMMENT ON COLUMN public.sj_job_task.task_status IS '执行的状态 0、失败 1、成功';
COMMENT ON COLUMN public.sj_job_task.retry_count IS '重试次数';
COMMENT ON COLUMN public.sj_job_task.mr_stage IS '动态分片所处阶段 1:map 2:reduce 3:mergeReduce';
COMMENT ON COLUMN public.sj_job_task.leaf IS '叶子节点';
COMMENT ON COLUMN public.sj_job_task.task_name IS '任务名称';
COMMENT ON COLUMN public.sj_job_task.client_info IS '客户端地址 clientId#ip:port';
COMMENT ON COLUMN public.sj_job_task.wf_context IS '工作流全局上下文';
COMMENT ON COLUMN public.sj_job_task.result_message IS '执行结果';
COMMENT ON COLUMN public.sj_job_task.args_str IS '执行方法参数';
COMMENT ON COLUMN public.sj_job_task.args_type IS '参数类型 ';
COMMENT ON COLUMN public.sj_job_task.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_job_task.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_job_task.update_dt IS '修改时间';


-- public.sj_job_task_batch definition

-- Drop table

-- DROP TABLE public.sj_job_task_batch;

CREATE TABLE public.sj_job_task_batch (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	job_id int8 NOT NULL, -- 任务id
	workflow_node_id int8 DEFAULT 0 NOT NULL, -- 工作流节点id
	parent_workflow_node_id int8 DEFAULT 0 NOT NULL, -- 工作流任务父批次id
	workflow_task_batch_id int8 DEFAULT 0 NOT NULL, -- 工作流任务批次id
	task_batch_status int2 DEFAULT 0 NOT NULL, -- 任务批次状态 0、失败 1、成功
	operation_reason int2 DEFAULT 0 NOT NULL, -- 操作原因
	execution_at int8 DEFAULT 0 NOT NULL, -- 任务执行时间
	system_task_type int2 DEFAULT 3 NOT NULL, -- 任务类型 3、JOB任务 4、WORKFLOW任务
	parent_id varchar(64) DEFAULT ''::character varying NOT NULL, -- 父节点
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	deleted int2 DEFAULT 0 NOT NULL, -- 逻辑删除 1、删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_job_task_batch_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_job_task_batch_01 ON public.sj_job_task_batch USING btree (job_id, task_batch_status);
CREATE INDEX idx_sj_job_task_batch_02 ON public.sj_job_task_batch USING btree (create_dt);
CREATE INDEX idx_sj_job_task_batch_03 ON public.sj_job_task_batch USING btree (namespace_id, group_name);
CREATE INDEX idx_sj_job_task_batch_04 ON public.sj_job_task_batch USING btree (workflow_task_batch_id, workflow_node_id);
COMMENT ON TABLE public.sj_job_task_batch IS '任务批次';

-- Column comments

COMMENT ON COLUMN public.sj_job_task_batch.id IS '主键';
COMMENT ON COLUMN public.sj_job_task_batch.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_job_task_batch.group_name IS '组名称';
COMMENT ON COLUMN public.sj_job_task_batch.job_id IS '任务id';
COMMENT ON COLUMN public.sj_job_task_batch.workflow_node_id IS '工作流节点id';
COMMENT ON COLUMN public.sj_job_task_batch.parent_workflow_node_id IS '工作流任务父批次id';
COMMENT ON COLUMN public.sj_job_task_batch.workflow_task_batch_id IS '工作流任务批次id';
COMMENT ON COLUMN public.sj_job_task_batch.task_batch_status IS '任务批次状态 0、失败 1、成功';
COMMENT ON COLUMN public.sj_job_task_batch.operation_reason IS '操作原因';
COMMENT ON COLUMN public.sj_job_task_batch.execution_at IS '任务执行时间';
COMMENT ON COLUMN public.sj_job_task_batch.system_task_type IS '任务类型 3、JOB任务 4、WORKFLOW任务';
COMMENT ON COLUMN public.sj_job_task_batch.parent_id IS '父节点';
COMMENT ON COLUMN public.sj_job_task_batch.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_job_task_batch.deleted IS '逻辑删除 1、删除';
COMMENT ON COLUMN public.sj_job_task_batch.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_job_task_batch.update_dt IS '修改时间';


-- public.sj_namespace definition

-- Drop table

-- DROP TABLE public.sj_namespace;

CREATE TABLE public.sj_namespace (
	id bigserial NOT NULL, -- 主键
	"name" varchar(64) NOT NULL, -- 名称
	unique_id varchar(64) NOT NULL, -- 唯一id
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 描述
	deleted int2 DEFAULT 0 NOT NULL, -- 逻辑删除 1、删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_namespace_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_namespace_01 ON public.sj_namespace USING btree (name);
COMMENT ON TABLE public.sj_namespace IS '命名空间';

-- Column comments

COMMENT ON COLUMN public.sj_namespace.id IS '主键';
COMMENT ON COLUMN public.sj_namespace."name" IS '名称';
COMMENT ON COLUMN public.sj_namespace.unique_id IS '唯一id';
COMMENT ON COLUMN public.sj_namespace.description IS '描述';
COMMENT ON COLUMN public.sj_namespace.deleted IS '逻辑删除 1、删除';
COMMENT ON COLUMN public.sj_namespace.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_namespace.update_dt IS '修改时间';


-- public.sj_notify_config definition

-- Drop table

-- DROP TABLE public.sj_notify_config;

CREATE TABLE public.sj_notify_config (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	notify_name varchar(64) DEFAULT ''::character varying NOT NULL, -- 通知名称
	system_task_type int2 DEFAULT 3 NOT NULL, -- 任务类型 1. 重试任务 2. 重试回调 3、JOB任务 4、WORKFLOW任务
	notify_status int2 DEFAULT 0 NOT NULL, -- 通知状态 0、未启用 1、启用
	recipient_ids varchar(128) NOT NULL, -- 接收人id列表
	notify_threshold int4 DEFAULT 0 NOT NULL, -- 通知阈值
	notify_scene int2 DEFAULT 0 NOT NULL, -- 通知场景
	rate_limiter_status int2 DEFAULT 0 NOT NULL, -- 限流状态 0、未启用 1、启用
	rate_limiter_threshold int4 DEFAULT 0 NOT NULL, -- 每秒限流阈值
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 描述
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_notify_config_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_notify_config_01 ON public.sj_notify_config USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_notify_config IS '通知配置';

-- Column comments

COMMENT ON COLUMN public.sj_notify_config.id IS '主键';
COMMENT ON COLUMN public.sj_notify_config.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_notify_config.group_name IS '组名称';
COMMENT ON COLUMN public.sj_notify_config.notify_name IS '通知名称';
COMMENT ON COLUMN public.sj_notify_config.system_task_type IS '任务类型 1. 重试任务 2. 重试回调 3、JOB任务 4、WORKFLOW任务';
COMMENT ON COLUMN public.sj_notify_config.notify_status IS '通知状态 0、未启用 1、启用';
COMMENT ON COLUMN public.sj_notify_config.recipient_ids IS '接收人id列表';
COMMENT ON COLUMN public.sj_notify_config.notify_threshold IS '通知阈值';
COMMENT ON COLUMN public.sj_notify_config.notify_scene IS '通知场景';
COMMENT ON COLUMN public.sj_notify_config.rate_limiter_status IS '限流状态 0、未启用 1、启用';
COMMENT ON COLUMN public.sj_notify_config.rate_limiter_threshold IS '每秒限流阈值';
COMMENT ON COLUMN public.sj_notify_config.description IS '描述';
COMMENT ON COLUMN public.sj_notify_config.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_notify_config.update_dt IS '修改时间';


-- public.sj_notify_recipient definition

-- Drop table

-- DROP TABLE public.sj_notify_recipient;

CREATE TABLE public.sj_notify_recipient (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	recipient_name varchar(64) NOT NULL, -- 接收人名称
	notify_type int2 DEFAULT 0 NOT NULL, -- 通知类型 1、钉钉 2、邮件 3、企业微信 4 飞书 5 webhook
	notify_attribute varchar(512) NOT NULL, -- 配置属性
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 描述
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_notify_recipient_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_notify_recipient_01 ON public.sj_notify_recipient USING btree (namespace_id);
COMMENT ON TABLE public.sj_notify_recipient IS '告警通知接收人';

-- Column comments

COMMENT ON COLUMN public.sj_notify_recipient.id IS '主键';
COMMENT ON COLUMN public.sj_notify_recipient.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_notify_recipient.recipient_name IS '接收人名称';
COMMENT ON COLUMN public.sj_notify_recipient.notify_type IS '通知类型 1、钉钉 2、邮件 3、企业微信 4 飞书 5 webhook';
COMMENT ON COLUMN public.sj_notify_recipient.notify_attribute IS '配置属性';
COMMENT ON COLUMN public.sj_notify_recipient.description IS '描述';
COMMENT ON COLUMN public.sj_notify_recipient.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_notify_recipient.update_dt IS '修改时间';


-- public.sj_retry definition

-- Drop table

-- DROP TABLE public.sj_retry;

CREATE TABLE public.sj_retry (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	group_id int8 NOT NULL, -- 组Id
	scene_name varchar(64) NOT NULL, -- 场景名称
	scene_id int8 NOT NULL, -- 场景ID
	idempotent_id varchar(64) NOT NULL, -- 幂等id
	biz_no varchar(64) DEFAULT ''::character varying NOT NULL, -- 业务编号
	executor_name varchar(512) DEFAULT ''::character varying NOT NULL, -- 执行器名称
	args_str text NOT NULL, -- 执行方法参数
	ext_attrs text NOT NULL, -- 扩展字段
	serializer_name varchar(32) DEFAULT 'jackson'::character varying NOT NULL, -- 执行方法参数序列化器名称
	next_trigger_at int8 NOT NULL, -- 下次触发时间
	retry_count int4 DEFAULT 0 NOT NULL, -- 重试次数
	retry_status int2 DEFAULT 0 NOT NULL, -- 重试状态 0、重试中 1、成功 2、最大重试次数
	task_type int2 DEFAULT 1 NOT NULL, -- 任务类型 1、重试数据 2、回调数据
	bucket_index int4 DEFAULT 0 NOT NULL, -- bucket
	parent_id int8 DEFAULT 0 NOT NULL, -- 父节点id
	deleted int8 DEFAULT 0 NOT NULL, -- 逻辑删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_retry_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_retry_01 ON public.sj_retry USING btree (biz_no);
CREATE INDEX idx_sj_retry_02 ON public.sj_retry USING btree (idempotent_id);
CREATE INDEX idx_sj_retry_03 ON public.sj_retry USING btree (retry_status, bucket_index);
CREATE INDEX idx_sj_retry_04 ON public.sj_retry USING btree (parent_id);
CREATE INDEX idx_sj_retry_05 ON public.sj_retry USING btree (create_dt);
CREATE UNIQUE INDEX uk_sj_retry_01 ON public.sj_retry USING btree (scene_id, task_type, idempotent_id, deleted);
COMMENT ON TABLE public.sj_retry IS '重试信息表';

-- Column comments

COMMENT ON COLUMN public.sj_retry.id IS '主键';
COMMENT ON COLUMN public.sj_retry.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_retry.group_name IS '组名称';
COMMENT ON COLUMN public.sj_retry.group_id IS '组Id';
COMMENT ON COLUMN public.sj_retry.scene_name IS '场景名称';
COMMENT ON COLUMN public.sj_retry.scene_id IS '场景ID';
COMMENT ON COLUMN public.sj_retry.idempotent_id IS '幂等id';
COMMENT ON COLUMN public.sj_retry.biz_no IS '业务编号';
COMMENT ON COLUMN public.sj_retry.executor_name IS '执行器名称';
COMMENT ON COLUMN public.sj_retry.args_str IS '执行方法参数';
COMMENT ON COLUMN public.sj_retry.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_retry.serializer_name IS '执行方法参数序列化器名称';
COMMENT ON COLUMN public.sj_retry.next_trigger_at IS '下次触发时间';
COMMENT ON COLUMN public.sj_retry.retry_count IS '重试次数';
COMMENT ON COLUMN public.sj_retry.retry_status IS '重试状态 0、重试中 1、成功 2、最大重试次数';
COMMENT ON COLUMN public.sj_retry.task_type IS '任务类型 1、重试数据 2、回调数据';
COMMENT ON COLUMN public.sj_retry.bucket_index IS 'bucket';
COMMENT ON COLUMN public.sj_retry.parent_id IS '父节点id';
COMMENT ON COLUMN public.sj_retry.deleted IS '逻辑删除';
COMMENT ON COLUMN public.sj_retry.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_retry.update_dt IS '修改时间';


-- public.sj_retry_dead_letter definition

-- Drop table

-- DROP TABLE public.sj_retry_dead_letter;

CREATE TABLE public.sj_retry_dead_letter (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	group_id int8 NOT NULL, -- 组Id
	scene_name varchar(64) NOT NULL, -- 场景名称
	scene_id int8 NOT NULL, -- 场景ID
	idempotent_id varchar(64) NOT NULL, -- 幂等id
	biz_no varchar(64) DEFAULT ''::character varying NOT NULL, -- 业务编号
	executor_name varchar(512) DEFAULT ''::character varying NOT NULL, -- 执行器名称
	serializer_name varchar(32) DEFAULT 'jackson'::character varying NOT NULL, -- 执行方法参数序列化器名称
	args_str text NOT NULL, -- 执行方法参数
	ext_attrs text NOT NULL, -- 扩展字段
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	CONSTRAINT sj_retry_dead_letter_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_retry_dead_letter_01 ON public.sj_retry_dead_letter USING btree (namespace_id, group_name, scene_name);
CREATE INDEX idx_sj_retry_dead_letter_02 ON public.sj_retry_dead_letter USING btree (idempotent_id);
CREATE INDEX idx_sj_retry_dead_letter_03 ON public.sj_retry_dead_letter USING btree (biz_no);
CREATE INDEX idx_sj_retry_dead_letter_04 ON public.sj_retry_dead_letter USING btree (create_dt);
COMMENT ON TABLE public.sj_retry_dead_letter IS '死信队列表';

-- Column comments

COMMENT ON COLUMN public.sj_retry_dead_letter.id IS '主键';
COMMENT ON COLUMN public.sj_retry_dead_letter.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_retry_dead_letter.group_name IS '组名称';
COMMENT ON COLUMN public.sj_retry_dead_letter.group_id IS '组Id';
COMMENT ON COLUMN public.sj_retry_dead_letter.scene_name IS '场景名称';
COMMENT ON COLUMN public.sj_retry_dead_letter.scene_id IS '场景ID';
COMMENT ON COLUMN public.sj_retry_dead_letter.idempotent_id IS '幂等id';
COMMENT ON COLUMN public.sj_retry_dead_letter.biz_no IS '业务编号';
COMMENT ON COLUMN public.sj_retry_dead_letter.executor_name IS '执行器名称';
COMMENT ON COLUMN public.sj_retry_dead_letter.serializer_name IS '执行方法参数序列化器名称';
COMMENT ON COLUMN public.sj_retry_dead_letter.args_str IS '执行方法参数';
COMMENT ON COLUMN public.sj_retry_dead_letter.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_retry_dead_letter.create_dt IS '创建时间';


-- public.sj_retry_scene_config definition

-- Drop table

-- DROP TABLE public.sj_retry_scene_config;

CREATE TABLE public.sj_retry_scene_config (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	scene_name varchar(64) NOT NULL, -- 场景名称
	group_name varchar(64) NOT NULL, -- 组名称
	scene_status int2 DEFAULT 0 NOT NULL, -- 组状态 0、未启用 1、启用
	max_retry_count int4 DEFAULT 5 NOT NULL, -- 最大重试次数
	back_off int2 DEFAULT 1 NOT NULL, -- 1、默认等级 2、固定间隔时间 3、CRON 表达式
	trigger_interval varchar(16) DEFAULT ''::character varying NOT NULL, -- 间隔时长
	notify_ids varchar(128) DEFAULT ''::character varying NOT NULL, -- 通知告警场景配置id列表
	deadline_request int8 DEFAULT 60000 NOT NULL, -- Deadline Request 调用链超时 单位毫秒
	executor_timeout int4 DEFAULT 5 NOT NULL, -- 任务执行超时时间，单位秒
	route_key int2 DEFAULT 4 NOT NULL, -- 路由策略
	block_strategy int2 DEFAULT 1 NOT NULL, -- 阻塞策略 1、丢弃 2、覆盖 3、并行
	cb_status int2 DEFAULT 0 NOT NULL, -- 回调状态 0、不开启 1、开启
	cb_trigger_type int2 DEFAULT 1 NOT NULL, -- 1、默认等级 2、固定间隔时间 3、CRON 表达式
	cb_max_count int4 DEFAULT 16 NOT NULL, -- 回调的最大执行次数
	cb_trigger_interval varchar(16) DEFAULT ''::character varying NOT NULL, -- 回调的最大执行次数
	owner_id int8 NULL, -- 负责人id
	labels varchar(512) DEFAULT ''::character varying NULL, -- 标签
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 描述
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_retry_scene_config_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uk_sj_retry_scene_config_01 ON public.sj_retry_scene_config USING btree (namespace_id, group_name, scene_name);
COMMENT ON TABLE public.sj_retry_scene_config IS '场景配置';

-- Column comments

COMMENT ON COLUMN public.sj_retry_scene_config.id IS '主键';
COMMENT ON COLUMN public.sj_retry_scene_config.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_retry_scene_config.scene_name IS '场景名称';
COMMENT ON COLUMN public.sj_retry_scene_config.group_name IS '组名称';
COMMENT ON COLUMN public.sj_retry_scene_config.scene_status IS '组状态 0、未启用 1、启用';
COMMENT ON COLUMN public.sj_retry_scene_config.max_retry_count IS '最大重试次数';
COMMENT ON COLUMN public.sj_retry_scene_config.back_off IS '1、默认等级 2、固定间隔时间 3、CRON 表达式';
COMMENT ON COLUMN public.sj_retry_scene_config.trigger_interval IS '间隔时长';
COMMENT ON COLUMN public.sj_retry_scene_config.notify_ids IS '通知告警场景配置id列表';
COMMENT ON COLUMN public.sj_retry_scene_config.deadline_request IS 'Deadline Request 调用链超时 单位毫秒';
COMMENT ON COLUMN public.sj_retry_scene_config.executor_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN public.sj_retry_scene_config.route_key IS '路由策略';
COMMENT ON COLUMN public.sj_retry_scene_config.block_strategy IS '阻塞策略 1、丢弃 2、覆盖 3、并行';
COMMENT ON COLUMN public.sj_retry_scene_config.cb_status IS '回调状态 0、不开启 1、开启';
COMMENT ON COLUMN public.sj_retry_scene_config.cb_trigger_type IS '1、默认等级 2、固定间隔时间 3、CRON 表达式';
COMMENT ON COLUMN public.sj_retry_scene_config.cb_max_count IS '回调的最大执行次数';
COMMENT ON COLUMN public.sj_retry_scene_config.cb_trigger_interval IS '回调的最大执行次数';
COMMENT ON COLUMN public.sj_retry_scene_config.owner_id IS '负责人id';
COMMENT ON COLUMN public.sj_retry_scene_config.labels IS '标签';
COMMENT ON COLUMN public.sj_retry_scene_config.description IS '描述';
COMMENT ON COLUMN public.sj_retry_scene_config.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_retry_scene_config.update_dt IS '修改时间';


-- public.sj_retry_summary definition

-- Drop table

-- DROP TABLE public.sj_retry_summary;

CREATE TABLE public.sj_retry_summary (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) DEFAULT ''::character varying NOT NULL, -- 组名称
	scene_name varchar(50) DEFAULT ''::character varying NOT NULL, -- 场景名称
	trigger_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 统计时间
	running_num int4 DEFAULT 0 NOT NULL, -- 重试中-日志数量
	finish_num int4 DEFAULT 0 NOT NULL, -- 重试完成-日志数量
	max_count_num int4 DEFAULT 0 NOT NULL, -- 重试到达最大次数-日志数量
	suspend_num int4 DEFAULT 0 NOT NULL, -- 暂停重试-日志数量
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_retry_summary_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_retry_summary_01 ON public.sj_retry_summary USING btree (trigger_at);
CREATE UNIQUE INDEX uk_sj_retry_summary_01 ON public.sj_retry_summary USING btree (namespace_id, group_name, scene_name, trigger_at);
COMMENT ON TABLE public.sj_retry_summary IS 'DashBoard_Retry';

-- Column comments

COMMENT ON COLUMN public.sj_retry_summary.id IS '主键';
COMMENT ON COLUMN public.sj_retry_summary.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_retry_summary.group_name IS '组名称';
COMMENT ON COLUMN public.sj_retry_summary.scene_name IS '场景名称';
COMMENT ON COLUMN public.sj_retry_summary.trigger_at IS '统计时间';
COMMENT ON COLUMN public.sj_retry_summary.running_num IS '重试中-日志数量';
COMMENT ON COLUMN public.sj_retry_summary.finish_num IS '重试完成-日志数量';
COMMENT ON COLUMN public.sj_retry_summary.max_count_num IS '重试到达最大次数-日志数量';
COMMENT ON COLUMN public.sj_retry_summary.suspend_num IS '暂停重试-日志数量';
COMMENT ON COLUMN public.sj_retry_summary.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_retry_summary.update_dt IS '修改时间';


-- public.sj_retry_task definition

-- Drop table

-- DROP TABLE public.sj_retry_task;

CREATE TABLE public.sj_retry_task (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	scene_name varchar(64) NOT NULL, -- 场景名称
	retry_id int8 NOT NULL, -- 重试信息Id
	ext_attrs text NOT NULL, -- 扩展字段
	task_status int2 DEFAULT 1 NOT NULL, -- 重试状态
	task_type int2 DEFAULT 1 NOT NULL, -- 任务类型 1、重试数据 2、回调数据
	operation_reason int2 DEFAULT 0 NOT NULL, -- 操作原因
	client_info varchar(128) DEFAULT NULL::character varying NULL, -- 客户端地址 clientId#ip:port
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_retry_task_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_retry_task_01 ON public.sj_retry_task USING btree (namespace_id, group_name, scene_name);
CREATE INDEX idx_sj_retry_task_02 ON public.sj_retry_task USING btree (task_status);
CREATE INDEX idx_sj_retry_task_03 ON public.sj_retry_task USING btree (create_dt);
CREATE INDEX idx_sj_retry_task_04 ON public.sj_retry_task USING btree (retry_id);
COMMENT ON TABLE public.sj_retry_task IS '重试任务表';

-- Column comments

COMMENT ON COLUMN public.sj_retry_task.id IS '主键';
COMMENT ON COLUMN public.sj_retry_task.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_retry_task.group_name IS '组名称';
COMMENT ON COLUMN public.sj_retry_task.scene_name IS '场景名称';
COMMENT ON COLUMN public.sj_retry_task.retry_id IS '重试信息Id';
COMMENT ON COLUMN public.sj_retry_task.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_retry_task.task_status IS '重试状态';
COMMENT ON COLUMN public.sj_retry_task.task_type IS '任务类型 1、重试数据 2、回调数据';
COMMENT ON COLUMN public.sj_retry_task.operation_reason IS '操作原因';
COMMENT ON COLUMN public.sj_retry_task.client_info IS '客户端地址 clientId#ip:port';
COMMENT ON COLUMN public.sj_retry_task.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_retry_task.update_dt IS '修改时间';


-- public.sj_retry_task_log_message definition

-- Drop table

-- DROP TABLE public.sj_retry_task_log_message;

CREATE TABLE public.sj_retry_task_log_message (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	retry_id int8 NOT NULL, -- 重试信息Id
	retry_task_id int8 NOT NULL, -- 重试任务Id
	message text NOT NULL, -- 异常信息
	log_num int4 DEFAULT 1 NOT NULL, -- 日志数量
	real_time int8 DEFAULT 0 NOT NULL, -- 上报时间
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	CONSTRAINT sj_retry_task_log_message_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_retry_task_log_message_01 ON public.sj_retry_task_log_message USING btree (namespace_id, group_name, retry_task_id);
CREATE INDEX idx_sj_retry_task_log_message_02 ON public.sj_retry_task_log_message USING btree (create_dt);
COMMENT ON TABLE public.sj_retry_task_log_message IS '任务调度日志信息记录表';

-- Column comments

COMMENT ON COLUMN public.sj_retry_task_log_message.id IS '主键';
COMMENT ON COLUMN public.sj_retry_task_log_message.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_retry_task_log_message.group_name IS '组名称';
COMMENT ON COLUMN public.sj_retry_task_log_message.retry_id IS '重试信息Id';
COMMENT ON COLUMN public.sj_retry_task_log_message.retry_task_id IS '重试任务Id';
COMMENT ON COLUMN public.sj_retry_task_log_message.message IS '异常信息';
COMMENT ON COLUMN public.sj_retry_task_log_message.log_num IS '日志数量';
COMMENT ON COLUMN public.sj_retry_task_log_message.real_time IS '上报时间';
COMMENT ON COLUMN public.sj_retry_task_log_message.create_dt IS '创建时间';


-- public.sj_server_node definition

-- Drop table

-- DROP TABLE public.sj_server_node;

CREATE TABLE public.sj_server_node (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	host_id varchar(64) NOT NULL, -- 主机id
	host_ip varchar(64) NOT NULL, -- 机器ip
	host_port int4 NOT NULL, -- 机器端口
	expire_at timestamp NOT NULL, -- 过期时间
	node_type int2 NOT NULL, -- 节点类型 1、客户端 2、是服务端
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	labels varchar(512) DEFAULT ''::character varying NULL, -- 标签
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_server_node_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_server_node_01 ON public.sj_server_node USING btree (namespace_id, group_name);
CREATE INDEX idx_sj_server_node_02 ON public.sj_server_node USING btree (expire_at, node_type);
CREATE UNIQUE INDEX uk_sj_server_node_01 ON public.sj_server_node USING btree (host_id, host_ip);
COMMENT ON TABLE public.sj_server_node IS '服务器节点';

-- Column comments

COMMENT ON COLUMN public.sj_server_node.id IS '主键';
COMMENT ON COLUMN public.sj_server_node.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_server_node.group_name IS '组名称';
COMMENT ON COLUMN public.sj_server_node.host_id IS '主机id';
COMMENT ON COLUMN public.sj_server_node.host_ip IS '机器ip';
COMMENT ON COLUMN public.sj_server_node.host_port IS '机器端口';
COMMENT ON COLUMN public.sj_server_node.expire_at IS '过期时间';
COMMENT ON COLUMN public.sj_server_node.node_type IS '节点类型 1、客户端 2、是服务端';
COMMENT ON COLUMN public.sj_server_node.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_server_node.labels IS '标签';
COMMENT ON COLUMN public.sj_server_node.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_server_node.update_dt IS '修改时间';


-- public.sj_system_user definition

-- Drop table

-- DROP TABLE public.sj_system_user;

CREATE TABLE public.sj_system_user (
	id bigserial NOT NULL, -- 主键
	username varchar(64) NOT NULL, -- 账号
	"password" varchar(128) NOT NULL, -- 密码
	"role" int2 DEFAULT 0 NOT NULL, -- 角色：1-普通用户、2-管理员
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_system_user_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.sj_system_user IS '系统用户表';

-- Column comments

COMMENT ON COLUMN public.sj_system_user.id IS '主键';
COMMENT ON COLUMN public.sj_system_user.username IS '账号';
COMMENT ON COLUMN public.sj_system_user."password" IS '密码';
COMMENT ON COLUMN public.sj_system_user."role" IS '角色：1-普通用户、2-管理员';
COMMENT ON COLUMN public.sj_system_user.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_system_user.update_dt IS '修改时间';


-- public.sj_system_user_permission definition

-- Drop table

-- DROP TABLE public.sj_system_user_permission;

CREATE TABLE public.sj_system_user_permission (
	id bigserial NOT NULL, -- 主键
	group_name varchar(64) NOT NULL, -- 组名称
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	system_user_id int8 NOT NULL, -- 系统用户id
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_system_user_permission_pkey PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uk_sj_system_user_permission_01 ON public.sj_system_user_permission USING btree (namespace_id, group_name, system_user_id);
COMMENT ON TABLE public.sj_system_user_permission IS '系统用户权限表';

-- Column comments

COMMENT ON COLUMN public.sj_system_user_permission.id IS '主键';
COMMENT ON COLUMN public.sj_system_user_permission.group_name IS '组名称';
COMMENT ON COLUMN public.sj_system_user_permission.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_system_user_permission.system_user_id IS '系统用户id';
COMMENT ON COLUMN public.sj_system_user_permission.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_system_user_permission.update_dt IS '修改时间';


-- public.sj_workflow definition

-- Drop table

-- DROP TABLE public.sj_workflow;

CREATE TABLE public.sj_workflow (
	id bigserial NOT NULL, -- 主键
	workflow_name varchar(64) NOT NULL, -- 工作流名称
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	workflow_status int2 DEFAULT 1 NOT NULL, -- 工作流状态 0、关闭、1、开启
	trigger_type int2 NOT NULL, -- 触发类型 1.CRON 表达式 2. 固定时间
	trigger_interval varchar(255) NOT NULL, -- 间隔时长
	next_trigger_at int8 NOT NULL, -- 下次触发时间
	block_strategy int2 DEFAULT 1 NOT NULL, -- 阻塞策略 1、丢弃 2、覆盖 3、并行
	executor_timeout int4 DEFAULT 0 NOT NULL, -- 任务执行超时时间，单位秒
	description varchar(256) DEFAULT ''::character varying NOT NULL, -- 描述
	flow_info text NULL, -- 流程信息
	wf_context text NULL, -- 上下文
	notify_ids varchar(128) DEFAULT ''::character varying NOT NULL, -- 通知告警场景配置id列表
	bucket_index int4 DEFAULT 0 NOT NULL, -- bucket
	"version" int4 NOT NULL, -- 版本号
	owner_id int8 NULL, -- 负责人id
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	deleted int2 DEFAULT 0 NOT NULL, -- 逻辑删除 1、删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_workflow_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_workflow_01 ON public.sj_workflow USING btree (create_dt);
CREATE INDEX idx_sj_workflow_02 ON public.sj_workflow USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_workflow IS '工作流';

-- Column comments

COMMENT ON COLUMN public.sj_workflow.id IS '主键';
COMMENT ON COLUMN public.sj_workflow.workflow_name IS '工作流名称';
COMMENT ON COLUMN public.sj_workflow.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_workflow.group_name IS '组名称';
COMMENT ON COLUMN public.sj_workflow.workflow_status IS '工作流状态 0、关闭、1、开启';
COMMENT ON COLUMN public.sj_workflow.trigger_type IS '触发类型 1.CRON 表达式 2. 固定时间';
COMMENT ON COLUMN public.sj_workflow.trigger_interval IS '间隔时长';
COMMENT ON COLUMN public.sj_workflow.next_trigger_at IS '下次触发时间';
COMMENT ON COLUMN public.sj_workflow.block_strategy IS '阻塞策略 1、丢弃 2、覆盖 3、并行';
COMMENT ON COLUMN public.sj_workflow.executor_timeout IS '任务执行超时时间，单位秒';
COMMENT ON COLUMN public.sj_workflow.description IS '描述';
COMMENT ON COLUMN public.sj_workflow.flow_info IS '流程信息';
COMMENT ON COLUMN public.sj_workflow.wf_context IS '上下文';
COMMENT ON COLUMN public.sj_workflow.notify_ids IS '通知告警场景配置id列表';
COMMENT ON COLUMN public.sj_workflow.bucket_index IS 'bucket';
COMMENT ON COLUMN public.sj_workflow."version" IS '版本号';
COMMENT ON COLUMN public.sj_workflow.owner_id IS '负责人id';
COMMENT ON COLUMN public.sj_workflow.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_workflow.deleted IS '逻辑删除 1、删除';
COMMENT ON COLUMN public.sj_workflow.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_workflow.update_dt IS '修改时间';


-- public.sj_workflow_node definition

-- Drop table

-- DROP TABLE public.sj_workflow_node;

CREATE TABLE public.sj_workflow_node (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	node_name varchar(64) NOT NULL, -- 节点名称
	group_name varchar(64) NOT NULL, -- 组名称
	job_id int8 NOT NULL, -- 任务信息id
	workflow_id int8 NOT NULL, -- 工作流ID
	node_type int2 DEFAULT 1 NOT NULL, -- 1、任务节点 2、条件节点
	expression_type int2 DEFAULT 0 NOT NULL, -- 1、SpEl、2、Aviator 3、QL
	fail_strategy int2 DEFAULT 1 NOT NULL, -- 失败策略 1、跳过 2、阻塞
	workflow_node_status int2 DEFAULT 1 NOT NULL, -- 工作流节点状态 0、关闭、1、开启
	priority_level int4 DEFAULT 1 NOT NULL, -- 优先级
	node_info text NULL, -- 节点信息
	"version" int4 NOT NULL, -- 版本号
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	deleted int2 DEFAULT 0 NOT NULL, -- 逻辑删除 1、删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_workflow_node_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_workflow_node_01 ON public.sj_workflow_node USING btree (create_dt);
CREATE INDEX idx_sj_workflow_node_02 ON public.sj_workflow_node USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_workflow_node IS '工作流节点';

-- Column comments

COMMENT ON COLUMN public.sj_workflow_node.id IS '主键';
COMMENT ON COLUMN public.sj_workflow_node.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_workflow_node.node_name IS '节点名称';
COMMENT ON COLUMN public.sj_workflow_node.group_name IS '组名称';
COMMENT ON COLUMN public.sj_workflow_node.job_id IS '任务信息id';
COMMENT ON COLUMN public.sj_workflow_node.workflow_id IS '工作流ID';
COMMENT ON COLUMN public.sj_workflow_node.node_type IS '1、任务节点 2、条件节点';
COMMENT ON COLUMN public.sj_workflow_node.expression_type IS '1、SpEl、2、Aviator 3、QL';
COMMENT ON COLUMN public.sj_workflow_node.fail_strategy IS '失败策略 1、跳过 2、阻塞';
COMMENT ON COLUMN public.sj_workflow_node.workflow_node_status IS '工作流节点状态 0、关闭、1、开启';
COMMENT ON COLUMN public.sj_workflow_node.priority_level IS '优先级';
COMMENT ON COLUMN public.sj_workflow_node.node_info IS '节点信息 ';
COMMENT ON COLUMN public.sj_workflow_node."version" IS '版本号';
COMMENT ON COLUMN public.sj_workflow_node.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_workflow_node.deleted IS '逻辑删除 1、删除';
COMMENT ON COLUMN public.sj_workflow_node.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_workflow_node.update_dt IS '修改时间';


-- public.sj_workflow_task_batch definition

-- Drop table

-- DROP TABLE public.sj_workflow_task_batch;

CREATE TABLE public.sj_workflow_task_batch (
	id bigserial NOT NULL, -- 主键
	namespace_id varchar(64) DEFAULT '764d604ec6fc45f68cd92514c40e9e1a'::character varying NOT NULL, -- 命名空间id
	group_name varchar(64) NOT NULL, -- 组名称
	workflow_id int8 NOT NULL, -- 工作流任务id
	task_batch_status int2 DEFAULT 0 NOT NULL, -- 任务批次状态 0、失败 1、成功
	operation_reason int2 DEFAULT 0 NOT NULL, -- 操作原因
	flow_info text NULL, -- 流程信息
	wf_context text NULL, -- 全局上下文
	execution_at int8 DEFAULT 0 NOT NULL, -- 任务执行时间
	ext_attrs varchar(256) DEFAULT ''::character varying NULL, -- 扩展字段
	"version" int4 DEFAULT 1 NOT NULL, -- 版本号
	deleted int2 DEFAULT 0 NOT NULL, -- 逻辑删除 1、删除
	create_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	update_dt timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 修改时间
	CONSTRAINT sj_workflow_task_batch_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_sj_workflow_task_batch_01 ON public.sj_workflow_task_batch USING btree (workflow_id, task_batch_status);
CREATE INDEX idx_sj_workflow_task_batch_02 ON public.sj_workflow_task_batch USING btree (create_dt);
CREATE INDEX idx_sj_workflow_task_batch_03 ON public.sj_workflow_task_batch USING btree (namespace_id, group_name);
COMMENT ON TABLE public.sj_workflow_task_batch IS '工作流批次';

-- Column comments

COMMENT ON COLUMN public.sj_workflow_task_batch.id IS '主键';
COMMENT ON COLUMN public.sj_workflow_task_batch.namespace_id IS '命名空间id';
COMMENT ON COLUMN public.sj_workflow_task_batch.group_name IS '组名称';
COMMENT ON COLUMN public.sj_workflow_task_batch.workflow_id IS '工作流任务id';
COMMENT ON COLUMN public.sj_workflow_task_batch.task_batch_status IS '任务批次状态 0、失败 1、成功';
COMMENT ON COLUMN public.sj_workflow_task_batch.operation_reason IS '操作原因';
COMMENT ON COLUMN public.sj_workflow_task_batch.flow_info IS '流程信息';
COMMENT ON COLUMN public.sj_workflow_task_batch.wf_context IS '全局上下文';
COMMENT ON COLUMN public.sj_workflow_task_batch.execution_at IS '任务执行时间';
COMMENT ON COLUMN public.sj_workflow_task_batch.ext_attrs IS '扩展字段';
COMMENT ON COLUMN public.sj_workflow_task_batch."version" IS '版本号';
COMMENT ON COLUMN public.sj_workflow_task_batch.deleted IS '逻辑删除 1、删除';
COMMENT ON COLUMN public.sj_workflow_task_batch.create_dt IS '创建时间';
COMMENT ON COLUMN public.sj_workflow_task_batch.update_dt IS '修改时间';