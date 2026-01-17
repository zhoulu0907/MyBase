# HTTP连接器实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**目标:** 实现OneBase v3.0 Flow流程引擎的HTTP连接器功能，提供标准化的REST API调用能力，支持多种认证方式、变量替换、SSRF防护等企业级特性。

**架构:** 采用与Script连接器一致的架构模式，使用独立的flow_connector_http表存储HTTP动作配置，通过HttpNodeComponent在流程中执行HTTP请求，支持GET/POST/PUT/PATCH/DELETE方法和5种认证方式（无认证/Basic Auth/Token/OAuth2/自定义签名）。

**技术栈:** Java 17+, Spring Boot, MyBatis-Flex, LiteFlow, Apache HttpClient 5, JSONPath, JUnit 5

---

## Task 1: 创建flow_connector_http数据库表

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/resources/db/migration/mysql/V2026.01.16.01__create_flow_connector_http_table.sql`

**Step 1: 编写数据库迁移脚本**

```sql
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
```

**Step 2: 验证SQL语法**

Run: `mysql --help | grep "Ver"`
Expected: MySQL客户端已安装

**Step 3: 提交迁移脚本**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/resources/db/migration/mysql/V2026.01.16.01__create_flow_connector_http_table.sql
git commit -m "feat(flow): 创建flow_connector_http表用于HTTP连接器动作配置"
```

---

## Task 2: 创建FlowConnectorHttpDO数据对象

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/dataobject/FlowConnectorHttpDO.java`

**Step 1: 编写FlowConnectorHttpDO类**

```java
package com.cmsr.onebase.module.flow.core.dal.dataobject;

import com.cmsr.onebase.framework.orm.entity.BaseAppEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * HTTP连接器动作配置数据对象
 *
 * <p>对应表: flow_connector_http
 * 存储HTTP请求的具体配置，与flow_connector表关联
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "flow_connector_http")
public class FlowConnectorHttpDO extends BaseAppEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属连接器UUID
     * 关联到 flow_connector.connector_uuid
     */
    @Column(value = "connector_uuid")
    private String connectorUuid;

    /**
     * HTTP动作UUID
     * 全局唯一标识，用于流程定义中引用
     */
    @Column(value = "http_uuid")
    private String httpUuid;

    /**
     * HTTP动作名称
     * 如: "获取用户信息"、"创建订单"
     */
    @Column(value = "http_name")
    private String httpName;

    /**
     * HTTP动作编码
     * 如: "GET_USER_INFO"、"CREATE_ORDER"
     */
    @Column(value = "http_code")
    private String httpCode;

    /**
     * 动作描述
     */
    @Column(value = "description")
    private String description;

    /**
     * HTTP请求方法
     * GET/POST/PUT/PATCH/DELETE
     */
    @Column(value = "request_method")
    private String requestMethod;

    /**
     * 请求路径
     * 支持变量替换，如: /api/v1/users/${userId}
     */
    @Column(value = "request_path")
    private String requestPath;

    /**
     * Query参数定义
     * JSON格式: List<HttpParameter>
     */
    @Column(value = "request_query")
    private String requestQuery;

    /**
     * 请求头定义
     * JSON格式: List<HttpHeader>
     */
    @Column(value = "request_headers")
    private String requestHeaders;

    /**
     * 请求体类型
     * JSON/FORM/RAW/NONE
     */
    @Column(value = "request_body_type")
    private String requestBodyType;

    /**
     * 请求体模板
     * 支持变量替换
     */
    @Column(value = "request_body_template")
    private String requestBodyTemplate;

    /**
     * 认证方式
     * NONE/BASIC/TOKEN/OAUTH2/CUSTOM_SIGNATURE/INHERIT
     */
    @Column(value = "auth_type")
    private String authType;

    /**
     * 认证配置
     * JSON格式
     */
    @Column(value = "auth_config")
    private String authConfig;

    /**
     * 响应字段映射定义
     * JSON格式
     */
    @Column(value = "response_mapping")
    private String responseMapping;

    /**
     * 成功条件表达式
     * JSON格式
     */
    @Column(value = "success_condition")
    private String successCondition;

    /**
     * 输入参数Schema
     * JSON格式，供前端生成表单
     */
    @Column(value = "input_schema")
    private String inputSchema;

    /**
     * 输出参数Schema
     * JSON格式
     */
    @Column(value = "output_schema")
    private String outputSchema;

    /**
     * 超时时间（毫秒）
     * 覆盖连接器配置
     */
    @Column(value = "timeout")
    private Integer timeout;

    /**
     * 重试次数
     * 覆盖连接器配置
     */
    @Column(value = "retry_count")
    private Integer retryCount;

    /**
     * Mock响应
     * 用于测试
     */
    @Column(value = "mock_response")
    private String mockResponse;

    /**
     * 启用状态
     * 0-禁用, 1-启用
     */
    @Column(value = "active_status")
    private Integer activeStatus;

    /**
     * 排序
     */
    @Column(value = "sort_order")
    private Integer sortOrder;
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交数据对象**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/dataobject/FlowConnectorHttpDO.java
git commit -m "feat(flow): 添加FlowConnectorHttpDO数据对象"
```

---

## Task 3: 创建FlowConnectorHttpTableDef表定义

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/dataobject/table/FlowConnectorHttpTableDef.java`

**Step 1: 编写FlowConnectorHttpTableDef类**

