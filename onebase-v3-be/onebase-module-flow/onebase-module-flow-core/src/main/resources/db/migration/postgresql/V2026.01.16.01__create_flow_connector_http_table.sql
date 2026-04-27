-- HTTP连接器动作配置表
-- 用于存储HTTP请求的具体配置，与flow_connector表关联
CREATE TABLE flow_connector_http (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    connector_uuid VARCHAR(64) NOT NULL,
    http_uuid VARCHAR(64) NOT NULL UNIQUE,
    http_name VARCHAR(100) NOT NULL,
    http_code VARCHAR(100),
    description VARCHAR(500),
    request_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(500) NOT NULL,
    request_query TEXT,
    request_headers TEXT,
    request_body_type VARCHAR(20),
    request_body_template TEXT,
    auth_type VARCHAR(50),
    auth_config TEXT,
    response_mapping TEXT,
    success_condition TEXT,
    input_schema TEXT,
    output_schema TEXT,
    timeout INTEGER,
    retry_count INTEGER,
    mock_response TEXT,
    active_status INTEGER DEFAULT 1,
    sort_order INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(100),
    update_by VARCHAR(100)
);

-- 创建索引
CREATE INDEX idx_flow_connector_http_connector_uuid ON flow_connector_http(connector_uuid);
CREATE INDEX idx_flow_connector_http_application_id ON flow_connector_http(application_id);
CREATE INDEX idx_flow_connector_http_http_code ON flow_connector_http(http_code);
CREATE INDEX idx_flow_connector_http_active_status ON flow_connector_http(active_status);
CREATE INDEX idx_flow_connector_http_http_uuid ON flow_connector_http(http_uuid);

-- 表注释
COMMENT ON TABLE flow_connector_http IS 'HTTP连接器动作配置表';
COMMENT ON COLUMN flow_connector_http.id IS '主键ID';
COMMENT ON COLUMN flow_connector_http.application_id IS '应用ID';
COMMENT ON COLUMN flow_connector_http.connector_uuid IS '所属连接器UUID';
COMMENT ON COLUMN flow_connector_http.http_uuid IS 'HTTP动作UUID';
COMMENT ON COLUMN flow_connector_http.http_name IS 'HTTP动作名称';
COMMENT ON COLUMN flow_connector_http.http_code IS 'HTTP动作编码';
COMMENT ON COLUMN flow_connector_http.description IS '动作描述';
COMMENT ON COLUMN flow_connector_http.request_method IS 'HTTP方法: GET/POST/PUT/PATCH/DELETE';
COMMENT ON COLUMN flow_connector_http.request_path IS '请求路径，支持变量替换如/api/users/${userId}';
COMMENT ON COLUMN flow_connector_http.request_query IS 'Query参数定义(JSON)';
COMMENT ON COLUMN flow_connector_http.request_headers IS '请求头定义(JSON)';
COMMENT ON COLUMN flow_connector_http.request_body_type IS '请求体类型: JSON/FORM/RAW/NONE';
COMMENT ON COLUMN flow_connector_http.request_body_template IS '请求体模板，支持变量替换';
COMMENT ON COLUMN flow_connector_http.auth_type IS '认证方式: NONE/BASIC/TOKEN/OAUTH2/CUSTOM_SIGNATURE/INHERIT';
COMMENT ON COLUMN flow_connector_http.auth_config IS '认证配置(JSON)';
COMMENT ON COLUMN flow_connector_http.response_mapping IS '响应字段映射定义(JSON)';
COMMENT ON COLUMN flow_connector_http.success_condition IS '成功条件表达式(JSON)';
COMMENT ON COLUMN flow_connector_http.input_schema IS '输入参数Schema(JSON)';
COMMENT ON COLUMN flow_connector_http.output_schema IS '输出参数Schema(JSON)';
COMMENT ON COLUMN flow_connector_http.timeout IS '超时时间(ms)，覆盖连接器配置';
COMMENT ON COLUMN flow_connector_http.retry_count IS '重试次数，覆盖连接器配置';
COMMENT ON COLUMN flow_connector_http.mock_response IS 'Mock响应（用于测试）';
COMMENT ON COLUMN flow_connector_http.active_status IS '启用状态: 0-禁用, 1-启用';
COMMENT ON COLUMN flow_connector_http.sort_order IS '排序';
COMMENT ON COLUMN flow_connector_http.create_time IS '创建时间';
COMMENT ON COLUMN flow_connector_http.update_time IS '更新时间';
COMMENT ON COLUMN flow_connector_http.create_by IS '创建人';
COMMENT ON COLUMN flow_connector_http.update_by IS '更新人';

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
