-- 更新脱敏字段配置选项，添加 nickname 和 username
-- 适用于 PostgreSQL
UPDATE infra_security_config_template
SET options = '{"mobile": "手机号", "email": "邮箱", "nickname": "昵称", "username": "用户名"}'
WHERE config_key = 'desensitizedField';