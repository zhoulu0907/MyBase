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