```java
package com.cmsr.onebase.module.flow.core.dal.dataobject.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.util.Date;

/**
 * flow_connector_http 表定义
 *
 * @author zhoulu
 * @since 2026-01-16
 */
public class FlowConnectorHttpTableDef extends TableDef {

    /**
     * 单例实例
     */
    public static final FlowConnectorHttpTableDef FLOW_CONNECTOR_HTTP = new FlowConnectorHttpTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 应用ID
     */
    public final QueryColumn APPLICATION_ID = new QueryColumn(this, "application_id");

    /**
     * 所属连接器UUID
     */
    public final QueryColumn CONNECTOR_UUID = new QueryColumn(this, "connector_uuid");

    /**
     * HTTP动作UUID
     */
    public final QueryColumn HTTP_UUID = new QueryColumn(this, "http_uuid");

    /**
     * HTTP动作名称
     */
    public final QueryColumn HTTP_NAME = new QueryColumn(this, "http_name");

    /**
     * HTTP动作编码
     */
    public final QueryColumn HTTP_CODE = new QueryColumn(this, "http_code");

    /**
     * 动作描述
     */
    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    /**
     * HTTP请求方法
     */
    public final QueryColumn REQUEST_METHOD = new QueryColumn(this, "request_method");

    /**
     * 请求路径
     */
    public final QueryColumn REQUEST_PATH = new QueryColumn(this, "request_path");

    /**
     * Query参数定义
     */
    public final QueryColumn REQUEST_QUERY = new QueryColumn(this, "request_query");

    /**
     * 请求头定义
     */
    public final QueryColumn REQUEST_HEADERS = new QueryColumn(this, "request_headers");

    /**
     * 请求体类型
     */
    public final QueryColumn REQUEST_BODY_TYPE = new QueryColumn(this, "request_body_type");

    /**
     * 请求体模板
     */
    public final QueryColumn REQUEST_BODY_TEMPLATE = new QueryColumn(this, "request_body_template");

    /**
     * 认证方式
     */
    public final QueryColumn AUTH_TYPE = new QueryColumn(this, "auth_type");

    /**
     * 认证配置
     */
    public final QueryColumn AUTH_CONFIG = new QueryColumn(this, "auth_config");

    /**
     * 响应字段映射定义
     */
    public final QueryColumn RESPONSE_MAPPING = new QueryColumn(this, "response_mapping");

    /**
     * 成功条件表达式
     */
    public final QueryColumn SUCCESS_CONDITION = new QueryColumn(this, "success_condition");

    /**
     * 输入参数Schema
     */
    public final QueryColumn INPUT_SCHEMA = new QueryColumn(this, "input_schema");

    /**
     * 输出参数Schema
     */
    public final QueryColumn OUTPUT_SCHEMA = new QueryColumn(this, "output_schema");

    /**
     * 超时时间
     */
    public final QueryColumn TIMEOUT = new QueryColumn(this, "timeout");

    /**
     * 重试次数
     */
    public final QueryColumn RETRY_COUNT = new QueryColumn(this, "retry_count");

    /**
     * Mock响应
     */
    public final QueryColumn MOCK_RESPONSE = new QueryColumn(this, "mock_response");

    /**
     * 启用状态
     */
    public final QueryColumn ACTIVE_STATUS = new QueryColumn(this, "active_status");

    /**
     * 排序
     */
    public final QueryColumn SORT_ORDER = new QueryColumn(this, "sort_order");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 创建人
     */
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    /**
     * 更新人
     */
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * 所有列
     */
    public final QueryColumn[] ALL_COLUMNS = new QueryColumn[]{
        ID, APPLICATION_ID, CONNECTOR_UUID, HTTP_UUID, HTTP_NAME, HTTP_CODE,
        DESCRIPTION, REQUEST_METHOD, REQUEST_PATH, REQUEST_QUERY, REQUEST_HEADERS,
        REQUEST_BODY_TYPE, REQUEST_BODY_TEMPLATE, AUTH_TYPE, AUTH_CONFIG,
        RESPONSE_MAPPING, SUCCESS_CONDITION, INPUT_SCHEMA, OUTPUT_SCHEMA,
        TIMEOUT, RETRY_COUNT, MOCK_RESPONSE, ACTIVE_STATUS, SORT_ORDER,
        CREATE_TIME, UPDATE_TIME, CREATE_BY, UPDATE_BY
    };

    /**
     * 构造函数
     */
    public FlowConnectorHttpTableDef() {
        super("", "flow_connector_http");
    }

    /**
     * 构造函数
     *
     * @param tableName 表名
     * @param alias    别名
     */
    public FlowConnectorHttpTableDef(String tableName, String alias) {
        super(tableName, alias);
    }
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交表定义**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/dataobject/table/FlowConnectorHttpTableDef.java
git commit -m "feat(flow): 添加FlowConnectorHttpTableDef表定义"
```

---

## Task 4: 创建FlowConnectorHttpMapper接口

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/mapper/FlowConnectorHttpMapper.java`

**Step 1: 编写FlowConnectorHttpMapper接口**

```java
package com.cmsr.onebase.module.flow.core.dal.mapper;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * HTTP连接器动作配置Mapper
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Mapper
public interface FlowConnectorHttpMapper extends BaseMapper<FlowConnectorHttpDO> {

}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交Mapper接口**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/mapper/FlowConnectorHttpMapper.java
git commit -m "feat(flow): 添加FlowConnectorHttpMapper接口"
```

---

## Task 5: 创建FlowConnectorHttpRepository

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/database/FlowConnectorHttpRepository.java`

**Step 1: 编写FlowConnectorHttpRepository类**

