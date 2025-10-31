# 权限校验架构设计文档

## 一、架构概述

本权限校验架构采用**策略模式**和**责任链模式**，提供了灵活、可扩展的权限校验机制。架构参考了现有的数据校验器（ValidationManager/ValidationService）设计模式，确保代码风格的一致性。

### 设计原则

1. **单一职责原则**：每个权限校验器只负责一种权限类型的校验
2. **开闭原则**：对扩展开放，对修改关闭，可以方便地添加新的权限校验器
3. **依赖倒置原则**：依赖抽象接口而非具体实现
4. **责任链模式**：按照优先级顺序执行多个权限校验器
5. **策略模式**：每个权限校验器是一个独立的策略

## 二、架构层次

```
                    ┌────────────────────────────────┐
                    │  AbstractMetadataDataMethod    │
                    │      CoreService               │
                    │  (业务服务层)                    │
                    └────────────┬───────────────────┘
                                 │ 调用
                                 ↓
                    ┌────────────────────────────────┐
                    │   PermissionManager             │
                    │   (权限管理器)                   │
                    │   - 统一入口                     │
                    │   - 责任链编排                   │
                    └────────────┬───────────────────┘
                                 │ 按优先级调用
                                 ↓
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
        ↓                        ↓                        ↓
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│  Operation    │      │     Data      │      │    Field      │
│  Permission   │ (10) │  Permission   │ (20) │  Permission   │ (30)
│   Checker     │ ───→ │   Checker     │ ───→ │   Checker     │
└───────────────┘      └───────────────┘      └───────────────┘
   操作权限校验           数据权限校验           字段权限校验
```

## 三、核心组件

### 3.1 PermissionChecker 接口

权限校验器的统一接口，定义了所有权限校验器必须实现的方法。

```java
public interface PermissionChecker {
    String getPermissionType();           // 权限类型
    boolean supports(ProcessContext ctx); // 是否支持当前操作
    void check(ProcessContext ctx);       // 执行权限校验
    int getOrder();                       // 执行顺序
}
```

### 3.2 PermissionManager 管理器

权限校验的统一入口，负责：
- 管理所有权限校验器
- 按优先级排序和执行
- 统一异常处理和日志记录

**执行顺序**：
1. **操作权限**（Order=10）：最先校验，确保用户有基本操作权限
2. **数据权限**（Order=20）：其次校验，确保用户有数据范围权限
3. **字段权限**（Order=30）：最后校验，确保用户有字段级别权限

### 3.3 三大权限校验器

#### 3.3.1 OperationPermissionChecker（操作权限）

**职责**：校验用户是否具有功能操作权限

**校验内容**：
- 页面访问权限（pageAllowed）
- 新增权限（canCreate）
- 编辑权限（canEdit）
- 删除权限（canDelete）
- 导入权限（canImport）
- 导出权限（canExport）
- 分享权限（canShare）

**适用操作**：所有操作

#### 3.3.2 DataPermissionChecker（数据权限）

**职责**：校验用户对特定数据行的访问权限

**校验内容**：
- 数据范围权限（全部数据、本人提交、本部门提交等）
- 数据级别权限（本人、本人及下属、部门等）
- 自定义过滤条件
- 数据行级操作权限（编辑、删除）

**适用操作**：UPDATE、DELETE、GET

**注意**：
- 对于QUERY、LIST、PAGE等查询操作，数据权限过滤应在SQL层面通过WHERE条件实现
- 此校验器主要用于单条数据的操作权限校验

#### 3.3.3 FieldPermissionChecker（字段权限）

**职责**：校验用户对特定字段的访问权限

**校验内容**：
- 字段可读权限（canRead）
- 字段可写权限（canEdit）
- 字段下载权限（canDownload）

**适用操作**：
- CREATE/UPDATE：检查字段编辑权限
- QUERY/GET/LIST/PAGE：检查字段读取权限并过滤

### 3.4 PermissionContextBuilder（权限构建器）

**职责**：从数据库或缓存中加载用户的权限信息

**构建流程**：
1. 根据用户ID、应用ID、菜单ID查询用户的角色
2. 根据角色ID查询对应的权限配置
3. 合并多个角色的权限（取并集）
4. 构建权限上下文对象

**待实现功能**：
- 集成 onebase-module-app 的权限查询服务
- 实现权限缓存机制
- 支持权限继承和覆盖规则

### 3.5 PermissionDeniedException（权限异常）

权限校验失败时抛出的统一异常，包含：
- 权限类型（permissionType）
- 被拒绝的具体权限标识（deniedPermission）
- 详细错误消息

## 四、权限模型

### 4.1 MetadataPermissionContext

权限上下文，包含三种权限：

```java
public class MetadataPermissionContext {
    private OperationPermission operationPermission; // 操作权限
    private DataPermission dataPermission;           // 数据权限
    private FieldPermission fieldPermission;         // 字段权限
}
```

### 4.2 OperationPermission（操作权限）

