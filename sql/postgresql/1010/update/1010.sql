ALTER TABLE public.app_resource_page ADD edit_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.edit_view_mode IS '编辑模式';
ALTER TABLE public.app_resource_page ADD detail_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.detail_view_mode IS '详情模式';
ALTER TABLE public.app_resource_page ADD is_default_edit_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.is_default_edit_view_mode IS '是否默认编辑视图';
ALTER TABLE public.app_resource_page ADD is_default_detail_view_mode int2 DEFAULT 0 NOT NULL;
COMMENT ON COLUMN public.app_resource_page.is_default_detail_view_mode IS '是否默认详情视图';
