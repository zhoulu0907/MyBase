CREATE TABLE bpm_flow_definition
(
    id              int8         NOT NULL,
    flow_code       varchar(40)  NOT NULL,
    flow_name       varchar(100) NOT NULL,
    model_value     varchar(40)  NOT NULL DEFAULT 'CLASSICS',
    category        varchar(100) NULL,
    "bpm_version"       varchar(20)  NOT NULL,
    is_publish      int2         NOT NULL DEFAULT 0,
    form_custom     bpchar(1)    NULL     DEFAULT 'N':: character varying,
    form_path       varchar(100) NULL,
    activity_status int2         NOT NULL DEFAULT 1,
    listener_type   varchar(100) NULL,
    listener_path   varchar(400) NULL,
    ext             text          NULL,
    "application_id" int8 NOT NULL,
    "version_tag"   int8 NOT NULL DEFAULT 0,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_definition_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_definition IS '流程定义表';

COMMENT ON COLUMN bpm_flow_definition.id IS '主键id';
COMMENT ON COLUMN bpm_flow_definition.flow_code IS '流程编码';
COMMENT ON COLUMN bpm_flow_definition.flow_name IS '流程名称';
COMMENT ON COLUMN bpm_flow_definition.model_value IS '设计器模型（CLASSICS经典模型 MIMIC仿钉钉模型）';
COMMENT ON COLUMN bpm_flow_definition.category IS '流程类别';
COMMENT ON COLUMN bpm_flow_definition."bpm_version" IS '流程版本';
COMMENT ON COLUMN bpm_flow_definition.is_publish IS '是否发布（0未发布 1已发布 9失效）';
COMMENT ON COLUMN bpm_flow_definition.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN bpm_flow_definition.form_path IS '审批表单路径';
COMMENT ON COLUMN bpm_flow_definition.activity_status IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN bpm_flow_definition.listener_type IS '监听器类型';
COMMENT ON COLUMN bpm_flow_definition.listener_path IS '监听器路径';
COMMENT ON COLUMN bpm_flow_definition.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN bpm_flow_definition."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_definition.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_definition.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_definition.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_definition.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_definition.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_definition.tenant_id IS '租户id';
COMMENT ON COLUMN bpm_flow_definition."application_id" IS '应用ID';
COMMENT ON COLUMN bpm_flow_definition."version_tag" IS '版本标签';

CREATE TABLE bpm_flow_node
(
    id              int8          NOT NULL,
    node_type       int2          NOT NULL,
    definition_id   int8          NOT NULL,
    node_code       varchar(100)  NOT NULL,
    node_name       varchar(100)  NULL,
    permission_flag varchar(200)  NULL,
    node_ratio      numeric(6, 3) NULL,
    coordinate      varchar(100)  NULL,
    any_node_skip   varchar(100)  NULL,
    listener_type   varchar(100)  NULL,
    listener_path   varchar(400)  NULL,
    handler_type    varchar(100)  NULL,
    handler_path    varchar(400)  NULL,
    form_custom     bpchar(1)     NULL DEFAULT 'N':: character varying,
    form_path       varchar(100)  NULL,
    "application_id" int8 NOT NULL,
    "version_tag"   int8 NOT NULL DEFAULT 0,
    "bpm_version"       varchar(20)   NOT NULL,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ext             text          NULL,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_node_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_node IS '流程节点表';

COMMENT ON COLUMN bpm_flow_node.id IS '主键id';
COMMENT ON COLUMN bpm_flow_node.node_type IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN bpm_flow_node.definition_id IS '流程定义id';
COMMENT ON COLUMN bpm_flow_node.node_code IS '流程节点编码';
COMMENT ON COLUMN bpm_flow_node.node_name IS '流程节点名称';
COMMENT ON COLUMN bpm_flow_node.permission_flag IS '权限标识（权限类型:权限标识，可以多个，用@@隔开)';
COMMENT ON COLUMN bpm_flow_node.node_ratio IS '流程签署比例值';
COMMENT ON COLUMN bpm_flow_node.coordinate IS '坐标';
COMMENT ON COLUMN bpm_flow_node.any_node_skip IS '任意结点跳转';
COMMENT ON COLUMN bpm_flow_node.listener_type IS '监听器类型';
COMMENT ON COLUMN bpm_flow_node.listener_path IS '监听器路径';
COMMENT ON COLUMN bpm_flow_node.handler_type IS '处理器类型';
COMMENT ON COLUMN bpm_flow_node.handler_path IS '处理器路径';
COMMENT ON COLUMN bpm_flow_node.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN bpm_flow_node.form_path IS '审批表单路径';
COMMENT ON COLUMN bpm_flow_node."bpm_version" IS '版本';
COMMENT ON COLUMN bpm_flow_node."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_node.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_node.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_node.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_node.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_node.ext IS '节点扩展属性';
COMMENT ON COLUMN bpm_flow_node.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_node.tenant_id IS '租户id';
COMMENT ON COLUMN bpm_flow_node.application_id IS '应用ID';
COMMENT ON COLUMN bpm_flow_node.version_tag IS '版本标签';

CREATE TABLE bpm_flow_skip
(
    id             int8         NOT NULL,
    definition_id  int8         NOT NULL,
    now_node_code  varchar(100) NOT NULL,
    now_node_type  int2         NULL,
    next_node_code varchar(100) NOT NULL,
    next_node_type int2         NULL,
    skip_name      varchar(100) NULL,
    skip_type      varchar(40)  NULL,
    skip_condition varchar(200) NULL,
    coordinate     varchar(100) NULL,
    "application_id" int8 NOT NULL,
    "version_tag"   int8 NOT NULL DEFAULT 0,
    "ext"          text NULL,
    "priority" int2 NOT NULL DEFAULT 0,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_skip_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_skip IS '节点跳转关联表';

COMMENT ON COLUMN bpm_flow_skip.id IS '主键id';
COMMENT ON COLUMN bpm_flow_skip.definition_id IS '流程定义id';
COMMENT ON COLUMN bpm_flow_skip.now_node_code IS '当前流程节点的编码';
COMMENT ON COLUMN bpm_flow_skip.now_node_type IS '当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN bpm_flow_skip.next_node_code IS '下一个流程节点的编码';
COMMENT ON COLUMN bpm_flow_skip.next_node_type IS '下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN bpm_flow_skip.skip_name IS '跳转名称';
COMMENT ON COLUMN bpm_flow_skip.skip_type IS '跳转类型（PASS审批通过 REJECT退回）';
COMMENT ON COLUMN bpm_flow_skip.skip_condition IS '跳转条件';
COMMENT ON COLUMN bpm_flow_skip.coordinate IS '坐标';
COMMENT ON COLUMN bpm_flow_skip."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_skip.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_skip.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_skip.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_skip.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_skip.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_skip.tenant_id IS '租户id';
COMMENT ON COLUMN bpm_flow_skip."ext" IS '扩展信息';
COMMENT ON COLUMN bpm_flow_skip."priority" IS '优先级';
COMMENT ON COLUMN bpm_flow_skip.application_id IS '应用ID';
COMMENT ON COLUMN bpm_flow_skip.version_tag IS '版本标签';

CREATE TABLE bpm_flow_instance
(
    id              int8         NOT NULL,
    definition_id   int8         NOT NULL,
    business_id     varchar(40)  NOT NULL,
    node_type       int2         NOT NULL,
    node_code       varchar(40)  NOT NULL,
    node_name       varchar(100) NULL,
    variable        text         NULL,
    flow_status     varchar(20)  NOT NULL,
    activity_status int2         NOT NULL DEFAULT 1,
    def_json        text         NULL,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ext             text          NULL,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_instance_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_instance IS '流程实例表';

COMMENT ON COLUMN bpm_flow_instance.id IS '主键id';
COMMENT ON COLUMN bpm_flow_instance.definition_id IS '对应flow_definition表的id';
COMMENT ON COLUMN bpm_flow_instance.business_id IS '业务id';
COMMENT ON COLUMN bpm_flow_instance.node_type IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN bpm_flow_instance.node_code IS '流程节点编码';
COMMENT ON COLUMN bpm_flow_instance.node_name IS '流程节点名称';
COMMENT ON COLUMN bpm_flow_instance.variable IS '任务变量';
COMMENT ON COLUMN bpm_flow_instance.flow_status IS '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';
COMMENT ON COLUMN bpm_flow_instance.activity_status IS '流程激活状态（0挂起 1激活）';
COMMENT ON COLUMN bpm_flow_instance.def_json IS '流程定义json';
COMMENT ON COLUMN bpm_flow_instance."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_instance.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_instance.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_instance.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_instance.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_instance.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN bpm_flow_instance.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_instance.tenant_id IS '租户id';

CREATE TABLE bpm_flow_task
(
    id            int8         NOT NULL,
    definition_id int8         NOT NULL,
    instance_id   int8         NOT NULL,
    node_code     varchar(100) NOT NULL,
    node_name     varchar(100) NULL,
    node_type     int2         NOT NULL,
    flow_status      varchar(20)  NOT NULL,
    form_custom   bpchar(1)    NULL DEFAULT 'N':: character varying,
    form_path     varchar(100) NULL,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_task_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_task IS '待办任务表';

COMMENT ON COLUMN bpm_flow_task.id IS '主键id';
COMMENT ON COLUMN bpm_flow_task.definition_id IS '对应flow_definition表的id';
COMMENT ON COLUMN bpm_flow_task.instance_id IS '对应flow_instance表的id';
COMMENT ON COLUMN bpm_flow_task.node_code IS '节点编码';
COMMENT ON COLUMN bpm_flow_task.node_name IS '节点名称';
COMMENT ON COLUMN bpm_flow_task.node_type IS '节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN bpm_flow_task.flow_status IS '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';
COMMENT ON COLUMN bpm_flow_task.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN bpm_flow_task.form_path IS '审批表单路径';
COMMENT ON COLUMN bpm_flow_task."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_task.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_task.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_task.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_task.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_task.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_task.tenant_id IS '租户id';

CREATE TABLE bpm_flow_his_task
(
    id               int8         NOT NULL,
    definition_id    int8         NOT NULL,
    instance_id      int8         NOT NULL,
    task_id          int8         NOT NULL,
    node_code        varchar(100) NULL,
    node_name        varchar(100) NULL,
    node_type        int2         NULL,
    target_node_code varchar(200) NULL,
    target_node_name varchar(200) NULL,
    approver         varchar(40)  NULL,
    cooperate_type   int2         NOT NULL DEFAULT 0,
    collaborator     varchar(500)  NULL,
    skip_type        varchar(10)  NULL,
    flow_status      varchar(20)  NOT NULL,
    form_custom      bpchar(1)    NULL     DEFAULT 'N':: character varying,
    form_path        varchar(100) NULL,
    ext              text         NULL,
    message          varchar(500) NULL,
    variable         text         NULL,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_his_task_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_his_task IS '历史任务记录表';

COMMENT ON COLUMN bpm_flow_his_task.id IS '主键id';
COMMENT ON COLUMN bpm_flow_his_task.definition_id IS '对应flow_definition表的id';
COMMENT ON COLUMN bpm_flow_his_task.instance_id IS '对应flow_instance表的id';
COMMENT ON COLUMN bpm_flow_his_task.task_id IS '对应flow_task表的id';
COMMENT ON COLUMN bpm_flow_his_task.node_code IS '开始节点编码';
COMMENT ON COLUMN bpm_flow_his_task.node_name IS '开始节点名称';
COMMENT ON COLUMN bpm_flow_his_task.node_type IS '开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）';
COMMENT ON COLUMN bpm_flow_his_task.target_node_code IS '目标节点编码';
COMMENT ON COLUMN bpm_flow_his_task.target_node_name IS '结束节点名称';
COMMENT ON COLUMN bpm_flow_his_task.approver IS '审批者';
COMMENT ON COLUMN bpm_flow_his_task.cooperate_type IS '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)';
COMMENT ON COLUMN bpm_flow_his_task.collaborator IS '协作人';
COMMENT ON COLUMN bpm_flow_his_task.skip_type IS '流转类型（PASS通过 REJECT退回 NONE无动作）';
COMMENT ON COLUMN bpm_flow_his_task.flow_status IS '流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）';
COMMENT ON COLUMN bpm_flow_his_task.form_custom IS '审批表单是否自定义（Y是 N否）';
COMMENT ON COLUMN bpm_flow_his_task.form_path IS '审批表单路径';
COMMENT ON COLUMN bpm_flow_his_task.message IS '审批意见';
COMMENT ON COLUMN bpm_flow_his_task.variable IS '任务变量';
COMMENT ON COLUMN bpm_flow_his_task.ext IS '扩展字段，预留给业务系统使用';
COMMENT ON COLUMN bpm_flow_his_task."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_his_task.create_time IS '任务开始时间';
COMMENT ON COLUMN bpm_flow_his_task.update_time IS '审批完成时间';
COMMENT ON COLUMN bpm_flow_his_task.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_his_task.tenant_id IS '租户id';

CREATE TABLE bpm_flow_user
(
    id           int8        NOT NULL,
    "type"       varchar(8)   NOT NULL,
    processed_by varchar(80) NULL,
    associated   int8        NOT NULL,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_user_pk PRIMARY KEY (id)
);
CREATE INDEX user_processed_type ON bpm_flow_user USING btree (processed_by, type);
CREATE INDEX user_associated_idx ON BPM_FLOW_USER USING btree (associated);
COMMENT ON TABLE bpm_flow_user IS '流程用户表';

COMMENT ON COLUMN bpm_flow_user.id IS '主键id';
COMMENT ON COLUMN bpm_flow_user."type" IS '人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）';
COMMENT ON COLUMN bpm_flow_user.processed_by IS '权限人';
COMMENT ON COLUMN bpm_flow_user.associated IS '任务表id';
COMMENT ON COLUMN bpm_flow_user."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_user.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_user.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_user.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_user.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_user.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_user.tenant_id IS '租户id';


-- 流程实例扩展信息表
-- 与流程实例表一一对应，扩展流程相关信息

CREATE TABLE bpm_flow_instance_biz_ext
(
    id                      int8         NOT NULL,
    instance_id             int8         NOT NULL,
    business_data_id             varchar(100) NOT NULL,
    business_data_code           varchar(100) NULL,
    binding_view_id              varchar(100) NOT NULL,
    bpm_title                    varchar(500) NOT NULL,
    "application_id" int8 NOT NULL,
    initiator_id            varchar(80) NOT NULL,         NULL,
    initiator_name          varchar(100) NULL,
    initiator_avatar        varchar(500) NULL,
    initiator_dept_id       int8         NULL,
    initiator_dept_name     varchar(100) NULL,
    submit_time             timestamp(6) NULL,
    form_summary            varchar(500) NOT NULL,
    form_name               varchar(100) NOT NULL,
    bpm_version             varchar(50)  NOT NULL,
    lock_version            int8         NOT NULL DEFAULT 0,
    creator                 int8         NOT NULL DEFAULT 0,
    create_time             timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 int8         NOT NULL DEFAULT 0,
    update_time             timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 int8         NOT NULL DEFAULT 0,
    tenant_id               int8         NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_instance_biz_ext_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE bpm_flow_instance_biz_ext IS '流程实例扩展信息表';

COMMENT ON COLUMN bpm_flow_instance_biz_ext.id IS '主键ID';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.instance_id IS '流程实例ID';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.business_data_id IS '业务ID';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.business_data_code IS '业务编码';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.binding_view_id IS '绑定视图ID（与流程实例表的form_path字段保持一致）';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.bpm_title IS '流程标题';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.initiator_id IS '发起人ID';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.initiator_name IS '发起人名称（冗余字段）';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.initiator_avatar IS '发起人头像（冗余字段）';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.initiator_dept_id IS '发起部门ID';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.initiator_dept_name IS '发起部门名称（冗余字段）';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.submit_time IS '发起时间（与create_time的区别：以提交表单动作为标准，而非保存表单）';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.form_summary IS '表单摘要';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.form_name IS '流程表单';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.bpm_version IS '流程版本号';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.application_id IS '应用ID';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.lock_version IS '乐观锁';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_instance_biz_ext.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX idx_bpm_flow_instance_biz_ext_instance_id ON bpm_flow_instance_biz_ext(instance_id);
CREATE INDEX idx_bpm_flow_instance_biz_ext_business_id ON bpm_flow_instance_biz_ext(business_data_id);
CREATE INDEX idx_bpm_flow_instance_biz_ext_tenant_id ON bpm_flow_instance_biz_ext(tenant_id);
CREATE INDEX idx_bpm_flow_instance_biz_ext_deleted ON bpm_flow_instance_biz_ext(deleted);

-- 唯一索引：一个流程实例只对应一条扩展信息（未删除的记录）
CREATE UNIQUE INDEX uk_bpm_flow_instance_biz_ext_instance_id ON bpm_flow_instance_biz_ext(instance_id) WHERE deleted = 0;

CREATE TABLE bpm_flow_agent
(
    id                      int8         NOT NULL,
    app_id                  int8         NOT NULL,
    principal_id            varchar(80)         NOT NULL,
    principal_name          varchar(64)  NOT NULL,
    agent_id             varchar(80)         NOT NULL,
    agent_name           varchar(64)  NOT NULL,
    start_time              timestamp(6) NOT NULL,
    end_time                timestamp(6) NOT NULL,
    revoker_id                 varchar(80),
    revoked_time            timestamp(6),

    "application_id" int8 NOT NULL,
    "version_tag"   int8 NOT NULL DEFAULT 0,
    lock_version            int8         NOT NULL DEFAULT 0,
    creator                 int8         NOT NULL DEFAULT 0,
    create_time             timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                 int8         NOT NULL DEFAULT 0,
    update_time             timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                 int8         NOT NULL DEFAULT 0,
    tenant_id               int8         NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_agent_pkey PRIMARY KEY (id)
);

-- 表注释
COMMENT ON TABLE bpm_flow_agent IS '流程代理表';

-- 字段注释
COMMENT ON COLUMN bpm_flow_agent.id IS '主键ID';
COMMENT ON COLUMN bpm_flow_agent.app_id IS '应用ID';
COMMENT ON COLUMN bpm_flow_agent.principal_id IS '被代理人用户ID，代理关系的发起方';
COMMENT ON COLUMN bpm_flow_agent.agent_id IS '代理人用户ID，代理关系的接受方';
COMMENT ON COLUMN bpm_flow_agent.start_time IS '代理生效开始时间';
COMMENT ON COLUMN bpm_flow_agent.end_time IS '代理结束时间，必须晚于开始时间';
COMMENT ON COLUMN bpm_flow_agent.revoker_id IS '撤销人ID';
COMMENT ON COLUMN bpm_flow_agent.revoked_time IS '撤销时间';
COMMENT ON COLUMN bpm_flow_agent.lock_version IS '乐观锁';
COMMENT ON COLUMN bpm_flow_agent.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_agent.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_agent.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_agent.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_agent.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_agent.tenant_id IS '租户ID';
COMMENT ON COLUMN bpm_flow_agent.agent_name IS '代理人用户名称';
COMMENT ON COLUMN bpm_flow_agent.principal_name IS '被代理人用户名称';
COMMENT ON COLUMN bpm_flow_agent.application_id IS '应用ID';
COMMENT ON COLUMN bpm_flow_agent.version_tag IS '版本标签';

CREATE TABLE bpm_flow_cc_record
(
    id               int8        NOT NULL,
    instance_id      int8         NOT NULL,
    task_id          int8         NULL,
    "viewed"         int2 NOT NULL DEFAULT 0,
    "viewed_time" timestamp(6) NULL,
    user_id varchar(80) NULL,
    "lock_version" int8 NOT NULL DEFAULT 0,
    "creator" int8 NOT NULL DEFAULT 0,
    "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater" int8 NOT NULL DEFAULT 0,
    "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted" int8 NOT NULL DEFAULT 0,
    "tenant_id" int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_cc_record_pk PRIMARY KEY (id)
);
COMMENT ON TABLE bpm_flow_cc_record IS '流程抄送记录表';

COMMENT ON COLUMN bpm_flow_cc_record.user_id IS '抄送用户ID';
COMMENT ON COLUMN bpm_flow_cc_record.task_id IS '任务表id';
COMMENT ON COLUMN bpm_flow_cc_record.instance_id IS '流程实例id';
COMMENT ON COLUMN bpm_flow_cc_record.viewed IS '已阅 0，否 1，是';
COMMENT ON COLUMN bpm_flow_cc_record.viewed_time IS '已读时间';

COMMENT ON COLUMN bpm_flow_cc_record.id IS '主键id';
COMMENT ON COLUMN bpm_flow_cc_record."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_cc_record.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_cc_record.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_cc_record.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_cc_record.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_cc_record.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_cc_record.tenant_id IS '租户id';

CREATE TABLE bpm_flow_agent_ins
(
    id              int8         NOT NULL,
    task_id         int8         NOT NULL,
    instance_id     int8         NOT NULL,
    principal_id    varchar(80)  NOT NULL,
    principal_name  varchar(64)  NOT NULL,
    agent_id        varchar(80)  NOT NULL,
    agent_name      varchar(64)  NOT NULL,
    is_executor     int2         NOT NULL DEFAULT 0,
    "lock_version"  int8 NOT NULL DEFAULT 0,
    "creator"       int8 NOT NULL DEFAULT 0,
    "create_time"   timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updater"       int8 NOT NULL DEFAULT 0,
    "update_time"   timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "deleted"       int8 NOT NULL DEFAULT 0,
    "tenant_id"     int8 NOT NULL DEFAULT 0,
    CONSTRAINT bpm_flow_agent_ins_pkey PRIMARY KEY (id)
);
-- 表注释
COMMENT ON TABLE bpm_flow_agent_ins IS '代理关系实例表';

-- 字段注释
COMMENT ON COLUMN bpm_flow_agent_ins.task_id IS '任务ID';
COMMENT ON COLUMN bpm_flow_agent_ins.instance_id IS '流程实例ID';
COMMENT ON COLUMN bpm_flow_agent_ins.principal_id IS '被代理人ID';
COMMENT ON COLUMN bpm_flow_agent_ins.agent_id IS '代理人ID';
COMMENT ON COLUMN bpm_flow_agent_ins.is_executor IS '是否执行人：0=未操作, 1=执行人';
COMMENT ON COLUMN bpm_flow_agent_ins.agent_name IS '代理人用户名称';
COMMENT ON COLUMN bpm_flow_agent_ins.principal_name IS '被代理人用户名称';

COMMENT ON COLUMN bpm_flow_agent_ins.id IS '主键id';
COMMENT ON COLUMN bpm_flow_agent_ins."lock_version" IS '乐观锁';
COMMENT ON COLUMN bpm_flow_agent_ins.create_time IS '创建时间';
COMMENT ON COLUMN bpm_flow_agent_ins.creator IS '创建人';
COMMENT ON COLUMN bpm_flow_agent_ins.update_time IS '更新时间';
COMMENT ON COLUMN bpm_flow_agent_ins.updater IS '更新人';
COMMENT ON COLUMN bpm_flow_agent_ins.deleted IS '删除标志';
COMMENT ON COLUMN bpm_flow_agent_ins.tenant_id IS '租户id';