```java
package com.cmsr.onebase.module.flow.core.dal.database;

import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef;
import com.cmsr.onebase.module.flow.core.dal.mapper.FlowConnectorHttpMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef.FLOW_CONNECTOR_HTTP;

/**
 * HTTP连接器动作配置Repository
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Component
public class FlowConnectorHttpRepository extends ServiceImpl<FlowConnectorHttpMapper, FlowConnectorHttpDO> {

    /**
     * 根据应用ID和UUID查询HTTP动作配置
     *
     * @param applicationId 应用ID
     * @param httpUuid      HTTP动作UUID
     * @return HTTP动作配置
     */
    public FlowConnectorHttpDO findByApplicationAndUuid(Long applicationId, String httpUuid) {
        return QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.HTTP_UUID.eq(httpUuid))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .one();
    }

    /**
     * 根据连接器UUID查询HTTP动作列表
     *
     * @param connectorUuid 连接器UUID
     * @return HTTP动作列表
     */
    public java.util.List<FlowConnectorHttpDO> findByConnectorUuid(String connectorUuid) {
        return QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_HTTP.SORT_ORDER.asc())
                .list();
    }

    /**
     * 根据应用ID和连接器UUID查询HTTP动作列表
     *
     * @param applicationId 应用ID
     * @param connectorUuid 连接器UUID
     * @return HTTP动作列表
     */
    public java.util.List<FlowConnectorHttpDO> findByApplicationAndConnectorUuid(
            Long applicationId, String connectorUuid) {
        return QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.CONNECTOR_UUID.eq(connectorUuid))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_HTTP.SORT_ORDER.asc())
                .list();
    }

    /**
     * 根据应用ID和HTTP动作编码查询
     *
     * @param applicationId 应用ID
     * @param httpCode      HTTP动作编码
     * @return HTTP动作配置
     */
    public FlowConnectorHttpDO findByApplicationAndCode(Long applicationId, String httpCode) {
        return QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.HTTP_CODE.eq(httpCode))
                .and(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .one();
    }
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交Repository**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/dal/database/FlowConnectorHttpRepository.java
git commit -m "feat(flow): 添加FlowConnectorHttpRepository数据访问层"
```

---

## Task 6: 创建辅助数据类

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/http/HttpParameter.java`
- Create: `onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/http/HttpHeader.java`
- Create: `onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/http/ParameterSchema.java`
- Create: `onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/http/SuccessCondition.java`

**Step 1: 编写HttpParameter类**

```java
package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;

/**
 * HTTP参数定义
 *
 * <p>用于定义HTTP请求的参数，支持Path、Query、Header、Body四种位置
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class HttpParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名
     */
    private String name;

    /**
     * 参数类型
     * STRING/NUMBER/BOOLEAN/OBJECT/ARRAY
     */
    private String type;

    /**
     * 参数位置
     * PATH/QUERY/HEADER/BODY
     */
    private String location;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 示例值
     */
    private String example;
}
```

**Step 2: 编写HttpHeader类**

```java
package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;

/**
 * HTTP请求头定义
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class HttpHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Header名
     */
    private String name;

    /**
     * Header值（支持变量）
     */
    private String value;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否必填
     */
    private Boolean required;
}
```

**Step 3: 编写ParameterSchema类**

```java
package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 参数Schema定义
 *
 * <p>用于描述输入输出参数的结构，供前端生成表单
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class ParameterSchema implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     * STRING/NUMBER/BOOLEAN/OBJECT/ARRAY
     */
    private String fieldType;

    /**
     * 字段描述
     */
    private String fieldDesc;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 示例值
     */
    private String example;

    /**
     * 嵌套字段（用于OBJECT和ARRAY类型）
     */
    private List<ParameterSchema> nestedFields;
}
```

**Step 4: 编写SuccessCondition类**

```java
package com.cmsr.onebase.module.flow.context.graph.nodes.http;

import lombok.Data;

import java.io.Serializable;

/**
 * 成功条件定义
 *
 * <p>用于定义HTTP请求成功的条件
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class SuccessCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功条件表达式（JSONPath）
     */
    private String expression;

    /**
     * 期望的HTTP状态码
     */
    private Integer expectStatusCode;

    /**
     * 响应体路径
     */
    private String responseBodyPath;

    /**
     * 描述
     */
    private String description;
}
```

**Step 5: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-context && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 6: 提交辅助数据类**

```bash
git add onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/http/
git commit -m "feat(flow): 添加HTTP连接器辅助数据类"
```

---

## Task 7: 创建HttpConnectorRequest VO

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/CreateHttpActionReqVO.java`
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/UpdateHttpActionReqVO.java`
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/HttpActionVO.java`

**Step 1: 编写CreateHttpActionReqVO类**

```java
package com.cmsr.onebase.module.flow.core.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建HTTP动作请求VO
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class CreateHttpActionReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属连接器UUID
     */
    @NotBlank(message = "连接器UUID不能为空")
    private String connectorUuid;

    /**
     * HTTP动作名称
     */
    @NotBlank(message = "HTTP动作名称不能为空")
    private String httpName;

    /**
     * HTTP动作编码
     */
    private String httpCode;

    /**
     * 动作描述
     */
    private String description;

    /**
     * HTTP请求方法
     */
    @NotBlank(message = "HTTP请求方法不能为空")
    private String requestMethod;

    /**
     * 请求路径
     */
    @NotBlank(message = "请求路径不能为空")
    private String requestPath;

    /**
     * Query参数
     */
    private List<Object> requestQuery;

    /**
     * 请求头
     */
    private List<Object> requestHeaders;

    /**
     * 请求体类型
     */
    private String requestBodyType;

    /**
     * 请求体模板
     */
    private String requestBodyTemplate;

    /**
     * 认证方式
     */
    private String authType;

    /**
     * 认证配置
     */
    private Object authConfig;

    /**
     * 响应映射
     */
    private Object responseMapping;

    /**
     * 成功条件
     */
    private Object successCondition;

    /**
     * 输入Schema
     */
    private List<Object> inputSchema;

    /**
     * 输出Schema
     */
    private List<Object> outputSchema;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 排序
     */
    private Integer sortOrder;
}
```

**Step 2: 编写UpdateHttpActionReqVO类**

