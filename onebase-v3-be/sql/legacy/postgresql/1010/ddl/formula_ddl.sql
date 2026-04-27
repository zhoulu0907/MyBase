-- public.formula_formula definition

-- Drop table

-- DROP TABLE public.formula_formula;

CREATE TABLE public.formula_formula (
	id int8 NOT NULL, -- 主键ID
	"name" varchar(128) NOT NULL, -- 公式名称
	description varchar(1024) NULL, -- 公式描述
	"expression" text NOT NULL, -- 公式表达式
	use_scene varchar(64) NOT NULL, -- 使用场景
	"type" varchar(32) NOT NULL, -- 公式类型（内置/自定义）
	creator int8 NOT NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	tenant_id int8 NOT NULL, -- 租户ID
	lock_version int8 DEFAULT 0 NOT NULL, -- 乐观锁
	deleted int8 DEFAULT 0 NOT NULL, -- 软删标记，0未删除，非0已删除
	CONSTRAINT pk_formula_formula PRIMARY KEY (id)
);
CREATE INDEX idx_formula_formula_name ON public.formula_formula USING btree (name);
CREATE INDEX idx_formula_formula_scene ON public.formula_formula USING btree (use_scene);
CREATE INDEX idx_formula_formula_tenant ON public.formula_formula USING btree (tenant_id, deleted);
CREATE INDEX idx_formula_formula_type ON public.formula_formula USING btree (type);
COMMENT ON TABLE public.formula_formula IS '公式表';

-- Column comments

COMMENT ON COLUMN public.formula_formula.id IS '主键ID';
COMMENT ON COLUMN public.formula_formula."name" IS '公式名称';
COMMENT ON COLUMN public.formula_formula.description IS '公式描述';
COMMENT ON COLUMN public.formula_formula."expression" IS '公式表达式';
COMMENT ON COLUMN public.formula_formula.use_scene IS '使用场景';
COMMENT ON COLUMN public.formula_formula."type" IS '公式类型（内置/自定义）';
COMMENT ON COLUMN public.formula_formula.creator IS '创建者';
COMMENT ON COLUMN public.formula_formula.create_time IS '创建时间';
COMMENT ON COLUMN public.formula_formula.updater IS '更新者';
COMMENT ON COLUMN public.formula_formula.update_time IS '更新时间';
COMMENT ON COLUMN public.formula_formula.tenant_id IS '租户ID';
COMMENT ON COLUMN public.formula_formula.lock_version IS '乐观锁';
COMMENT ON COLUMN public.formula_formula.deleted IS '软删标记，0未删除，非0已删除';


-- public.formula_function definition

-- Drop table

-- DROP TABLE public.formula_function;

CREATE TABLE public.formula_function (
	id int8 NOT NULL, -- 主键ID
	"type" varchar(64) NOT NULL, -- 函数类型
	"name" varchar(128) NOT NULL, -- 函数名称
	"expression" text NOT NULL, -- 函数表达式
	summary varchar(512) NULL, -- 函数简介
	"usage" text NULL, -- 函数用法（md格式）
	example text NULL, -- 函数示例（md格式）
	return_type varchar(64) NOT NULL, -- 返回值类型
	"version" varchar(32) DEFAULT '1' NOT NULL, -- 函数版本
	status int4 DEFAULT 1 NOT NULL, -- 函数状态
	creator int8 NOT NULL, -- 创建者
	create_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
	updater int8 NOT NULL, -- 更新者
	update_time timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
	tenant_id int8 NOT NULL, -- 租户ID
	lock_version int8 DEFAULT 0 NOT NULL, -- 乐观锁
	deleted int8 DEFAULT 0 NOT NULL, -- 软删标记，0未删除，非0已删除
	CONSTRAINT pk_formula_function PRIMARY KEY (id)
);
CREATE INDEX idx_formula_function_name ON public.formula_function USING btree (name);
CREATE INDEX idx_formula_function_status ON public.formula_function USING btree (status);
CREATE INDEX idx_formula_function_tenant ON public.formula_function USING btree (tenant_id, deleted);
CREATE INDEX idx_formula_function_type ON public.formula_function USING btree (type);
COMMENT ON TABLE public.formula_function IS '函数表';

-- Column comments

COMMENT ON COLUMN public.formula_function.id IS '主键ID';
COMMENT ON COLUMN public.formula_function."type" IS '函数类型';
COMMENT ON COLUMN public.formula_function."name" IS '函数名称';
COMMENT ON COLUMN public.formula_function."expression" IS '函数表达式';
COMMENT ON COLUMN public.formula_function.summary IS '函数简介';
COMMENT ON COLUMN public.formula_function."usage" IS '函数用法（md格式）';
COMMENT ON COLUMN public.formula_function.example IS '函数示例（md格式）';
COMMENT ON COLUMN public.formula_function.return_type IS '返回值类型';
COMMENT ON COLUMN public.formula_function."version" IS '函数版本';
COMMENT ON COLUMN public.formula_function.status IS '函数状态';
COMMENT ON COLUMN public.formula_function.creator IS '创建者';
COMMENT ON COLUMN public.formula_function.create_time IS '创建时间';
COMMENT ON COLUMN public.formula_function.updater IS '更新者';
COMMENT ON COLUMN public.formula_function.update_time IS '更新时间';
COMMENT ON COLUMN public.formula_function.tenant_id IS '租户ID';
COMMENT ON COLUMN public.formula_function.lock_version IS '乐观锁';
COMMENT ON COLUMN public.formula_function.deleted IS '软删标记，0未删除，非0已删除';