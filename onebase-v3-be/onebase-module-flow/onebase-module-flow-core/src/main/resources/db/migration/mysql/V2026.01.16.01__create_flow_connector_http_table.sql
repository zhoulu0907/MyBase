-- HTTP连接器动作配置表
-- 用于存储HTTP请求的具体配置，与flow_connector表关联
CREATE TABLE flow_connector_http (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    application_id BIGINT NOT NULL COMMENT '应用ID',

    -- 关联连接器
    connector_uuid VARCHAR(64) NOT NULL COMMENT '所属连接器UUID',

    -- HTTP动作基本信息
    http_uuid VARCHAR(64) NOT NULL UNIQUE COMMENT 'HTTP动作UUID',
    http_name VARCHAR(100) NOT NULL COMMENT 'HTTP动作名称',
    http_code VARCHAR(100) COMMENT 'HTTP动作编码',
    description VARCHAR(500) COMMENT '动作描述',

    -- HTTP请求配置
    request_method VARCHAR(10) NOT NULL COMMENT 'HTTP方法: GET/POST/PUT/PATCH/DELETE',
    request_path VARCHAR(500) NOT NULL COMMENT '请求路径，支持变量替换如/api/users/${userId}',
    request_query TEXT COMMENT 'Query参数定义(JSON)',
    request_headers TEXT COMMENT '请求头定义(JSON)',
    request_body_type VARCHAR(20) COMMENT '请求体类型: JSON/FORM/RAW/NONE',
    request_body_template TEXT COMMENT '请求体模板，支持变量替换',

    -- 认证配置（可覆盖连接器级别的认证）
    auth_type VARCHAR(50) COMMENT '认证方式: NONE/BASIC/TOKEN/OAUTH2/CUSTOM_SIGNATURE/INHERIT',
    auth_config TEXT COMMENT '认证配置(JSON)',

    -- 响应处理
    response_mapping TEXT COMMENT '响应字段映射定义(JSON)',
    success_condition TEXT COMMENT '成功条件表达式(JSON)',

    -- 输入输出Schema（供前端生成表单）
    input_schema TEXT COMMENT '输入参数Schema(JSON)',
    output_schema TEXT COMMENT '输出参数Schema(JSON)',

    -- 高级配置
    timeout INT COMMENT '超时时间(ms)，覆盖连接器配置',
    retry_count INT COMMENT '重试次数，覆盖连接器配置',
    mock_response TEXT COMMENT 'Mock响应（用于测试）',

    -- 状态管理
    active_status INT DEFAULT 1 COMMENT '启用状态: 0-禁用, 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',

    -- 审计字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(100),
    update_by VARCHAR(100),

    INDEX idx_connector_uuid (connector_uuid),
    INDEX idx_application_id (application_id),
    INDEX idx_http_code (http_code),
    INDEX idx_active_status (active_status),
    INDEX idx_http_uuid (http_uuid)
) COMMENT 'HTTP连接器动作配置表';

-- 插入示例数据
INSERT INTO flow_connector_http (
    application_id, connector_uuid, http_uuid, http_name, http_code, description,
    request_method, request_path, request_body_type, auth_type,
    input_schema, output_schema, active_status, sort_order
) VALUES (
    1,
    'uuid-http-demo-001',
    'uuid-http-get-user-001',
    '获取用户信息',
    'GET_USER_INFO',
    '根据用户ID获取用户详细信息',
    'GET',
    '/api/v1/users/${userId}',
    'NONE',
    'INHERIT',
    '[{"fieldName":"userId","fieldType":"STRING","fieldDesc":"用户ID","required":true,"example":"12345"}]',
    '[{"fieldName":"userId","fieldType":"STRING","fieldDesc":"用户ID"},{"fieldName":"userName","fieldType":"STRING","fieldDesc":"用户姓名"},{"fieldName":"userEmail","fieldType":"STRING","fieldDesc":"用户邮箱"}]',
    1,
    0
);
