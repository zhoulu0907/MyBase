-- 应用表添加项目编码字段
ALTER TABLE app_application ADD COLUMN IF NOT EXISTS project_code VARCHAR(64);

COMMENT ON COLUMN app_application.project_code IS '项目编码';

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_app_project_code ON app_application(project_code);