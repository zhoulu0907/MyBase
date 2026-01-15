-- 通用连接器初始化脚本
-- 创建连接器定义和节点配置数据

-- 1. 163邮箱连接器定义
INSERT INTO flow_connector (
    connector_uuid, 
    connector_name, 
    type_code, 
    description, 
    config, 
    version, 
    status, 
    create_time, 
    update_time
) VALUES (
    'uuid-163-email-001', 
    '163邮箱连接器', 
    'EMAIL_163', 
    '163邮箱发送连接器，支持文本和HTML邮件发送', 
    '{
        "smtpHost": "smtp.163.com",
        "smtpPort": 465,
        "useSSL": true,
        "useTLS": false,
        "timeout": 30000,
        "connectionTimeout": 10000,
        "maxRetries": 3
    }', 
    '1.0.0', 
    1, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET connector_name = VALUES(connector_name),
        description = VALUES(description),
        config = VALUES(config),
        version = VALUES(version),
        status = VALUES(status),
        update_time = NOW();

-- 2. 阿里云短信连接器定义
INSERT INTO flow_connector (
    connector_uuid, 
    connector_name, 
    type_code, 
    description, 
    config, 
    version, 
    status, 
    create_time, 
    update_time
) VALUES (
    'uuid-sms-ali-001', 
    '阿里云短信连接器', 
    'SMS_ALI', 
    '阿里云短信发送连接器，支持模板短信和验证码短信', 
    '{
        "regionId": "cn-hangzhou",
        "signName": "OneBase",
        "endpoint": "https://dysmsapi.aliyuncs.com",
        "apiVersion": "2017-05-25",
        "timeout": 10000,
        "maxRetries": 3
    }', 
    '1.0.0', 
    1, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET connector_name = VALUES(connector_name),
        description = VALUES(description),
        config = VALUES(config),
        version = VALUES(version),
        status = VALUES(status),
        update_time = NOW();

-- 3. MySQL数据库连接器定义
INSERT INTO flow_connector (
    connector_uuid, 
    connector_name, 
    type_code, 
    description, 
    config, 
    version, 
    status, 
    create_time, 
    update_time
) VALUES (
    'uuid-database-mysql-001', 
    'MySQL数据库连接器', 
    'DATABASE_MYSQL', 
    'MySQL数据库连接器，支持查询和更新操作', 
    '{
        "driverClass": "com.mysql.cj.jdbc.Driver",
        "connectionTimeout": 30000,
        "queryTimeout": 60000,
        "maxRetries": 3,
        "autoReconnect": true,
        "useUnicode": true,
        "characterEncoding": "UTF-8"
    }', 
    '1.0.0', 
    1, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET connector_name = VALUES(connector_name),
        description = VALUES(description),
        config = VALUES(config),
        version = VALUES(version),
        status = VALUES(status),
        update_time = NOW();

-- 4. 163邮箱发送节点配置
INSERT INTO flow_node_config (
    level1_code, 
    level2_code, 
    level3_code, 
    node_name, 
    node_code, 
    simple_remark, 
    detail_description, 
    active_status, 
    default_properties, 
    conn_config_type, 
    conn_config, 
    action_config_type, 
    action_config, 
    sort_order, 
    create_time, 
    update_time
) VALUES (
    'CONNECTOR', 
    'EMAIL', 
    '163', 
    '163邮箱发送', 
    'EMAIL_163_SEND', 
    '163邮箱发送节点', 
    '用于发送163邮件，支持文本和HTML格式', 
    1, 
    '{
        "to": ["user@example.com"],
        "subject": "测试邮件",
        "from": "noreply@163.com",
        "cc": [],
        "bcc": []
    }', 
    'EMAIL', 
    '{
        "smtpHost": "smtp.163.com",
        "smtpPort": 465,
        "useSSL": true
    }', 
    'EMAIL_SEND', 
    '{
        "templateType": "TEXT",
        "charset": "UTF-8",
        "priority": 3,
        "requireReadReceipt": false
    }', 
    100, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET node_name = VALUES(node_name),
        simple_remark = VALUES(simple_remark),
        detail_description = VALUES(detail_description),
        active_status = VALUES(active_status),
        default_properties = VALUES(default_properties),
        conn_config_type = VALUES(conn_config_type),
        conn_config = VALUES(conn_config),
        action_config_type = VALUES(action_config_type),
        action_config = VALUES(action_config),
        sort_order = VALUES(sort_order),
        update_time = NOW();

