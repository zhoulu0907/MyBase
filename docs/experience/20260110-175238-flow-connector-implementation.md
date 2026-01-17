# Flow 模块通用连接器实现经验

**时间**: 2026-01-10 17:52:38
**任务**: 接手 flow 模块，实现通用连接器架构

---

## 1. 任务背景

接手 OneBase v3 项目的 flow 模块，目标是实现通用连接器架构，模仿 ScriptNodeComponent 新增连接器。

参考文档：`/docs/universal-connector-development-plan.md`

---

## 2. 已完成工作

### 2.1 接口简化（关键决策）

**原设计问题**：
- `ConnectorExecutor` 接口使用 3 参数：`execute(connectorConfig, actionConfig, componentContext)`
- `CommonConnectorComponent` 调用时只传 2 参数，导致编译错误

**简化方案**：
```java
// 简化后的接口
Map<String, Object> execute(String actionType, Map<String, Object> config) throws Exception;
```

- `actionType`: 动作类型（EMAIL_SEND、SMS_SEND、DATABASE_QUERY）
- `config`: 合并后的配置（connectorConfig + actionConfig + inputData）

**原因**：参考 ScriptNodeComponent 的简洁风格，用户明确要求代码简洁

---

### 2.2 文件结构重组

**删除文件**：
- `onebase-module-flow-core/.../connector/ConnectorFactory.java`
- `onebase-module-flow-core/.../connector/ConnectorExecutor.java`

**新建结构**：
```
onebase-module-flow-component/src/main/java/.../connector/
├── ConnectorExecutor.java           # 接口定义
└── impl/
    ├── Email163Connector.java      # 163邮箱连接器
    ├── SmsAliConnector.java        # 阿里云短信连接器
    └── DatabaseMysqlConnector.java # MySQL数据库连接器
```

**符合 Java 规范**：
- 接口和实现在同一模块
- 实现类放在 impl 子包

---

### 2.3 依赖添加

**pom.xml** 添加：
```xml
<!-- Jakarta Mail for Email Connector -->
<dependency>
    <groupId>org.eclipse.angus</groupId>
    <artifactId>angus-mail</artifactId>
</dependency>

<!-- Aliyun SMS for SMS Connector -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>dysmsapi20170525</artifactId>
    <version>2.0.24</version>
</dependency>
```

---

## 3. 关键技术点

### 3.1 阿里云短信 API

**包名修正**：
```java
import com.aliyun.teautil.models.RuntimeOptions;  // 正确
// import com.aliyun.teaopenapi.models.RuntimeOptions;  // 错误
```

**API 参数类型**：
```java
// 手机号码：需要逗号分隔的字符串，不是 List
String phoneNumbers = String.join(",", phoneList);
request.setPhoneNumbers(phoneNumbers);

// 模板参数：需要 JSON 字符串，不是 Map
String templateParam = JsonUtils.toJsonString(templateParamMap);
request.setTemplateParam(templateParam);
```

**异常处理**：
```java
private Client createSmsClient(Map<String, Object> config) throws Exception {
    // new Client(config) 会抛出 Exception，必须声明
}
```

---

### 3.2 Jakarta Mail (Email)

**包名**：
```java
import jakarta.mail.*;  // Spring Boot 3.x 使用 jakarta，不是 javax
```

**AddressException 处理**：
```java
// ❌ Lambda 表达式会抛出 AddressException
InternetAddress[] toAddresses = toList.stream()
    .map(InternetAddress::new)
    .toArray(InternetAddress[]::new);

// ✅ 改用传统 for 循环
InternetAddress[] toAddresses = new InternetAddress[toList.size()];
for (int i = 0; i < toList.size(); i++) {
    toAddresses[i] = new InternetAddress(toList.get(i));
}
```

---

### 3.3 CommonConnectorComponent 实现

**简洁风格**：
```java
@Component
public class CommonConnectorComponent extends SkippableNodeComponent {

    @Autowired
    private Email163Connector email163Connector;

    @Autowired
    private SmsAliConnector smsAliConnector;

    @Autowired
    private DatabaseMysqlConnector databaseMysqlConnector;

    // 使用 switch-case 路由，代码清晰
    private Map<String, Object> executeConnector(String connectorCode, String actionType, Map<String, Object> config) {
        switch (connectorCode) {
            case "EMAIL_163":
                return email163Connector.execute(actionType, config);
            case "SMS_ALI":
                return smsAliConnector.execute(actionType, config);
            case "DATABASE_MYSQL":
                return databaseMysqlConnector.execute(actionType, config);
            default:
                throw new RuntimeException("不支持的连接器类型: " + connectorCode);
        }
    }
}
```

