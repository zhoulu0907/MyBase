-- 公式表建表语句
create table public.formula_formula
(
    id           bigint                              not null
        constraint pk_formula_formula primary key,
    name         varchar(128)                        not null,
    description  varchar(1024),
    expression   text                                not null,
    use_scene    varchar(64)                         not null,
    type         varchar(32)                         not null,
    creator      bigint                              not null,
    create_time  timestamp default CURRENT_TIMESTAMP not null,
    updater      bigint                              not null,
    update_time  timestamp default CURRENT_TIMESTAMP not null,
    tenant_id    bigint                              not null,
    lock_version bigint    default 0                 not null,
    deleted      bigint    default 0                 not null
);

comment on table public.formula_formula is '公式表';
comment on column public.formula_formula.id is '主键ID';
comment on column public.formula_formula.name is '公式名称';
comment on column public.formula_formula.description is '公式描述';
comment on column public.formula_formula.expression is '公式表达式';
comment on column public.formula_formula.use_scene is '使用场景';
comment on column public.formula_formula.type is '公式类型（内置/自定义）';
comment on column public.formula_formula.creator is '创建者';
comment on column public.formula_formula.create_time is '创建时间';
comment on column public.formula_formula.updater is '更新者';
comment on column public.formula_formula.update_time is '更新时间';
comment on column public.formula_formula.tenant_id is '租户ID';
comment on column public.formula_formula.lock_version is '乐观锁';
comment on column public.formula_formula.deleted is '软删标记，0未删除，非0已删除';

-- 函数表建表语句
create table public.formula_function
(
    id           bigint                                not null
        constraint pk_formula_function primary key,
    type         varchar(64)                           not null,
    name         varchar(128)                          not null,
    expression   text                                  not null,
    summary      varchar(512),
    usage        text,
    example      text,
    return_type  varchar(64)                           not null,
    version      varchar(32) default 1                 not null,
    status       integer     default 1                 not null,
    creator      bigint                                not null,
    create_time  timestamp   default CURRENT_TIMESTAMP not null,
    updater      bigint                                not null,
    update_time  timestamp   default CURRENT_TIMESTAMP not null,
    tenant_id    bigint                                not null,
    lock_version bigint      default 0                 not null,
    deleted      bigint      default 0                 not null
);

comment on table public.formula_function is '函数表';
comment on column public.formula_function.id is '主键ID';
comment on column public.formula_function.type is '函数类型';
comment on column public.formula_function.name is '函数名称';
comment on column public.formula_function.expression is '函数表达式';
comment on column public.formula_function.summary is '函数简介';
comment on column public.formula_function.usage is '函数用法（md格式）';
comment on column public.formula_function.example is '函数示例（md格式）';
comment on column public.formula_function.return_type is '返回值类型';
comment on column public.formula_function.version is '函数版本';
comment on column public.formula_function.status is '函数状态';
comment on column public.formula_function.creator is '创建者';
comment on column public.formula_function.create_time is '创建时间';
comment on column public.formula_function.updater is '更新者';
comment on column public.formula_function.update_time is '更新时间';
comment on column public.formula_function.tenant_id is '租户ID';
comment on column public.formula_function.lock_version is '乐观锁';
comment on column public.formula_function.deleted is '软删标记，0未删除，非0已删除';

-- 添加索引
create index idx_formula_formula_name on public.formula_formula (name);
create index idx_formula_formula_type on public.formula_formula (type);
create index idx_formula_formula_scene on public.formula_formula (use_scene);
create index idx_formula_formula_tenant on public.formula_formula (tenant_id, deleted);

create index idx_formula_function_name on public.formula_function (name);
create index idx_formula_function_type on public.formula_function (type);
create index idx_formula_function_status on public.formula_function (status);
create index idx_formula_function_tenant on public.formula_function (tenant_id, deleted);

alter table public.formula_formula
    owner to postgres;
alter table public.formula_function
    owner to postgres;