-- 5. 阿里云短信发送节点配置
INSERT INTO flow_node_config (
    level1_code, 
    level2_code, 
    level3_code, 
    node_name, 
    node_code, 
    simple_remark, 
    detail_description, 
    active_status, 
    default_properties, 
    conn_config_type, 
    conn_config, 
    action_config_type, 
    action_config, 
    sort_order, 
    create_time, 
    update_time
) VALUES (
    'CONNECTOR', 
    'SMS', 
    'ALI', 
    '阿里云短信发送', 
    'SMS_ALI_SEND', 
    '阿里云短信发送节点', 
    '用于发送阿里云短信，支持模板短信', 
    1, 
    '{
        "phoneNumbers": ["13800138000"],
        "templateCode": "SMS_123456",
        "templateParam": {
            "code": "123456",
            "product": "OneBase"
        }
    }', 
    'SMS', 
    '{
        "regionId": "cn-hangzhou",
        "signName": "OneBase"
    }', 
    'SMS_SEND', 
    '{
        "templateType": "TEMPLATE",
        "timeout": 10000,
        "maxRetries": 3
    }', 
    200, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET node_name = VALUES(node_name),
        simple_remark = VALUES(simple_remark),
        detail_description = VALUES(detail_description),
        active_status = VALUES(active_status),
        default_properties = VALUES(default_properties),
        conn_config_type = VALUES(conn_config_type),
        conn_config = VALUES(conn_config),
        action_config_type = VALUES(action_config_type),
        action_config = VALUES(action_config),
        sort_order = VALUES(sort_order),
        update_time = NOW();

-- 6. MySQL数据库查询节点配置
INSERT INTO flow_node_config (
    level1_code, 
    level2_code, 
    level3_code, 
    node_name, 
    node_code, 
    simple_remark, 
    detail_description, 
    active_status, 
    default_properties, 
    conn_config_type, 
    conn_config, 
    action_config_type, 
    action_config, 
    sort_order, 
    create_time, 
    update_time
) VALUES (
    'CONNECTOR', 
    'DATABASE', 
    'MYSQL', 
    'MySQL数据库查询', 
    'DATABASE_MYSQL_QUERY', 
    'MySQL数据库查询节点', 
    '用于执行MySQL数据库查询操作', 
    1, 
    '{
        "sql": "SELECT * FROM users WHERE status = ?",
        "parameters": ["active"],
        "resultKey": "queryResult"
    }', 
    'DATABASE', 
    '{
        "driverClass": "com.mysql.cj.jdbc.Driver",
        "connectionTimeout": 30000
    }', 
    'DATABASE_QUERY', 
    '{
        "operationType": "QUERY",
        "fetchSize": 100,
        "timeout": 60000
    }', 
    300, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET node_name = VALUES(node_name),
        simple_remark = VALUES(simple_remark),
        detail_description = VALUES(detail_description),
        active_status = VALUES(active_status),
        default_properties = VALUES(default_properties),
        conn_config_type = VALUES(conn_config_type),
        conn_config = VALUES(conn_config),
        action_config_type = VALUES(action_config_type),
        action_config = VALUES(action_config),
        sort_order = VALUES(sort_order),
        update_time = NOW();

-- 7. MySQL数据库更新节点配置
INSERT INTO flow_node_config (
    level1_code, 
    level2_code, 
    level3_code, 
    node_name, 
    node_code, 
    simple_remark, 
    detail_description, 
    active_status, 
    default_properties, 
    conn_config_type, 
    conn_config, 
    action_config_type, 
    action_config, 
    sort_order, 
    create_time, 
    update_time
) VALUES (
    'CONNECTOR', 
    'DATABASE', 
    'MYSQL', 
    'MySQL数据库更新', 
    'DATABASE_MYSQL_UPDATE', 
    'MySQL数据库更新节点', 
    '用于执行MySQL数据库更新操作', 
    1, 
    '{
        "sql": "UPDATE users SET last_login_time = ? WHERE user_id = ?",
        "parameters": ["NOW()", 123],
        "resultKey": "updateResult"
    }', 
    'DATABASE', 
    '{
        "driverClass": "com.mysql.cj.jdbc.Driver",
        "connectionTimeout": 30000
    }', 
    'DATABASE_UPDATE', 
    '{
        "operationType": "UPDATE",
        "batchSize": 100,
        "timeout": 60000
    }', 
    400, 
    NOW(), 
    NOW()
) ON DUPLICATE KEY UPDATE 
    SET node_name = VALUES(node_name),
        simple_remark = VALUES(simple_remark),
        detail_description = VALUES(detail_description),
        active_status = VALUES(active_status),
        default_properties = VALUES(default_properties),
        conn_config_type = VALUES(conn_config_type),
        conn_config = VALUES(conn_config),
        action_config_type = VALUES(action_config_type),
        action_config = VALUES(action_config),
        sort_order = VALUES(sort_order),
        update_time = NOW();

-- 提交事务
COMMIT;