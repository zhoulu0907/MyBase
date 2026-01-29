-- V2026.01.27.01__add_config_to_flow_connector_env.sql
-- 为 flow_connector_env 表重命名 extra_config 为 config，用于存储动作配置

-- 注意：如果 extra_config 列已存在，重命名为 config
-- 如果 extra_config 列不存在（已被手动重命名），则跳过
-- 使用 DO 块来实现条件重命名
DO $$
BEGIN
    -- 检查 extra_config 列是否存在
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'flow_connector_env'
        AND column_name = 'extra_config'
    ) THEN
        -- 重命名列
        ALTER TABLE flow_connector_env
        RENAME COLUMN extra_config TO config;
    ELSE
        -- extra_config 列不存在，可能已被手动重命名
        -- 检查 config 列是否已存在
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'flow_connector_env'
            AND column_name = 'config'
        ) THEN
            -- config 列也不存在，添加新列
            ALTER TABLE flow_connector_env
            ADD COLUMN config TEXT;
        END IF;
    END IF;
END $$;

-- 添加或更新字段注释
COMMENT ON COLUMN flow_connector_env.config IS '动作配置（JSON格式），包含 actions（列表字段）和 actionDetails（详细配置）';

-- 注意：未创建索引，原因：
-- 1. config 字段为 TEXT 类型，主要用于存储，不用于 WHERE 条件查询
-- 2. 项目中类似的 JSON 字段（auth_config）均未创建索引
-- 3. 如果未来有 JSON 内容查询需求，可考虑添加 GIN 索引
