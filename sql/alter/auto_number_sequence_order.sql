-- =====================================================
-- 自动编号结构重构 - 方案B（轻量改造）
-- 新增 sequence_order 字段，支持 SEQUENCE 规则项排序
-- 执行时间：2025-12-09
-- =====================================================

-- 在 config 表新增 SEQUENCE 排序字段
ALTER TABLE "public"."metadata_auto_number_config" 
    ADD COLUMN "sequence_order" int4 NOT NULL DEFAULT 999;

-- 添加字段注释
COMMENT ON COLUMN "public"."metadata_auto_number_config"."sequence_order" IS 'SEQUENCE规则项在列表中的排序位置';


-- 添加节点配置方式
ALTER TABLE public.flow_node_type ADD config_type varchar(4) NULL;
COMMENT ON COLUMN public.flow_node_type.config_type IS '节点配置方式：code(代码)、form(动态表单)';
ALTER TABLE public.flow_node_type ADD form_config text NULL;
COMMENT ON COLUMN public.flow_node_type.form_config IS '动态表单配置json';

-- 删除节点配置字段
ALTER TABLE public.flow_process_date_field DROP COLUMN entity_id;

