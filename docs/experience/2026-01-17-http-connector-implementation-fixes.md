# HTTP 连接器实现与编译错误修复经验

**日期**: 2026-01-17
**任务**: HTTP 连接器 V2 动态配置功能实现及编译错误修复
**状态**: ✅ 已完成

---

## 一、背景

在之前的 HTTP 连接器 V1 基础上，扩展了 V2 动态配置功能：
- **V1**: 内联配置（url、method、headers 直接写在节点数据中）
- **V2**: 动态配置（从数据库 flow_connector_http 表加载配置）
- **兼容性**: 保持向后兼容，V1 内联配置仍可使用

---

## 二、主要工作内容

### 2.1 核心文件修改

1. **FlowGraphBuilder.java** - 动态配置加载
   - 在 `traverseNodeAndEnrichData()` 方法中添加 HttpNodeData 处理分支
   - 根据 `httpUuid` 从数据库加载 HTTP 动作配置
   - 合并连接器级配置（baseUrl、认证信息）和动作级配置

2. **HttpNodeData.java** - 新增字段
   ```java
   private String connectorUuid;      // 连接器 UUID
   private String httpUuid;            // HTTP 动作 UUID
   private transient Map<String, Object> connectorConfig;  // 运行时加载
   private transient Map<String, Object> actionConfig;     // 运行时加载
   ```

3. **HttpNodeComponent.java** - 智能配置选择
   ```java
   if (nodeData.getConnectorConfig() != null && nodeData.getActionConfig() != null) {
       // 使用动态加载的配置（V2）
       connectorConfig = nodeData.getConnectorConfig();
       actionConfig = nodeData.getActionConfig();
   } else {
       // 使用内联配置（V1 向后兼容）
       actionConfig.put("requestMethod", nodeData.getMethod());
       // ...
   }
   ```

### 2.2 新增文件（V2 数据层）

1. **数据库迁移**: `V2026.01.16.01__create_flow_connector_http_table.sql`
   - PostgreSQL 版本（31 字段 + 5 索引）
   - MySQL 版本（同样结构，不同语法）

2. **DO/VO/Mapper/Repository**: 完整的 MyBatis-Flex 数据访问层

3. **Service/Controller**: REST API CRUD 操作

---

## 三、遇到的编译错误及解决方案

### 错误 1: QueryWrapper API 使用错误

**错误信息**:
```
不兼容的类型: QueryWrapper无法转换为QueryCondition
```

**问题代码** (FlowGraphBuilder.java:154-157):
```java
FlowConnectorDO connectorDO = TenantManager.withoutTenantCondition(() ->
    flowConnectorMapper.selectOneByCondition(
        QueryWrapper.create()
            .where(FLOW_CONNECTOR.APPLICATION_ID.eq(applicationId))
            .and(FLOW_CONNECTOR.CONNECTOR_UUID.eq(...))  // ❌ 错误
    ));
```

**原因**: `.where()` 返回的不是 QueryWrapper，链式调用需要在 `.where()` 内部完成

**解决方案**:
```java
QueryWrapper connectorQuery = QueryWrapper.create()
        .where(FLOW_CONNECTOR.APPLICATION_ID.eq(applicationId))
        .and(FLOW_CONNECTOR.CONNECTOR_UUID.eq(...));
FlowConnectorDO connectorDO = TenantManager.withoutTenantCondition(() ->
        flowConnectorMapper.selectOneByQuery(connectorQuery));  // ✅ 使用 selectOneByQuery
```

---

### 错误 2: IDEA 空指针警告

**警告信息**:
```
Method invocation 'get' may produce 'NullPointerException'
```

**问题代码**:
```java
String baseUrl = (String) connectorConfig.get("baseUrl");  // ❌ connectorConfig 可能为 null
```

**解决方案**: 添加空值安全检查
```java
String baseUrl = connectorConfig != null ? (String) connectorConfig.get("baseUrl") : null;
```

**影响范围**: HttpNodeComponent.java 中所有访问 `connectorConfig` 和 `actionConfig` 的地方

---

### 错误 3: javax.validation 包不存在

**错误信息**:
```
java: 程序包javax.validation不存在
```

**原因**: Spring Boot 3.x 已从 Java EE 迁移到 Jakarta EE，包名从 `javax.*` 改为 `jakarta.*`

**修复范围**:
- `FlowConnectorHttpService.java`
- `FlowConnectorHttpServiceImpl.java`
- `FlowConnectorHttpController.java`

**解决方案**:
```java
// 修改前
import javax.validation.Valid;
import javax.annotation.Resource;

// 修改后
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
```

---

### 错误 4: Repository 继承关系错误

**错误信息**:
```
返回类型 FlowConnectorHttpRepository 与 BaseMapper<FlowConnectorHttpDO> 不兼容
```

**问题代码**:
```java
public class FlowConnectorHttpRepository extends BaseAppRepository<FlowConnectorHttpMapper, FlowConnectorHttpDO> {
    // ❌ BaseAppRepository 不适用于 MyBatis-Flex
}
```

**解决方案**: 改为继承 MyBatis-Flex 的 `ServiceImpl`
```java
public class FlowConnectorHttpRepository extends ServiceImpl<FlowConnectorHttpMapper, FlowConnectorHttpDO> {
    public FlowConnectorHttpDO findByApplicationAndUuid(Long applicationId, String httpUuid) {
        QueryWrapper query = this.query()
                .where(FLOW_CONNECTOR_HTTP.APPLICATION_ID.eq(applicationId))
                .and(FLOW_CONNECTOR_HTTP.HTTP_UUID.eq(httpUuid));
        return this.getOne(query);  // ✅ ServiceImpl 提供的方法
    }
}
```

