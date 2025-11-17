ALTER TABLE public.app_resource_page ADD edit_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.edit_view_mode IS '编辑模式';
ALTER TABLE public.app_resource_page ADD detail_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.detail_view_mode IS '详情模式';
ALTER TABLE public.app_resource_page ADD is_default_edit_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.is_default_edit_view_mode IS '是否默认编辑视图';
ALTER TABLE public.app_resource_page ADD is_default_detail_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.is_default_detail_view_mode IS '是否默认详情视图';
ALTER TABLE public.app_resource_page ADD is_latest_updated int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.is_latest_updated IS '最新更新的视图';
ALTER TABLE public.app_resource_component ADD component_index int8 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.component_index IS '组件索引';

ALTER TABLE public.app_resource_pageset ADD pageset_type int4 DEFAULT 1 NOT NULL;

ALTER TABLE public.app_resource_page ADD interaction_rules text NULL;
COMMENT ON COLUMN public.app_resource_page.interaction_rules IS '视图规则';


