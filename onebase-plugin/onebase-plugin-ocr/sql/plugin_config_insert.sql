-- =====================================================
-- OneBase OCR 插件配置数据库脚本
-- 用于 onebase-server-runtime 生产环境
-- =====================================================

-- 插件配置表: plugin_config_info
-- 说明: 存储插件的配置信息,按 plugin_id 分组
-- 配置键采用通用命名,支持多种 OCR 服务商 (百度/阿里/腾讯等)

-- 1. 插入 OCR 服务商 Client ID 配置
-- 百度: API Key
-- 阿里: Access Key ID
-- 腾讯: Secret ID
INSERT INTO plugin_config_info (plugin_id, config_key, config_value, description, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (
    'onebase-plugin-ocr',                    -- 插件ID
    'client-id',                             -- 配置键 (通用)
    'your-api-key',                          -- 配置值 (请替换为真实的 API Key)
    'OCR 服务商 Client ID (API Key / Access Key ID / Secret ID)',  -- 描述
    'admin',                                  -- 创建者
    NOW(),                                    -- 创建时间
    'admin',                                  -- 更新者
    NOW(),                                    -- 更新时间
    0,                                        -- 删除标记 (0=未删除)
    1                                         -- 租户ID (请根据实际情况修改)
);

-- 2. 插入 OCR 服务商 Client Secret 配置
-- 百度: Secret Key
-- 阿里: Access Key Secret
-- 腾讯: Secret Key
INSERT INTO plugin_config_info (plugin_id, config_key, config_value, description, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (
    'onebase-plugin-ocr',
    'client-secret',                         -- 配置键 (通用)
    'your-secret-key',                       -- 配置值 (请替换为真实的 Secret Key)
    'OCR 服务商 Client Secret (Secret Key / Access Key Secret)',
    'admin',
    NOW(),
    'admin',
    NOW(),
    0,
    1
);

-- 3. 插入 OCR 服务商 API 接入点配置 (可选,如不配置则使用默认值)
-- 百度: https://aip.baidubce.com
-- 阿里: https://ocr.cn-shanghai.aliyuncs.com (根据区域调整)
-- 腾讯: https://ocr.tencentcloudapi.com
INSERT INTO plugin_config_info (plugin_id, config_key, config_value, description, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES (
    'onebase-plugin-ocr',
    'endpoint',                              -- 配置键 (通用)
    'https://aip.baidubce.com',              -- 默认使用百度接入点
    'OCR 服务商 API 接入点',
    'admin',
    NOW(),
    'admin',
    NOW(),
    0,
    1
);

-- =====================================================
-- 使用说明:
-- 1. 替换 'your-api-key' 为真实的服务商 API Key
-- 2. 替换 'your-secret-key' 为真实的服务商 Secret Key
-- 3. 根据使用的服务商修改 endpoint 值:
--    - 百度: https://aip.baidubce.com
--    - 阿里: https://ocr.cn-shanghai.aliyuncs.com
--    - 腾讯: https://ocr.tencentcloudapi.com
-- 4. 根据实际情况修改 tenant_id (租户ID)
-- 5. 如需支持多租户,为每个租户插入独立的配置记录
-- =====================================================

-- 查询验证
SELECT * FROM plugin_config_info WHERE plugin_id = 'onebase-plugin-ocr';