```java
public class OperationPermission {
    private boolean pageAllowed;      // 页面可访问
    private boolean allViewsAllowed;  // 所有视图可访问
    private boolean allFieldsAllowed; // 所有字段可操作
    private boolean canCreate;        // 可新增
    private boolean canEdit;          // 可编辑
    private boolean canDelete;        // 可删除
    private boolean canImport;        // 可导入
    private boolean canExport;        // 可导出
    private boolean canShare;         // 可分享
}
```

### 4.3 DataPermission（数据权限）

```java
public class DataPermission {
    private boolean allAllowed;                    // 全部允许
    private boolean allDenied;                     // 全部拒绝
    private List<DataPermissionGroup> groups;      // 权限组列表
}

public class DataPermissionGroup {
    private List<DataPermissionTag> scopTags;      // 权限范围标签
    private Long scopeFieldId;                     // 范围字段ID
    private DataPermissionLevel scopeLevel;        // 权限级别
    private String scopeValue;                     // 权限范围值
    private List<List<DataPermissionFilter>> filters; // 过滤条件
    private boolean canEdit;                       // 可编辑
    private boolean canDelete;                     // 可删除
}
```

**数据权限标签**（DataPermissionTag）：
- ALL_DATA：全部数据
- OWN_SUBMIT：本人提交
- DEPARTMENT_SUBMIT：本部门提交
- SUB_DEPARTMENT_SUBMIT：下级部门提交
- CUSTOM_CONDITION：自定义条件

**数据权限级别**（DataPermissionLevel）：
- SELF：本人
- SELF_AND_SUBORDINATES：本人及下属员工
- MAIN_DEPARTMENT：当前员工所在主部门
- MAIN_DEPARTMENT_AND_SUBS：当前员工所在主部门及下级部门
- SPECIFIED_DEPARTMENT：指定部门
- SPECIFIED_PERSON：指定人员

### 4.4 FieldPermission（字段权限）

```java
public class FieldPermission {
    private boolean allAllowed;                // 全部允许
    private boolean allDenied;                 // 全部拒绝
    private List<FieldPermissionItem> fields;  // 字段权限列表
}

public class FieldPermissionItem {
    private Long fieldId;         // 字段ID
    private boolean canRead;      // 可读
    private boolean canEdit;      // 可编辑
    private boolean canDownload;  // 可下载（文件字段）
}
```

## 五、使用示例

### 5.1 在业务服务中使用

```java
@Service
public class AbstractMetadataDataMethodCoreService {
    
    @Resource
    private PermissionManager permissionManager;
    
    protected void validatePermission(ProcessContext context) {
        log.info("开始执行权限校验：entityId={}, operationType={}", 
                context.getEntityId(), 
                context.getOperationType());

        // 使用权限管理器执行完整的权限校验流程
        permissionManager.checkPermission(context);

        log.info("权限校验完成：entityId={}", context.getEntityId());
    }
}
```

### 5.2 构建权限上下文

```java
@Resource
private PermissionContextBuilder permissionContextBuilder;

// 构建权限上下文
MetadataPermissionContext permissionContext = permissionContextBuilder
    .buildPermissionContext(loginUserCtx, menuId, entityId);

// 设置到请求上下文
requestContext.setPermissionContext(permissionContext);
```

### 5.3 快速检查单个操作权限

```java
@Resource
private PermissionManager permissionManager;

// 检查是否有新增权限
boolean canCreate = permissionManager.hasOperationPermission(
    permissionContext, "CREATE");

if (!canCreate) {
    throw new PermissionDeniedException("操作权限", "CREATE", "无新增权限");
}
```

## 六、扩展指南

### 6.1 添加新的权限校验器

1. 实现 `PermissionChecker` 接口
2. 添加 `@Component` 注解
3. 实现必要的方法
4. 设置合适的执行顺序（Order）

```java
@Slf4j
@Component
public class CustomPermissionChecker implements PermissionChecker {
    
    @Override
    public String getPermissionType() {
        return "自定义权限";
    }
    
    @Override
    public boolean supports(ProcessContext context) {
        // 判断是否需要执行此校验
        return true;
    }
    
    @Override
    public void check(ProcessContext context) {
        // 执行权限校验逻辑
        // 如果校验失败，抛出 PermissionDeniedException
    }
    
    @Override
    public int getOrder() {
        return 40; // 在字段权限之后执行
    }
}
```

### 6.2 完善数据权限校验

数据权限校验器当前为简化实现，完整实现需要：

1. **查询数据的创建人和所属部门**：
   ```java
   // 根据数据ID查询数据的创建人、创建部门等信息
   DataOwnerInfo ownerInfo = getDataOwnerInfo(entityId, dataId);
   ```

