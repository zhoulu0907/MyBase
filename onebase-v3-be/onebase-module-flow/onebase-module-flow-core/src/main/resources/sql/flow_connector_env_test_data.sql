-- 连接器环境配置测试数据
-- 涵盖多种连接器类型和环境，模拟真实业务场景

-- 1. HTTP API - 开发环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    1,
    'env-http-dev-001',
    'HTTP API开发环境',
    'DEV',
    'HTTP',
    'https://dev-api.example.com',
    'Bearer',
    '{"tokenType": "Bearer", "headerName": "Authorization", "prefix": "Bearer "}',
    '用于开发环境测试的HTTP API接口',
    '{"timeout": 30000, "retryTimes": 3, "enableLog": true}',
    1,
    100,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 2. HTTP API - 测试环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    2,
    'env-http-test-001',
    'HTTP API测试环境',
    'TEST',
    'HTTP',
    'https://test-api.example.com',
    'Bearer',
    '{"tokenType": "Bearer", "headerName": "Authorization", "prefix": "Bearer "}',
    '测试环境HTTP API接口，用于集成测试',
    '{"timeout": 30000, "retryTimes": 2, "enableLog": true}',
    1,
    101,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 3. HTTP API - 生产环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    3,
    'env-http-prod-001',
    'HTTP API生产环境',
    'PROD',
    'HTTP',
    'https://api.example.com',
    'APIKey',
    '{"headerName": "X-API-Key", "keyLocation": "header"}',
    '生产环境HTTP API接口',
    '{"timeout": 60000, "retryTimes": 1, "enableLog": false}',
    1,
    102,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 4. MySQL数据库 - 开发环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    4,
    'env-mysql-dev-001',
    'MySQL开发数据库',
    'DEV',
    'DATABASE_MYSQL',
    'jdbc:mysql://dev-mysql.example.com:3306/onebase_dev',
    'Basic',
    '{"username": "dev_user", "password": "dev@2025", "useSSL": false}',
    '开发环境MySQL数据库，用于开发调试',
    '{"connectionTimeout": 10000, "maxPoolSize": 10, "minIdle": 2}',
    1,
    200,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 5. MySQL数据库 - 测试环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    5,
    'env-mysql-test-001',
    'MySQL测试数据库',
    'TEST',
    'DATABASE_MYSQL',
    'jdbc:mysql://test-mysql.example.com:3306/onebase_test',
    'Basic',
    '{"username": "test_user", "password": "test@2025", "useSSL": false}',
    '测试环境MySQL数据库，用于集成测试',
    '{"connectionTimeout": 10000, "maxPoolSize": 20, "minIdle": 5}',
    1,
    201,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 6. PostgreSQL数据库 - 开发环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    6,
    'env-postgres-dev-001',
    'PostgreSQL开发数据库',
    'DEV',
    'DATABASE_POSTGRESQL',
    'jdbc:postgresql://dev-pg.example.com:5432/onebase_dev',
    'Basic',
    '{"username": "postgres", "password": "dev@pg2025", "schema": "public"}',
    '开发环境PostgreSQL数据库',
    '{"connectionTimeout": 10000, "maxPoolSize": 10, "currentSchema": "public"}',
    1,
    300,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 7. 邮件SMTP - 生产环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    7,
    'env-email-prod-001',
    '企业邮件SMTP服务',
    'PROD',
    'EMAIL_SMTP',
    'smtp://smtp.exmail.qq.com:587',
    'Plain',
    '{"username": "noreply@example.com", "password": "email@2025", "from": "OneBase系统", "useTLS": true}',
    '生产环境邮件发送服务',
    '{"port": 587, "enableStartTLS": true, "connectionTimeout": 10000}',
    1,
    400,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 8. 阿里云短信 - 生产环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    8,
    'env-sms-ali-prod-001',
    '阿里云短信服务',
    'PROD',
    'SMS_ALI',
    'https://dysmsapi.aliyuncs.com',
    'AccessKey',
    '{"accessKeyId": "LTAI5txxxxx", "accessKeySecret": "xxxxx", "regionId": "cn-hangzhou", "signName": "OneBase"}',
    '生产环境阿里云短信发送服务',
    '{"endpoint": "dysmsapi.aliyuncs.com", "version": "2017-05-25"}',
    1,
    500,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 9. FTP文件服务 - 测试环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    9,
    'env-ftp-test-001',
    'FTP文件传输服务',
    'TEST',
    'FTP',
    'ftp://test-ftp.example.com:21',
    'Basic',
    '{"username": "ftpuser", "password": "ftp@2025", "passiveMode": true}',
    '测试环境FTP文件传输服务',
    '{"connectTimeout": 30000, "dataTimeout": 60000, "encoding": "UTF-8"}',
    1,
    600,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 10. Redis缓存 - 开发环境
INSERT INTO flow_connector_env (
    id, env_uuid, env_name, env_code, type_code,
    env_url, auth_type, auth_config, description, extra_config,
    active_status, sort_order, tenant_id, application_id,
    creator, create_time, updater, update_time, deleted, lock_version
) VALUES (
    10,
    'env-redis-dev-001',
    'Redis缓存服务',
    'DEV',
    'REDIS',
    'redis://dev-redis.example.com:6379',
    'Password',
    '{"password": "redis@2025", "database": 0}',
    '开发环境Redis缓存服务',
    '{"maxTotal": 20, "maxIdle": 10, "minIdle": 2, "timeout": 3000}',
    1,
    700,
    1,
    NULL,
    1,
    CURRENT_TIMESTAMP,
    1,
    CURRENT_TIMESTAMP,
    0,
    0
);

-- 查询验证
-- SELECT env_name, type_code, env_code, env_url, auth_type, active_status
-- FROM flow_connector_env
-- ORDER BY sort_order;
