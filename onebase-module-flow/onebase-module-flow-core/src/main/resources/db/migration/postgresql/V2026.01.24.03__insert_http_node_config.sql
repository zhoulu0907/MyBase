-- HTTP类型CRM接口服务节点配置
-- 插入HTTP连接器的节点配置，包含3个动作：获取客户列表、获取客户详情、获取客户订单

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
    version,
    create_time,
    update_time
) VALUES (
    'CONNECTOR',
    'HTTP',
    'CRM',
    'HTTP类型CRM接口服务',
    'HTTP',
    'HTTP连接器，支持CRM系统的API调用',
    '支持通过HTTP协议调用CRM系统接口，包含获取客户列表、客户详情和客户订单记录三个动作。支持自定义请求头、路径参数和查询参数。',
    1,
    '{
        "baseUrl": "",
        "timeout": 30000,
        "retryTimes": 3,
        "enableLog": true
    }',
    'HTTP',
    '{
        "type": "object",
        "properties": {
            "baseUrl": {
                "type": "string",
                "title": "API基础URL",
                "description": "例如: https://api.example.com"
            },
            "timeout": {
                "type": "number",
                "title": "超时时间(ms)",
                "default": 30000
            },
            "retryTimes": {
                "type": "number",
                "title": "重试次数",
                "default": 3
            },
            "enableLog": {
                "type": "boolean",
                "title": "启用日志",
                "default": true
            }
        }
    }',
    'HTTP_API',
    '{
        "type": "HTTP",
        "title": "HTTP类型CRM接口服务",
        "properties": {
            "getCustomerList": {
                "type": "object",
                "title": "动作1：获取客户列表",
                "description": "分页查询客户列表接口",
                "x-component": "Card",
                "x-api-meta": {
                    "path": "/api/v1/customers",
                    "method": "GET",
                    "summary": "获取客户列表"
                },
                "properties": {
                    "headers": {
                        "type": "object",
                        "title": "请求头",
                        "properties": {
                            "Authorization": {
                                "type": "string",
                                "title": "认证令牌",
                                "required": true,
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "header"
                            }
                        }
                    },
                    "query": {
                        "type": "object",
                        "title": "查询参数",
                        "properties": {
                            "page": {
                                "type": "number",
                                "title": "页码",
                                "default": 1,
                                "x-decorator": "FormItem",
                                "x-component": "NumberPicker",
                                "x-param-in": "query"
                            },
                            "pageSize": {
                                "type": "number",
                                "title": "每页数量",
                                "default": 20,
                                "x-decorator": "FormItem",
                                "x-component": "NumberPicker",
                                "x-param-in": "query"
                            },
                            "search": {
                                "type": "string",
                                "title": "搜索关键词",
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "query"
                            },
                            "customerType": {
                                "type": "string",
                                "title": "客户类型",
                                "enum": [
                                    {"label": "普通客户", "value": "normal"},
                                    {"label": "VIP客户", "value": "vip"}
                                ],
                                "x-decorator": "FormItem",
                                "x-component": "Select",
                                "x-param-in": "query"
                            }
                        }
                    }
                }
            },
            "getCustomerDetail": {
                "type": "object",
                "title": "动作2：获取客户详情",
                "description": "根据ID获取客户详细信息",
                "x-component": "Card",
                "x-api-meta": {
                    "path": "/api/v1/customers/{customerId}",
                    "method": "GET",
                    "summary": "获取客户详情"
                },
                "properties": {
                    "headers": {
                        "type": "object",
                        "title": "请求头",
                        "properties": {
                            "Authorization": {
                                "type": "string",
                                "title": "认证令牌",
                                "required": true,
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "header"
                            }
                        }
                    },
                    "path": {
                        "type": "object",
                        "title": "路径参数",
                        "properties": {
                            "customerId": {
                                "type": "string",
                                "title": "客户ID",
                                "required": true,
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "path"
                            }
                        }
                    },
                    "query": {
                        "type": "object",
                        "title": "查询参数",
                        "properties": {
                            "fields": {
                                "type": "string",
                                "title": "包含字段",
                                "description": "需要返回的字段列表，逗号分隔",
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "query"
                            },
                            "detailLevel": {
                                "type": "string",
                                "title": "详情级别",
                                "enum": ["basic", "full"],
                                "default": "basic",
                                "x-decorator": "FormItem",
                                "x-component": "Select",
                                "x-param-in": "query"
                            }
                        }
                    }
                }
            },
            "getCustomerOrders": {
                "type": "object",
                "title": "动作3：获取客户订单记录",
                "description": "查询指定客户的订单历史",
                "x-component": "Card",
                "x-api-meta": {
                    "path": "/api/v1/customers/{customerId}/orders",
                    "method": "GET",
                    "summary": "获取客户订单记录"
                },
                "properties": {
                    "headers": {
                        "type": "object",
                        "title": "请求头",
                        "properties": {
                            "Authorization": {
                                "type": "string",
                                "title": "认证令牌",
                                "required": true,
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "header"
                            }
                        }
                    },
                    "path": {
                        "type": "object",
                        "title": "路径参数",
                        "properties": {
                            "customerId": {
                                "type": "string",
                                "title": "客户ID",
                                "required": true,
                                "x-decorator": "FormItem",
                                "x-component": "Input",
                                "x-param-in": "path"
                            }
                        }
                    },
                    "query": {
                        "type": "object",
                        "title": "查询参数",
                        "properties": {
                            "startDate": {
                                "type": "string",
                                "title": "开始日期",
                                "x-decorator": "FormItem",
                                "x-component": "DatePicker",
                                "x-param-in": "query"
                            },
                            "endDate": {
                                "type": "string",
                                "title": "结束日期",
                                "x-decorator": "FormItem",
                                "x-component": "DatePicker",
                                "x-param-in": "query"
                            },
                            "status": {
                                "type": "string",
                                "title": "订单状态",
                                "enum": [
                                    {"label": "待支付", "value": "pending"},
                                    {"label": "已支付", "value": "paid"},
                                    {"label": "已发货", "value": "shipped"},
                                    {"label": "已完成", "value": "completed"}
                                ],
                                "x-decorator": "FormItem",
                                "x-component": "Select",
                                "x-param-in": "query"
                            },
                            "sortBy": {
                                "type": "string",
                                "title": "排序字段",
                                "enum": ["created_at", "total_amount", "status"],
                                "default": "created_at",
                                "x-decorator": "FormItem",
                                "x-component": "Select",
                                "x-param-in": "query"
                            },
                            "sortOrder": {
                                "type": "string",
                                "title": "排序方向",
                                "enum": [
                                    {"label": "升序", "value": "asc"},
                                    {"label": "降序", "value": "desc"}
                                ],
                                "default": "desc",
                                "x-decorator": "FormItem",
                                "x-component": "Radio.Group",
                                "x-param-in": "query"
                            }
                        }
                    }
                }
            }
        }
    }',
    100,
    '1.0.0',
    NOW(),
    NOW()
) ON CONFLICT (node_code) DO UPDATE SET
    node_name = EXCLUDED.node_name,
    simple_remark = EXCLUDED.simple_remark,
    detail_description = EXCLUDED.detail_description,
    active_status = EXCLUDED.active_status,
    default_properties = EXCLUDED.default_properties,
    conn_config_type = EXCLUDED.conn_config_type,
    conn_config = EXCLUDED.conn_config,
    action_config_type = EXCLUDED.action_config_type,
    action_config = EXCLUDED.action_config,
    sort_order = EXCLUDED.sort_order,
    version = EXCLUDED.version,
    update_time = EXCLUDED.update_time;