2. **根据权限标签判断可见性**：
   ```java
   for (DataPermissionTag tag : group.getScopTags()) {
       switch (tag) {
           case OWN_SUBMIT:
               if (ownerInfo.getCreatorId().equals(loginUserId)) {
                   return true;
               }
               break;
           case DEPARTMENT_SUBMIT:
               if (ownerInfo.getDepartmentId().equals(userDepartmentId)) {
                   return true;
               }
               break;
           // ... 其他标签的判断
       }
   }
   ```

3. **根据权限级别和范围值判断**：
   ```java
   switch (group.getScopeLevel()) {
       case SELF:
           return ownerInfo.getCreatorId().equals(loginUserId);
       case SPECIFIED_PERSON:
           List<Long> allowedUserIds = parseUserIds(group.getScopeValue());
           return allowedUserIds.contains(ownerInfo.getCreatorId());
       // ... 其他级别的判断
   }
   ```

4. **根据自定义过滤条件判断**：
   ```java
   if (CollectionUtils.isNotEmpty(group.getFilters())) {
       return evaluateFilters(group.getFilters(), dataValues);
   }
   ```

### 6.3 集成权限查询服务

在 `PermissionContextBuilder` 中集成 `onebase-module-app` 的权限查询服务：

```java
@Resource
private AppAuthPermissionService appAuthPermissionService;

private OperationPermission loadOperationPermission(Long userId, 
                                                     Long applicationId, 
                                                     Long menuId) {
    AuthPermissionReq req = new AuthPermissionReq();
    req.setUserId(userId);
    req.setApplicationId(applicationId);
    req.setMenuId(menuId);
    
    AuthDetailFunctionPermissionVO vo = 
        appAuthPermissionService.getFunctionPermission(req);
    
    return convertToOperationPermission(vo);
}
```

## 七、注意事项

### 7.1 性能优化

1. **权限缓存**：
   - 用户的权限信息应该缓存，避免每次请求都查询数据库
   - 建议使用 Redis 缓存，过期时间可设置为 30 分钟
   - 权限变更时需要及时清除缓存

2. **查询操作的数据权限**：
   - 对于 QUERY、LIST、PAGE 等批量查询操作，不应该逐条检查数据权限
   - 应该在 SQL 层面通过 WHERE 条件实现数据权限过滤
   - 可以使用 MyBatis 拦截器或者 Anyline 的查询条件构建机制

3. **字段权限过滤**：
   - 字段权限的读取过滤应该在查询结果处理阶段进行
   - 可以在 ResultHandler 中统一处理字段过滤
   - 避免在每个业务方法中重复编写字段过滤逻辑

### 7.2 异常处理

1. **统一异常处理**：
   - 所有权限校验失败应抛出 `PermissionDeniedException`
   - 在全局异常处理器中捕获并返回友好的错误提示
   - 记录详细的权限校验失败日志，便于问题排查

2. **权限校验失败的返回信息**：
   - 不要泄露敏感信息（如数据库结构、内部逻辑等）
   - 提供明确的错误提示，帮助用户理解为什么没有权限
   - 可以提供申请权限的入口或联系方式

### 7.3 测试建议

1. **单元测试**：
   - 为每个权限校验器编写单元测试
   - 测试各种边界情况和异常场景
   - 使用 Mock 对象隔离外部依赖

2. **集成测试**：
   - 测试权限校验的完整流程
   - 测试多角色权限合并的正确性
   - 测试权限缓存的有效性

3. **压力测试**：
   - 测试高并发场景下的权限校验性能
   - 验证缓存机制的有效性
   - 确保不会成为系统性能瓶颈

## 八、TODO 清单

### 短期（1-2周）

- [ ] 完善 `PermissionContextBuilder`，集成 app 模块的权限查询服务
- [ ] 实现权限缓存机制（基于 Redis）
- [ ] 完善 `DataPermissionChecker` 的数据行级权限校验逻辑
- [ ] 为查询操作实现 SQL 层面的数据权限过滤
- [ ] 编写单元测试和集成测试

### 中期（2-4周）

- [ ] 实现权限继承和覆盖规则
- [ ] 实现多角色权限合并策略（并集、交集、优先级等）
- [ ] 优化字段权限过滤机制，统一在 ResultHandler 中处理
- [ ] 实现权限审计日志，记录所有权限校验行为
- [ ] 编写权限管理的使用文档和最佳实践指南

### 长期（1-2月）

- [ ] 支持动态权限规则（可通过配置动态调整权限逻辑）
- [ ] 实现权限分析工具，帮助管理员了解权限配置
- [ ] 支持权限模拟功能（管理员可以模拟其他用户查看权限效果）
- [ ] 实现基于时间的权限控制（临时授权）
- [ ] 性能优化和压力测试

## 九、参考资料

- 现有的 ValidationManager 和 ValidationService 设计模式
- onebase-module-app 的权限模型和 API
- Spring Security 的权限校验机制
- 设计模式：策略模式、责任链模式

---

**文档版本**：v1.0  
**创建日期**：2025-10-31  
**作者**：matianyu  
**最后更新**：2025-10-31