```java
package com.cmsr.onebase.module.flow.core.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 更新HTTP动作请求VO
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class UpdateHttpActionReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * HTTP动作名称
     */
    private String httpName;

    /**
     * HTTP动作编码
     */
    private String httpCode;

    /**
     * 动作描述
     */
    private String description;

    /**
     * HTTP请求方法
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String requestPath;

    /**
     * Query参数
     */
    private List<Object> requestQuery;

    /**
     * 请求头
     */
    private List<Object> requestHeaders;

    /**
     * 请求体类型
     */
    private String requestBodyType;

    /**
     * 请求体模板
     */
    private String requestBodyTemplate;

    /**
     * 认证方式
     */
    private String authType;

    /**
     * 认证配置
     */
    private Object authConfig;

    /**
     * 响应映射
     */
    private Object responseMapping;

    /**
     * 成功条件
     */
    private Object successCondition;

    /**
     * 输入Schema
     */
    private List<Object> inputSchema;

    /**
     * 输出Schema
     */
    private List<Object> outputSchema;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 启用状态
     */
    private Integer activeStatus;

    /**
     * 排序
     */
    private Integer sortOrder;
}
```

**Step 3: 编写HttpActionVO类**

```java
package com.cmsr.onebase.module.flow.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * HTTP动作响应VO
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class HttpActionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 应用ID
     */
    private Long applicationId;

    /**
     * 所属连接器UUID
     */
    private String connectorUuid;

    /**
     * HTTP动作UUID
     */
    private String httpUuid;

    /**
     * HTTP动作名称
     */
    private String httpName;

    /**
     * HTTP动作编码
     */
    private String httpCode;

    /**
     * 动作描述
     */
    private String description;

    /**
     * HTTP请求方法
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String requestPath;

    /**
     * Query参数
     */
    private List<Object> requestQuery;

    /**
     * 请求头
     */
    private List<Object> requestHeaders;

    /**
     * 请求体类型
     */
    private String requestBodyType;

    /**
     * 请求体模板
     */
    private String requestBodyTemplate;

    /**
     * 认证方式
     */
    private String authType;

    /**
     * 认证配置
     */
    private Object authConfig;

    /**
     * 响应映射
     */
    private Object responseMapping;

    /**
     * 成功条件
     */
    private Object successCondition;

    /**
     * 输入Schema
     */
    private List<Object> inputSchema;

    /**
     * 输出Schema
     */
    private List<Object> outputSchema;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * Mock响应
     */
    private String mockResponse;

    /**
     * 启用状态
     */
    private Integer activeStatus;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;
}
```

**Step 4: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 5: 提交VO类**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/CreateHttpActionReqVO.java onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/UpdateHttpActionReqVO.java onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/HttpActionVO.java
git commit -m "feat(flow): 添加HTTP连接器VO类"
```

---

## Task 8: 创建PageConnectorHttpReqVO分页查询VO

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/PageConnectorHttpReqVO.java`

**Step 1: 编写PageConnectorHttpReqVO类**

```java
package com.cmsr.onebase.module.flow.core.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询HTTP动作请求VO
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Data
public class PageConnectorHttpReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 连接器UUID
     */
    private String connectorUuid;

    /**
     * HTTP动作名称（模糊查询）
     */
    private String httpName;

    /**
     * HTTP动作编码（模糊查询）
     */
    private String httpCode;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 启用状态
     */
    private Integer activeStatus;
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交分页查询VO**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/vo/PageConnectorHttpReqVO.java
git commit -m "feat(flow): 添加PageConnectorHttpReqVO分页查询VO"
```

---

## Task 9: 创建FlowConnectorHttpService接口

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorHttpService.java`

**Step 1: 编写FlowConnectorHttpService接口**

```java
package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;

import javax.validation.Valid;

/**
 * HTTP连接器动作Service接口
 *
 * @author zhoulu
 * @since 2026-01-16
 */
public interface FlowConnectorHttpService {

    /**
     * 创建HTTP动作
     *
     * @param createReqVO 创建请求
     * @return HTTP动作ID
     */
    Long createHttpAction(@Valid CreateHttpActionReqVO createReqVO);

    /**
     * 更新HTTP动作
     *
     * @param updateReqVO 更新请求
     */
    void updateHttpAction(@Valid UpdateHttpActionReqVO updateReqVO);

    /**
     * 删除HTTP动作
     *
     * @param id HTTP动作ID
     */
    void deleteHttpAction(Long id);

    /**
     * 获取HTTP动作详情
     *
     * @param id HTTP动作ID
     * @return HTTP动作VO
     */
    HttpActionVO getHttpAction(Long id);

