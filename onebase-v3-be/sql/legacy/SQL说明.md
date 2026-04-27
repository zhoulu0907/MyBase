
# 必备基础字段-postgreSQL
create table public.xxx (
id int8 primary key not null, -- ID
creator int8 NOT NULL, -- 创建者
create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
updater int8 NOT NULL, -- 更新者
update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
tenant_id int8 NOT NULL, -- 租户ID
lock_version int8 DEFAULT 0 NOT NULL, -- 乐观锁
deleted int8 DEFAULT 0 NOT NULL, -- 软删标记，0未删除，非0已删除
);
comment on table public.xxx is '某某表';
comment on column public.xxx.id is 'ID';
comment on column public.xxx.creator is '创建者';
comment on column public.xxx.create_time is '创建时间';
comment on column public.xxx.updater is '更新者';
comment on column public.xxx.update_time is '更新时间';
comment on column public.xxx.tenant_id is '租户ID';
comment on column public.xxx.lock_version is '乐观锁';
comment on column public.xxx.deleted is '软删标记，0未删除，非0已删除';