**配置合并**：
```java
Map<String, Object> mergedConfig = new HashMap<>();
mergedConfig.putAll(nodeData.getConnectorConfig());
mergedConfig.putAll(nodeData.getActionConfig());
mergedConfig.put("inputData", inputData);
```

---

## 4. 数据库架构

### 4.1 数据表

**flow_connector** - 连接器定义：
```java
private String connectorUuid;
private String connectorName;
private String code;
private String typeCode;
private String description;
private String configJson;  // 连接器配置
```

**flow_node_config** - 节点配置：
```java
private String nodeCode;
private String connConfigJson;    // 组件上下文
private String actionConfigJson;  // 动作配置
```

### 4.2 数据加载

**FlowGraphBuilder** 已实现双Map数据增强：
```java
private void traverseNodeAndEnrichData(Long applicationId, JsonGraphNode node) {
    if (node.getData() instanceof CommonNodeData commonNodeData) {
        // 加载连接器配置
        FlowConnectorDO connectorDO = flowConnectorMapper.selectByApplicationAndCode(...);
        commonNodeData.setConnectorConfig(JsonUtils.parseObject(connectorDO.getConfigJson(), Map.class));

        // 加载节点配置
        FlowNodeConfigDO nodeConfigDO = flowNodeConfigMapper.selectByApplicationAndCode(...);
        commonNodeData.setComponentContext(JsonUtils.parseObject(nodeConfigDO.getConnConfigJson(), Map.class));
        commonNodeData.setActionConfig(JsonUtils.parseObject(nodeConfigDO.getActionConfigJson(), Map.class));
    }
}
```

---

## 5. 编译错误修复记录

| 错误 | 原因 | 解决方案 |
|------|------|---------|
| `RuntimeOptions` 找不到 | 包名错误 | 改为 `com.aliyun.teautil.models` |
| `setPhoneNumbers` 类型不匹配 | 需要String，不是List | `String.join(",", list)` |
| `setTemplateParam` 类型不匹配 | 需要JSON String | `JsonUtils.toJsonString(map)` |
| `AddressException` in lambda | lambda异常处理 | 改用for循环 |
| `Client()` 未报告异常 | 构造函数抛Exception | 添加 `throws Exception` |

---

## 6. 核心设计理念

**用户偏好**：简洁 > 复杂，参考 ScriptNodeComponent 风格

**设计原则**：
- ✅ 直接依赖注入（不使用 Factory）
- ✅ Switch-case 路由（代码清晰）
- ✅ 统一返回类型 `Map<String, Object>`
- ✅ 避免过度抽象
- ❌ 不使用 ConnectorFactory
- ❌ 不使用复杂的动态加载

---

## 7. 已完成工作更新

### 7.1 编译问题全部解决 ✅

**状态**: 所有连接器编译通过（2026-01-10 17:59）

**已修复问题**：
- ✅ Email163Connector lambda 异常处理（改用 for 循环）
- ✅ SmsAliConnector 所有编译错误
- ✅ 所有依赖正确添加
- ✅ 接口签名统一

**编译命令**：
```bash
mvn clean install -Dmaven.test.skip=true
```

**编译结果**: BUILD SUCCESS

---

## 8. 待完成工作

- [ ] 数据库连接问题排查（非代码问题）
- [ ] 运行 SQL 初始化脚本
- [ ] 测试连接器功能
- [ ] 添加单元测试
- [ ] 添加更多连接器类型

---

## 8. 相关文件

| 文件 | 说明 |
|------|------|
| `/docs/universal-connector-development-plan.md` | 通用连接器开发方案 |
| `/FLOW_STARTUP_FIX_GUIDE.md` | Flow启动修复指南 |
| `onebase-module-flow-core/src/main/resources/sql/common-connector-init.sql` | 数据库初始化脚本 |
| `onebase-module-flow-component/src/.../connector/` | 连接器实现目录 |

---

**下次继续点**：
1. 解决数据库连接后，运行初始化脚本
2. 创建测试流程验证连接器功能
3. 添加单元测试覆盖核心逻辑