    /**
     * 分页查询HTTP动作列表
     *
     * @param pageReqVO 分页查询请求
     * @return 分页结果
     */
    PageResult<HttpActionVO> getHttpActionPage(PageConnectorHttpReqVO pageReqVO);
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-build && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交Service接口**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorHttpService.java
git commit -m "feat(flow): 添加FlowConnectorHttpService接口"
```

---

## Task 10: 创建FlowConnectorHttpServiceImpl实现类

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorHttpServiceImpl.java`

**Step 1: 编写FlowConnectorHttpServiceImpl类**

```java
package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorHttpRepository;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

import static com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorHttpTableDef.FLOW_CONNECTOR_HTTP;

/**
 * HTTP连接器动作Service实现
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Service
@Validated
public class FlowConnectorHttpServiceImpl extends ServiceImpl<FlowConnectorHttpRepository, FlowConnectorHttpDO>
        implements FlowConnectorHttpService {

    @Setter
    private FlowConnectorHttpRepository connectorHttpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHttpAction(@Valid CreateHttpActionReqVO createReqVO) {
        // 转换为DO
        FlowConnectorHttpDO connectorHttpDO = BeanUtils.toBean(createReqVO, FlowConnectorHttpDO.class);

        // 生成UUID
        connectorHttpDO.setHttpUuid("http-" + UUID.randomUUID().toString().replace("-", ""));

        // 设置默认值
        if (connectorHttpDO.getActiveStatus() == null) {
            connectorHttpDO.setActiveStatus(1);
        }
        if (connectorHttpDO.getSortOrder() == null) {
            connectorHttpDO.setSortOrder(0);
        }

        // 保存
        connectorHttpRepository.save(connectorHttpDO);

        return connectorHttpDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHttpAction(@Valid UpdateHttpActionReqVO updateReqVO) {
        // 查询是否存在
        FlowConnectorHttpDO existHttpDO = connectorHttpRepository.getById(updateReqVO.getId());
        if (existHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + updateReqVO.getId());
        }

        // 转换为DO并更新
        FlowConnectorHttpDO connectorHttpDO = BeanUtils.toBean(updateReqVO, FlowConnectorHttpDO.class);
        connectorHttpRepository.updateById(connectorHttpDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHttpAction(Long id) {
        // 软删除：设置active_status为0
        FlowConnectorHttpDO connectorHttpDO = connectorHttpRepository.getById(id);
        if (connectorHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + id);
        }

        connectorHttpDO.setActiveStatus(0);
        connectorHttpRepository.updateById(connectorHttpDO);
    }

    @Override
    public HttpActionVO getHttpAction(Long id) {
        FlowConnectorHttpDO connectorHttpDO = connectorHttpRepository.getById(id);
        if (connectorHttpDO == null) {
            throw new IllegalArgumentException("HTTP动作不存在: " + id);
        }

        return BeanUtils.toBean(connectorHttpDO, HttpActionVO.class);
    }

    @Override
    public PageResult<HttpActionVO> getHttpActionPage(PageConnectorHttpReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(FLOW_CONNECTOR_HTTP.ACTIVE_STATUS.eq(1))
                .orderBy(FLOW_CONNECTOR_HTTP.SORT_ORDER.asc(), FLOW_CONNECTOR_HTTP.CREATE_TIME.desc());

        // 添加查询条件
        if (pageReqVO.getConnectorUuid() != null) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.CONNECTOR_UUID.eq(pageReqVO.getConnectorUuid()));
        }
        if (pageReqVO.getHttpName() != null && !pageReqVO.getHttpName().isEmpty()) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.HTTP_NAME.like(pageReqVO.getHttpName()));
        }
        if (pageReqVO.getHttpCode() != null && !pageReqVO.getHttpCode().isEmpty()) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.HTTP_CODE.like(pageReqVO.getHttpCode()));
        }
        if (pageReqVO.getRequestMethod() != null) {
            queryWrapper.and(FLOW_CONNECTOR_HTTP.REQUEST_METHOD.eq(pageReqVO.getRequestMethod()));
        }

        // 分页查询
        Page<FlowConnectorHttpDO> page = connectorHttpRepository.paginate(
                pageReqVO.getPageNo(), pageReqVO.getPageSize(), queryWrapper
        );

        // 转换为VO
        return BeanUtils.toPage(page, HttpActionVO.class);
    }
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-build && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交Service实现**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/service/FlowConnectorHttpServiceImpl.java
git commit -m "feat(flow): 添加FlowConnectorHttpServiceImpl实现"
```

---

## Task 11: 创建FlowConnectorHttpController

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorHttpController.java`

**Step 1: 编写FlowConnectorHttpController类**

```java
package com.cmsr.onebase.module.flow.build.controller;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.core.vo.CreateHttpActionReqVO;
import com.cmsr.onebase.module.flow.core.vo.HttpActionVO;
import com.cmsr.onebase.module.flow.core.vo.PageConnectorHttpReqVO;
import com.cmsr.onebase.module.flow.core.vo.UpdateHttpActionReqVO;
import com.cmsr.onebase.module.flow.build.service.FlowConnectorHttpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.cmsr.onebase.framework.common.pojo.CommonResult.success;

/**
 * HTTP连接器动作管理Controller
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Tag(name = "管理后台 - HTTP连接器动作")
@RestController
@RequestMapping("/flow/connector/http/action")
@Validated
public class FlowConnectorHttpController {

    @Setter
    @Resource
    private FlowConnectorHttpService connectorHttpService;

    @PostMapping("/create")
    @Operation(summary = "创建HTTP动作")
    @PreAuthorize("@ss.hasPermission('flow:connector:http:create')")
    public CommonResult<Long> createHttpAction(@Valid @RequestBody CreateHttpActionReqVO createReqVO) {
        return success(connectorHttpService.createHttpAction(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新HTTP动作")
    @PreAuthorize("@ss.hasPermission('flow:connector:http:update')")
    public CommonResult<Boolean> updateHttpAction(@Valid @RequestBody UpdateHttpActionReqVO updateReqVO) {
        connectorHttpService.updateHttpAction(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除HTTP动作")
    @Parameter(name = "id", description = "HTTP动作ID", required = true)
    @PreAuthorize("@ss.hasPermission('flow:connector:http:delete')")
    public CommonResult<Boolean> deleteHttpAction(@RequestParam("id") Long id) {
        connectorHttpService.deleteHttpAction(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取HTTP动作详情")
    @Parameter(name = "id", description = "HTTP动作ID", required = true)
    @PreAuthorize("@ss.hasPermission('flow:connector:http:query')")
    public CommonResult<HttpActionVO> getHttpAction(@RequestParam("id") Long id) {
        return success(connectorHttpService.getHttpAction(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询HTTP动作列表")
    @PreAuthorize("@ss.hasPermission('flow:connector:http:query')")
    public CommonResult<PageResult<HttpActionVO>> getHttpActionPage(@Valid PageConnectorHttpReqVO pageReqVO) {
        return success(connectorHttpService.getHttpActionPage(pageReqVO));
    }
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-build && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交Controller**

```bash
git add onebase-module-flow/onebase-module-flow-build/src/main/java/com/cmsr/onebase/module/flow/build/controller/FlowConnectorHttpController.java
git commit -m "feat(flow): 添加FlowConnectorHttpController控制器"
```

---

## Task 12: 扩展FlowGraphBuilder支持HttpNodeData

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/graph/FlowGraphBuilder.java:97-138`

**Step 1: 修改traverseNodeAndEnrichData方法**

在`traverseNodeAndEnrichData`方法的ScriptNodeData处理之后添加HttpNodeData处理逻辑：

```java
private void traverseNodeAndEnrichData(Long applicationId, JsonGraphNode node) {
    if (node.getData() instanceof ScriptNodeData scriptNodeData) {
        FlowConnectorScriptDO connectorScriptDO = TenantManager.withoutTenantCondition(() ->
            connectorScriptRepository.findByApplicationAndUuid(applicationId, scriptNodeData.getActionId(), scriptNodeData.getActionUuid()));
        scriptNodeData.setScript(connectorScriptDO.getRawScript());
        scriptNodeData.setInputSchema(connectorScriptDO.getInputSchema());
        scriptNodeData.setOutputSchema(connectorScriptDO.getOutputSchema());
    }
    if (node.getData() instanceof CommonNodeData commonNodeData) {
        // 加载连接器配置
        FlowConnectorDO connectorDO = TenantManager.withoutTenantCondition(() ->
            flowConnectorMapper.selectByApplicationAndCode(applicationId, commonNodeData.getConnectorCode()));
        if (connectorDO != null) {
            Map<String, Object> connectorConfig = new HashMap<>();
            if (StringUtils.isNotBlank(connectorDO.getConfigJson())) {
                connectorConfig = JsonUtils.parseObject(connectorDO.getConfigJson(), Map.class);
            }
            commonNodeData.setConnectorConfig(connectorConfig);
        }

        // 加载节点配置
        FlowNodeConfigDO nodeConfigDO = TenantManager.withoutTenantCondition(() ->
            flowNodeConfigMapper.selectByApplicationAndCode(applicationId, commonNodeData.getNodeCode()));
        if (nodeConfigDO != null) {
            Map<String, Object> componentContext = new HashMap<>();
            if (StringUtils.isNotBlank(nodeConfigDO.getConnConfigJson())) {
                componentContext = JsonUtils.parseObject(nodeConfigDO.getConnConfigJson(), Map.class);
            }
            commonNodeData.setComponentContext(componentContext);

            Map<String, Object> actionConfig = new HashMap<>();
            if (StringUtils.isNotBlank(nodeConfigDO.getActionConfigJson())) {
                actionConfig = JsonUtils.parseObject(nodeConfigDO.getActionConfigJson(), Map.class);
            }
            commonNodeData.setActionConfig(actionConfig);
        }
    }
    // ========== 新增：HttpNodeData处理 ==========
    if (node.getData() instanceof HttpNodeData httpNodeData) {
        // 从数据库加载HTTP动作配置
        FlowConnectorHttpDO httpActionDO = TenantManager.withoutTenantCondition(() ->
            connectorHttpRepository.findByApplicationAndUuid(
                applicationId,
                httpNodeData.getHttpUuid()
            ));

        if (httpActionDO != null) {
            // 加载连接器配置
            FlowConnectorDO connectorDO = TenantManager.withoutTenantCondition(() ->
                flowConnectorMapper.selectOneByCondition(
                    QueryWrapper.create()
                        .where(FlowConnectorTableDef.FLOW_CONNECTOR.APPLICATION_ID.eq(applicationId))
                        .and(FlowConnectorTableDef.FLOW_CONNECTOR.CONNECTOR_UUID.eq(httpActionDO.getConnectorUuid()))
                ));

            if (connectorDO != null) {
                Map<String, Object> connectorConfig = new HashMap<>();
                if (StringUtils.isNotBlank(connectorDO.getConfigJson())) {
                    connectorConfig = JsonUtils.parseObject(connectorDO.getConfigJson(), Map.class);
                }
                httpNodeData.setConnectorConfig(connectorConfig);
            }

            // 合并HTTP动作配置
            Map<String, Object> httpActionConfig = new HashMap<>();
            httpActionConfig.put("requestMethod", httpActionDO.getRequestMethod());
            httpActionConfig.put("requestPath", httpActionDO.getRequestPath());
            httpActionConfig.put("requestQuery", httpActionDO.getRequestQuery());
            httpActionConfig.put("requestHeaders", httpActionDO.getRequestHeaders());
            httpActionConfig.put("requestBodyType", httpActionDO.getRequestBodyType());
            httpActionConfig.put("requestBodyTemplate", httpActionDO.getRequestBodyTemplate());
            httpActionConfig.put("authType", httpActionDO.getAuthType());
            httpActionConfig.put("authConfig", httpActionDO.getAuthConfig());
            httpActionConfig.put("responseMapping", httpActionDO.getResponseMapping());
            httpActionConfig.put("successCondition", httpActionDO.getSuccessCondition());
            httpActionConfig.put("inputSchema", httpActionDO.getInputSchema());
            httpActionConfig.put("outputSchema", httpActionDO.getOutputSchema());

            // 覆盖超时和重试配置
            if (httpActionDO.getTimeout() != null) {
                httpActionConfig.put("timeout", httpActionDO.getTimeout());
            }
            if (httpActionDO.getRetryCount() != null) {
                httpActionConfig.put("retryCount", httpActionDO.getRetryCount());
            }

            httpNodeData.setActionConfig(httpActionConfig);
        }
    }
    // ========== 新增结束 ==========

    if (CollectionUtils.isNotEmpty(node.getBlocks())) {
        for (JsonGraphNode child : node.getBlocks()) {
            traverseNodeAndEnrichData(applicationId, child);
        }
    }
}
```

**Step 2: 添加依赖注入**

在FlowGraphBuilder类的字段声明区域添加：

```java
@Setter
@Autowired
private FlowConnectorHttpRepository connectorHttpRepository;
```

**Step 3: 添加import语句**

在文件顶部添加：

```java
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowConnectorHttpDO;
import com.cmsr.onebase.module.flow.core.dal.database.FlowConnectorHttpRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.table.FlowConnectorTableDef;
import com.mybatisflex.core.query.QueryWrapper;
```

**Step 4: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-core && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 5: 提交FlowGraphBuilder修改**

```bash
git add onebase-module-flow/onebase-module-flow-core/src/main/java/com/cmsr/onebase/module/flow/core/graph/FlowGraphBuilder.java
git commit -m "feat(flow): 扩展FlowGraphBuilder支持HttpNodeData配置加载"
```

---

## Task 13: 更新HttpNodeData支持connectorUuid和httpUuid

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/HttpNodeData.java`

**Step 1: 在HttpNodeData类中添加新字段**

在HttpNodeData类的字段声明区域（在现有字段之后）添加：

```java
/**
 * 连接器UUID
 * 用于标识所属的连接器，从流程定义中传入
 */
private String connectorUuid;

/**
 * HTTP动作UUID
 * 用于标识具体的HTTP动作配置，从流程定义中传入
 */
private String httpUuid;

/**
 * 连接器配置
 * 运行时从flow_connector表加载的配置
 * 包含baseUrl、全局认证信息等
 */
private transient Map<String, Object> connectorConfig;

/**
 * HTTP动作配置
 * 运行时从flow_connector_http表加载的配置
 * 包含requestMethod、requestPath等
 */
private transient Map<String, Object> actionConfig;
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-context && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交HttpNodeData修改**

```bash
git add onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/HttpNodeData.java
git commit -m "feat(flow): 更新HttpNodeData支持connectorUuid和httpUuid"
```

---

## Task 14: 更新HttpNodeComponent支持动态配置加载

**Files:**
- Modify: `onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/external/HttpNodeComponent.java`

**Step 1: 修改process方法以支持动态配置**

将现有的process方法修改为支持从connectorConfig和actionConfig加载配置：

```java
@Override
public void process() throws Exception {
    // ========== 步骤 1: 初始化上下文 ==========
    ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
    executeContext.addLog("HTTP 请求节点开始执行");

    VariableContext variableContext = this.getContextBean(VariableContext.class);
    HttpNodeData nodeData = (HttpNodeData) executeContext.getNodeData(this.getTag());
    InLoopDepth inLoopDepth = nodeData.getInLoopDepth();

    // ========== 步骤 2: 解析循环变量 ==========
    Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(
            this, inLoopDepth, variableContext.getNodeVariables()
    );

    // ========== 步骤 3: 获取配置（支持动态加载和内联配置） ==========
    Map<String, Object> connectorConfig;
    Map<String, Object> actionConfig;

    // 如果有connectorUuid/httpUuid，使用动态加载的配置
    if (nodeData.getConnectorConfig() != null && nodeData.getActionConfig() != null) {
        connectorConfig = nodeData.getConnectorConfig();
        actionConfig = nodeData.getActionConfig();
    } else {
        // 否则使用内联配置（向后兼容）
        connectorConfig = new HashMap<>();
        actionConfig = new HashMap<>();
        actionConfig.put("requestMethod", nodeData.getMethod());
        actionConfig.put("requestPath", nodeData.getUrl());
        actionConfig.put("requestHeaders", nodeData.getHeaders());
        actionConfig.put("requestBodyTemplate", nodeData.getBodyContent());
        actionConfig.put("timeout", nodeData.getTimeout());
        actionConfig.put("retryCount", nodeData.getRetry());
    }

    // ========== 步骤 4: 构建完整URL ==========
    String baseUrl = (String) connectorConfig.get("baseUrl");
    String requestPath = (String) actionConfig.get("requestPath");
    String resolvedPath = replaceVariables(requestPath, expressionContext);
    String fullUrl = baseUrl != null ? baseUrl + resolvedPath : resolvedPath;

    // ========== 步骤 5: 构建请求对象 ==========
    HttpRequest request = new HttpRequest();
    request.setUrl(fullUrl);
    request.setMethod((String) actionConfig.get("requestMethod"));

    // 处理请求头
    @SuppressWarnings("unchecked")
    java.util.List<HttpNodeData.Header> headersFromConfig = (java.util.List<HttpNodeData.Header>) actionConfig.get("requestHeaders");
    java.util.List<HttpNodeData.Header> headers = headersFromConfig != null ? headersFromConfig : nodeData.getHeaders();
    if (headers != null) {
        for (HttpNodeData.Header header : headers) {
            String resolvedValue = replaceVariables(header.getValue(), expressionContext);
            header.setValue(resolvedValue);
        }
    }
    request.setHeaders(headers);

    // 处理请求体
    String requestBodyTemplate = (String) actionConfig.get("requestBodyTemplate");
    String resolvedBodyContent = replaceVariables(requestBodyTemplate != null ? requestBodyTemplate : nodeData.getBodyContent(), expressionContext);
    request.setBodyContent(resolvedBodyContent);

    // 处理超时和重试
    Integer timeout = (Integer) actionConfig.get("timeout");
    request.setTimeout(timeout != null ? timeout : (nodeData.getTimeout() != null ? nodeData.getTimeout() : 5000));

    Integer retryCount = (Integer) actionConfig.get("retryCount");
    request.setRetry(retryCount != null ? retryCount : (nodeData.getRetry() != null ? nodeData.getRetry() : 0));

    // ========== 步骤 6: 执行HTTP请求 ==========
    HttpServiceResponse serviceResponse = httpExecuteService.execute(request);

    // ========== 步骤 7: 构建输出 ==========
    Map<String, Object> output = new HashMap<>();
    output.put("statusCode", serviceResponse.getStatusCode());
    output.put("headers", serviceResponse.getHeaders());
    output.put("body", serviceResponse.getBody());
    output.put("rawBody", serviceResponse.getRawBody());
    output.put("duration", serviceResponse.getDuration());

    // ========== 步骤 8: 记录日志并保存结果 ==========
    executeContext.addLog(String.format(
            "HTTP 请求执行成功 - 方法: %s, URL: %s, 状态码: %d, 耗时: %dms",
            request.getMethod(), request.getUrl(),
            serviceResponse.getStatusCode(), serviceResponse.getDuration()
    ));

    variableContext.putNodeVariables(this.getTag(), output);
}
```

**Step 2: 编译验证**

Run: `cd onebase-module-flow/onebase-module-flow-component && mvn compile -DskipTests`
Expected: BUILD SUCCESS

**Step 3: 提交HttpNodeComponent修改**

```bash
git add onebase-module-flow/onebase-module-flow-component/src/main/java/com/cmsr/onebase/module/flow/component/external/HttpNodeComponent.java
git commit -m "feat(flow): 更新HttpNodeComponent支持动态配置加载"
```

---

## Task 15: 创建HttpExecuteService单元测试

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-component/src/test/java/com/cmsr/onebase/module/flow/component/external/service/HttpExecuteServiceTest.java`

**Step 1: 编写测试类**

```java
package com.cmsr.onebase.module.flow.component.external.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpExecuteService单元测试
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Tag("unit")
@SpringBootTest
class HttpExecuteServiceTest {

