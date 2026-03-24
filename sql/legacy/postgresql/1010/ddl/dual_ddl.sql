-- public.dual definition

-- Drop table

-- DROP TABLE public.dual;

CREATE TABLE public.dual (
	id int2 NULL
);
COMMENT ON TABLE public.dual IS '数据库连接的表';
