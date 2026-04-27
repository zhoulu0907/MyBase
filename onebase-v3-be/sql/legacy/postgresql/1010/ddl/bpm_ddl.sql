-- public.bpm_flow_definition definition

-- Drop table

-- DROP TABLE public.bpm_flow_definition;

CREATE TABLE public.bpm_flow_definition (
	id int8 NOT NULL, -- 主键id
	flow_code varchar(40) NOT NULL, -- 流程编码
	flow_name varchar(100) NOT NULL, -- 流程名称
	model_value varchar(40) DEFAULT 'CLASSICS'::character varying NOT NULL, -- 设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）
	category varchar(100) NULL, -- 流程类别
	is_publish int2 DEFAULT 0 NOT NULL, -- 是否发布（0未发布 1已发布 9失效）
	form_custom bpchar(1) DEFAULT 'N'::character varying NULL, -- 审批表单是否自定义（Y是 N否）
	form_path varchar(100) NULL, -- 审批表单路径
	activity_status int2 DEFAULT 1 NOT NULL, -- 流程激活状态（0挂起 1激活）
	listener_type varchar(100) NULL, -- 监听器类型
	listener_path varchar(400) NULL, -- 监听器路径
	ext varchar(500) NULL, -- 扩展字段，预留给业务系统使用
	lock_version int8 DEFAULT 0 NOT NULL, -- 流程版本
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	deleted int8 DEFAULT 0 NOT NULL, -- 删除标志
	tenant_id int8 DEFAULT 0 NOT NULL, -- 租户id
	CONSTRAINT bpm_flow_definition_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE public.bpm_flow_definition IS '流程定义表';

-- Column comments

COMMENT ON COLUMN public.bpm_flow_definition.id IS '主键id';
COMMENT ON COLUMN public.bpm_flow_definition.flow_code IS '流程编码';
COMMENT ON COLUMN public.bpm_flow_definition.flow_name IS '流程名称';
COMMENT ON COLUMN public.bpm_flow_definition.model_value IS '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）';
COMMENT ON COLUMN public.bpm_flow_definition.category IS '流程类别';
COMMENT ON COLUMN public.bpm_flow_definition.is_publish IS '是否发布（0未发布 1已发布 9失效）';
COMMENT ON COLUMN public.bpm_flow_definition.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN public.bpm_flow_definition.form_path IS '审批表单路径';
COMMENT ON COLUMN public.bpm_flow_definition.activity_status IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN public.bpm_flow_definition.listener_type IS '监听器类型';
COMMENT ON COLUMN public.bpm_flow_definition.listener_path IS '监听器路径';
COMMENT ON COLUMN public.bpm_flow_definition.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN public.bpm_flow_definition.lock_version IS '流程版本';
COMMENT ON COLUMN public.bpm_flow_definition.create_time IS '创建时间';
COMMENT ON COLUMN public.bpm_flow_definition.update_time IS '更新时间';
COMMENT ON COLUMN public.bpm_flow_definition.deleted IS '删除标志';
COMMENT ON COLUMN public.bpm_flow_definition.tenant_id IS '租户id';