    @Autowired
    private HttpExecuteService httpExecuteService;

    @Test
    void shouldExecuteGETRequestSuccessfully() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/get");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getRawBody()).isNotEmpty();
        assertThat(response.getDuration()).isGreaterThan(0);
    }

    @Test
    void shouldExecutePOSTRequestWithBody() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/post");
        request.setMethod("POST");
        request.setBodyContent("{\"test\":\"data\"}");
        request.setTimeout(5000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldParseJsonResponse() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/json");
        request.setMethod("GET");
        request.setTimeout(5000);
        request.setRetry(0);

        // When
        HttpServiceResponse response = httpExecuteService.execute(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(java.util.Map.class);
    }

    @Test
    void shouldHandleRequestTimeout() {
        // Given
        HttpRequest request = new HttpRequest();
        request.setUrl("https://httpbin.org/delay/10");
        request.setMethod("GET");
        request.setTimeout(1000);
        request.setRetry(0);

        // When & Then
        assertThatThrownBy(() -> httpExecuteService.execute(request))
            .isInstanceOf(HttpTimeoutException.class);
    }
}
```

**Step 2: 运行测试（需要网络连接）**

Run: `cd onebase-module-flow/onebase-module-flow-component && mvn test -Dtest=HttpExecuteServiceTest`
Expected: 测试通过（需要网络连接到httpbin.org）

**Step 3: 提交测试类**

```bash
git add onebase-module-flow/onebase-module-flow-component/src/test/java/com/cmsr/onebase/module/flow/component/external/service/HttpExecuteServiceTest.java
git commit -m "test(flow): 添加HttpExecuteService单元测试"
```

---

## Task 16: 创建HttpNodeComponent集成测试

**Files:**
- Create: `onebase-module-flow/onebase-module-flow-component/src/test/java/com/cmsr/onebase/module/flow/component/external/HttpNodeComponentIntegrationTest.java`

**Step 1: 编写集成测试类**

```java
package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.module.flow.component.external.service.HttpExecuteService;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.HttpNodeData;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpNodeComponent集成测试
 *
 * @author zhoulu
 * @since 2026-01-16
 */
@Tag("integration")
@SpringBootTest
class HttpNodeComponentIntegrationTest {