**方法调用调整**:
- `getMapper().selectOneByQuery(query)` → `this.getOne(query)`
- `getMapper().selectListByQuery(query)` → `this.list(query)`

---

### 错误 5: 分页 VO 缺少字段

**错误信息**:
```
找不到符号: 方法 getPageNo()
找不到符号: 方法 getPageSize()
```

**问题代码**:
```java
public class PageConnectorHttpReqVO implements Serializable {
    private String connectorUuid;
    // ❌ 缺少 pageNo, pageSize 字段
}
```

**解决方案**: 继承 `PageParam` 获得分页字段
```java
public class PageConnectorHttpReqVO extends PageParam {
    private String connectorUuid;
    // ✅ PageParam 提供 pageNo, pageSize, sortField, sortOrder 等字段
}
```

---

### 错误 6: Service 分页方法调用错误

**错误信息**:
```
找不到符号: 方法 paginate(Integer, Integer, QueryWrapper)
找不到符号: 方法 toPage(Page, Class<HttpActionVO>)
```

**问题代码**:
```java
@Service
public class FlowConnectorHttpServiceImpl extends ServiceImpl<...> {
    public PageResult<HttpActionVO> getHttpActionPage(PageConnectorHttpReqVO pageReqVO) {
        Page<FlowConnectorHttpDO> page = connectorHttpRepository.paginate(
                pageReqVO.getPageNo(), pageReqVO.getPageSize(), queryWrapper);  // ❌ 方法不存在
        return BeanUtils.toPage(page, HttpActionVO.class);  // ❌ 方法不存在
    }
}
```

**解决方案**:
```java
@Service
public class FlowConnectorHttpServiceImpl implements FlowConnectorHttpService {
    // ❌ 不要继承 ServiceImpl，使用组合方式
    @Setter
    private FlowConnectorHttpRepository connectorHttpRepository;

    public PageResult<HttpActionVO> getHttpActionPage(PageConnectorHttpReqVO pageReqVO) {
        // ✅ 使用 Page.of() 创建分页对象
        Page<FlowConnectorHttpDO> page = connectorHttpRepository.page(
                Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);

        // ✅ 手动转换 DO → VO
        List<HttpActionVO> voList = page.getRecords().stream()
                .map(doObj -> BeanUtils.toBean(doObj, HttpActionVO.class))
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotalRow());
    }
}
```

**关键点**:
1. Service 层不继承 `ServiceImpl`，使用依赖注入
2. `Page.of(pageNo, pageSize)` 创建 MyBatis-Flex 的 Page 对象
3. `PageUtils.toPageResult()` 不会自动转换类型，需要手动 DO → VO

---

## 四、技术要点总结

### 4.1 MyBatis-Flex 正确用法

**QueryWrapper 链式调用**:
```java
// ✅ 正确：条件在 where() 内部链式调用
QueryWrapper query = QueryWrapper.create()
        .where(FLOW_CONNECTOR.APPLICATION_ID.eq(id)
                .and(FLOW_CONNECTOR.HTTP_UUID.eq(uuid)));

// ✅ 正确：分步调用
QueryWrapper query = QueryWrapper.create()
        .where(FLOW_CONNECTOR.APPLICATION_ID.eq(id))
        .and(FLOW_CONNECTOR.HTTP_UUID.eq(uuid));
```

**Repository 继承**:
```java
public class XxxRepository extends ServiceImpl<XxxMapper, XxxDO> {
    // 使用 this.query(), this.getOne(), this.list() 等方法
}
```

**分页查询**:
```java
Page<DO> page = repository.page(Page.of(pageNo, pageSize), queryWrapper);
List<VO> voList = page.getRecords().stream()
        .map(doObj -> BeanUtils.toBean(doObj, VO.class))
        .collect(Collectors.toList());
return new PageResult<>(voList, page.getTotalRow());
```

### 4.2 Spring Boot 3.x 迁移要点

| 旧包名 (Java EE) | 新包名 (Jakarta EE) |
|-----------------|-------------------|
| javax.validation | jakarta.validation |
| javax.annotation | jakarta.annotation |
| javax.persistence | jakarta.persistence |
| javax.servlet | jakarta.servlet |

### 4.3 向后兼容设计模式

```java
// 优先使用新配置，降级到旧配置
if (newConfig != null) {
    useNewConfig(newConfig);
} else {
    useLegacyConfig(legacyConfig);
}
```

---

## 五、Git 提交记录

| Commit | 描述 |
|--------|------|
| ebe7d2bf6 | fix(flow): 修正 QueryWrapper 使用方式，使用 selectOneByQuery 替代 selectOneByCondition |
| 7763d5715 | fix(flow): 添加空指针安全检查，防止从 actionConfig/connectorConfig 获取值时出现 NPE |
| 69fa14260 | fix(flow): 修复 HTTP 连接器代码的编译错误（javax → jakarta, Repository 继承关系, 分页方法调用） |

---

## 六、后续优化建议

1. **统一分页结果转换**: 考虑创建 `PageUtils.toPageResult(page, VO.class)` 工具方法
2. **配置验证**: 在 HttpNodeComponent 中添加必需配置项的校验
3. **异常处理**: 增加数据库连接失败、HTTP 动作不存在等异常的处理
4. **单元测试**: 为新增的动态配置功能编写测试用例

---

## 七、相关文档

- 实现计划: `docs/plan/httpconnector/2026-01-16-http-connector.md`
- 数据模型: `onebase-module-flow/onebase-module-flow-context/src/main/java/com/cmsr/onebase/module/flow/context/graph/nodes/HttpNodeData.java`
- API 文档: `FlowConnectorHttpController.java` (Swagger 注解)
