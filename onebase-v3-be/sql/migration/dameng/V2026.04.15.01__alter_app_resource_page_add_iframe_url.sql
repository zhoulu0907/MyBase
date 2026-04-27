-- 达梦: 为 app_resource_page 表添加 iframe_url 字段
-- 用于 iframe 类型菜单存储嵌入 URL

ALTER TABLE app_resource_page ADD iframe_url VARCHAR(1024);

COMMENT ON COLUMN app_resource_page.iframe_url IS 'iframe 嵌入 URL';