    @Autowired
    private FlowExecutor flowExecutor;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
    }

    @Test
    void shouldExecuteHttpNodeInFlow() {
        // Given: 创建测试流程
        String flowDefinition = """
            <chain name="testHttpChain">
                THEN(
                    api_http
                )
            </chain>
            """;

        // When: 执行流程
        LiteflowResponse response = flowExecutor.execute2Resp("testHttpChain", null);

        // Then: 验证结果
        assertThat(response.isSuccess()).isTrue();
    }
}
```

**Step 2: 提交集成测试**

```bash
git add onebase-module-flow/onebase-module-flow-component/src/test/java/com/cmsr/onebase/module/flow/component/external/HttpNodeComponentIntegrationTest.java
git commit -m "test(flow): 添加HttpNodeComponent集成测试"
```

---

## 实施说明

### 验收标准

1. **数据模型完成**: flow_connector_http表创建成功，DO/Mapper/Repository正常工作
2. **API接口完成**: 能够通过Swagger测试所有CRUD接口
3. **流程执行完成**: 在流程中能够成功调用HTTP API并获取响应
4. **测试覆盖**: 单元测试覆盖率达到80%以上

### 依赖关系

任务间存在依赖关系，建议按顺序执行：
- Task 1-5: 数据层（数据库表 → DO → Mapper → Repository）
- Task 6-8: 辅助类和VO（辅助数据类 → 请求/响应VO）
- Task 9-11: 业务层和控制器（Service接口 → Service实现 → Controller）
- Task 12-14: 核心集成（FlowGraphBuilder → HttpNodeData → HttpNodeComponent）
- Task 15-16: 测试（单元测试 → 集成测试）

### 注意事项

1. **向后兼容**: HttpNodeComponent同时支持动态配置（从数据库加载）和内联配置（流程定义中直接写）两种方式
2. **安全性**: SSRF防护、认证信息加密等安全特性在后续高级特性阶段实现
3. **测试**: 单元测试使用httpbin.org作为测试API端点
4. **事务管理**: Service层方法添加@Transactional注解确保数据一致性
