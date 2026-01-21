# Huangjie 代码模块学习路径

**制定日期:** 2025-12-27
**制定者:** Kanten (Claude Code)
**学习周期:** 8周（2个月）
**难度等级:** ⭐⭐⭐⭐ (中高级)

---

## 📋 目录

1. [学习路径总览](#学习路径总览)
2. [阶段一:框架层基础(第1-2周)](#阶段一框架层基础第1-2周)
3. [阶段二:ORM框架核心(第3周)](#阶段二orm框架核心第3周)
4. [阶段三:Flow流程引擎(第4-6周)](#阶段三flow流程引擎第4-6周)
5. [阶段四:App应用模块(第7周)](#阶段四app应用模块第7周)
6. [阶段五:实战项目(第8周)](#阶段五实战项目第8周)
7. [学习检查清单](#学习检查清单)

---

## 学习路径总览

### 🗺️ 模块依赖关系图

```
┌─────────────────────────────────────────────────────────┐
│                   框架层 (Framework)                      │
│  onebase-spring-boot-starter-orm  ←── 第一阶段            │
│  onebase-common                                           │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│                   ORM框架 (Foundation)                    │
│  BaseBizRepository                                       │
│  QueryWrapperUtils                          ←── 第二阶段  │
│  租户隔离、版本管理                                         │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│                   Flow流程引擎 (Core)                      │
│  FlowProcessExecutor                                     │
│  ExpressionExecutor                          ←── 第三阶段  │
│  DataAddNodeComponent                                    │
│  ExecuteContext                                          │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│                   App应用模块 (Application)                │
│  AppMenuRepository                                       │
│  AppAuthPermissionService                   ←── 第四阶段  │
│  应用菜单、权限管理                                          │
└─────────────────────────────────────────────────────────┘
```

### 📊 学习时间分配

| 阶段 | 模块 | 周数 | 占比 | 难度 |
|------|------|------|------|------|
| **阶段一** | 框架层基础 | 2周 | 25% | ⭐⭐ |
| **阶段二** | ORM框架 | 1周 | 12.5% | ⭐⭐⭐ |
| **阶段三** | Flow引擎 | 3周 | 37.5% | ⭐⭐⭐⭐⭐ |
| **阶段四** | App模块 | 1周 | 12.5% | ⭐⭐⭐⭐ |
| **阶段五** | 实战项目 | 1周 | 12.5% | ⭐⭐⭐⭐ |

---

## 阶段一:框架层基础(第1-2周)

### 🎯 学习目标

理解OneBase框架的基础架构，掌握Spring Boot Starter、工具类、通用组件的设计思想。

### 📚 第1周: Spring Boot Starter与通用工具

#### Day 1-2: ORM框架基础

**学习文件:**
- `onebase-spring-boot-starter-orm`
  - `QueryWrapperUtils.java` (65次修改) ⭐⭐⭐⭐⭐
  - `BaseBizRepository.java` (59次修改) ⭐⭐⭐⭐⭐

**学习重点:**
1. MyBatis-Flex的基本使用
2. 租户隔离的实现机制
3. 版本管理的自动注入
4. 查询包装器的链式API

**代码示例:**
```java
// 学习点1: 租户隔离的自动注入
protected void injectQueryFilter(QueryWrapper queryWrapper) {
    if (ApplicationManager.isIgnoreApplicationCondition()) {
        return;
    }
    Long applicationId = ApplicationManager.getApplicationId();
    Long versionTag = ApplicationManager.getVersionTag();

    QueryColumn applicationIdColumn = QueryWrapperUtils.createApplicationIdColumn(this, queryWrapper);
    QueryColumn versionTagColumn = QueryWrapperUtils.createVersionTagColumn(this, queryWrapper);

    queryWrapper.and(applicationIdColumn.eq(applicationId));
    queryWrapper.and(versionTagColumn.eq(versionTag));
}

// 学习点2: 链式查询API
List<AppMenuDO> menus = QueryWrapperUtils.create()
    .where(AppMenuDO::getApplicationId).eq(appId)
    .and(AppMenuDO::getVersionTag).eq(versionTag)
    .and(AppMenuDO::getParentId).isNull()
    .orderBy(AppMenuDO::getSortNumber, true)
    .list();
```

**实践任务:**
- [ ] 编写一个简单的Repository,继承BaseBizRepository
- [ ] 实现租户隔离的查询
- [ ] 实现版本管理的数据操作

#### Day 3-4: 公共工具类

**学习文件:**
- `onebase-common`
  - 查看工具类的设计模式
  - 学习日期、字符串、集合工具

**学习重点:**
1. 工具类的静态方法设计
2. 空值安全处理
3. 集合操作的封装

**实践任务:**
- [ ] 使用Hutool工具类处理日期
- [ ] 使用Apache Commons处理集合
- [ ] 使用Guava处理字符串

#### Day 5-7: 安全与多租户

**学习文件:**
- `onebase-security-build`
- `onebase-security-runtime`
- `onebase-spring-boot-starter-biz-tenant`

**学习重点:**
1. 多租户上下文管理
2. 应用版本管理
3. 安全认证与授权

**代码示例:**
```java
// 多租户上下文
public class ApplicationManager {
    private static final ThreadLocal<Long> APPLICATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> VERSION_TAG = new ThreadLocal<>();

    public static Long getApplicationId() {
        return APPLICATION_ID.get();
    }

    public static void setApplicationId(Long applicationId) {
        APPLICATION_ID.set(applicationId);
    }
}
```

**实践任务:**
- [ ] 实现一个简单的多租户过滤器
- [ ] 实现应用版本切换
- [ ] 测试租户数据隔离

### 📚 第2周: 设计模式与编码规范

#### Day 1-3: 设计模式学习

**重点模式:**
1. **模板方法模式** - BaseBizRepository
2. **策略模式** - ExpressionExecutor
3. **工厂方法模式** - QueryWrapperUtils
4. **构建器模式** - ExecuteContext

**学习方式:**
- 阅读代码,识别模式
- 画出类图
- 编写类似代码

**实践任务:**
- [ ] 使用模板方法模式重构一个类
- [ ] 使用策略模式实现一个功能
- [ ] 使用工厂模式创建对象

#### Day 4-7: 代码规范与最佳实践

**学习内容:**
1. 阅读Huangjie代码风格分析报告
2. 学习命名规范
3. 学习异常处理
4. 学习日志记录

**实践任务:**
- [ ] 按照规范编写一个Service类
- [ ] 实现完善的异常处理
- [ ] 添加合适的日志记录

---

## 阶段二:ORM框架核心(第3周)

### 🎯 学习目标

深入理解OneBase ORM框架的核心实现,掌握租户隔离、版本管理、数据操作的底层机制。

### 📚 第3周: ORM框架深入

#### Day 1-3: BaseBizRepository源码精读

**学习文件:**
- `BaseBizRepository.java` (352行, 59次修改)

**学习重点:**
1. **模板方法模式的实现**
   ```java
   @Override
   public T getOne(QueryWrapper query) {
       injectQueryFilter(query);  // 模板方法
       return getMapper().selectOneByQuery(query);
   }
   ```

2. **AOP思想的应用**
   - 在父类统一处理横切关注点
   - 子类无需关心租户隔离
   - 自动注入版本条件

3. **版本管理的实现**
   ```java
   // 编辑态 -> 运行态
   public void copyEditToRuntime(Long applicationId) {
       QueryWrapper queryWrapper = QueryWrapper.create()
           .where(applicationIdColumn.eq(applicationId))
           .where(versionTagColumn.eq(VersionTagEnum.BUILD.getValue()));
       List<T> entities = this.getMapper().selectListByQuery(queryWrapper);
       entities.forEach(entity -> {
           entity.setId(null);
           entity.setVersionTag(VersionTagEnum.RUNTIME.getValue());
       });
       super.saveBatch(entities);
   }
   ```

4. **链式UpdateChain**
   ```java
   super.updateChain()
       .set(versionTagColumn, versionTag)
       .where(applicationIdColumn.eq(applicationId))
       .where(versionTagColumn.eq(VersionTagEnum.RUNTIME.getValue()))
       .update();
   ```

**实践任务:**
- [ ] 实现一个自定义Repository,继承BaseBizRepository
- [ ] 实现版本发布功能(编辑态->运行态)
- [ ] 实现版本回滚功能(历史版->运行版)
- [ ] 编写单元测试验证租户隔离

#### Day 4-5: QueryWrapperUtils工具类

**学习文件:**
- `QueryWrapperUtils.java` (130行, 65次修改)

**学习重点:**
1. **方法重载的设计**
   ```java
   public static QueryColumn createApplicationIdColumn(ServiceImpl serviceImpl, QueryWrapper queryWrapper) { ... }
   public static QueryColumn createApplicationIdColumn(ServiceImpl serviceImpl) { ... }
   ```

2. **复杂条件判断的清晰化**
   ```java
   public static boolean isQueryFilterable(QueryWrapper queryWrapper) {
       // 不处理UNION类型
       List<UnionWrapper> unions = CPI.getUnions(queryWrapper);
       if (CollectionUtils.isNotEmpty(unions)) {
           return false;
       }
       // 不处理子查询
       List<QueryWrapper> childSelect = CPI.getChildSelect(queryWrapper);
       if (CollectionUtils.isNotEmpty(childSelect)) {
           return false;
       }
       // ...
       return true;
   }
   ```

3. **反射获取表名**
   ```java
   private static String getTableName(BaseMapper baseMapper) {
       Class<?> mapperClass = ClassUtil.getUsefulClass(baseMapper.getClass());
       Type type = mapperClass.getGenericInterfaces()[0];
       if (type instanceof ParameterizedType) {
           Class<?> modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
           Table tableAnnotation = modelClass.getAnnotation(Table.class);
           if (tableAnnotation != null) {
               return tableAnnotation.value();
           }
       }
       return null;
   }
   ```

**实践任务:**
- [ ] 实现一个自定义的QueryWrapper工具类
- [ ] 添加新的查询条件过滤逻辑
- [ ] 测试不同查询场景(UNION、子查询、JOIN)

#### Day 6-7: 综合实践

**实践项目:实现一个简单的版本管理系统**

**需求:**
1. 实体包含3个版本:编辑态(0)、运行态(1)、历史版(其他)
2. 可以从编辑态发布到运行态
3. 可以回滚到任意历史版本
4. 支持租户隔离

**验收标准:**
- [ ] 所有查询自动注入租户和版本条件
- [ ] 版本发布功能正常
- [ ] 版本回滚功能正常
- [ ] 单元测试覆盖率>80%

---

## 阶段三:Flow流程引擎(第4-6周)

### 🎯 学习目标

掌握Flow流程引擎的核心实现,理解流程执行、表达式计算、节点组件的设计模式。

### 📚 第4周:流程执行核心

#### Day 1-3: FlowProcessExecutor流程执行器

**学习文件:**
- `FlowProcessExecutor.java` (272行, 44次修改) ⭐⭐⭐⭐⭐

**学习重点:**

1. **流程执行的完整流程**
   ```java
   public ExecutorResult startExecution(ExecutorInput executorInput) {
       // 1. 参数验证
       if (StringUtils.isEmpty(traceId)) {
           return ExecutorResult.error("traceId不能为空");
       }

       // 2. 创建上下文
       ExecuteContext executeContext = createExecuteContext(executorInput);
       FlowExecutionLogDO executionLog = createNewExecutionLog(executorInput);

       try {
           // 3. 初始化变量
           VariableContext variableContext = new VariableContext();
           variableContext.setInputParams(executorInput.getInputParams());

           // 4. 执行流程
           ExecutorResult result = executeFlow(processId, variableContext, executeContext);

           // 5. 处理结果
           executionLog.setExecutionResult(result.isSuccess() ? ... : ...);
           return result;
       } catch (Exception e) {
           // 6. 异常处理
           log.error("执行流程异常", e);
           return ExecutorResult.error(processId, "执行流程异常", e);
       } finally {
           // 7. 保存日志
           executionLog.setLogText(executeContext.getLogText());
           executionLog.setEndTime(LocalDateTime.now());
           flowExecutionLogRepository.save(executionLog);
       }
   }
   ```

2. **try-finally保证资源清理**
   - 无论成功失败都保存执行日志
   - 计算执行时长
   - 记录详细错误信息

3. **私有方法提取,职责单一**
   ```java
   private ExecuteContext createExecuteContext(ExecutorInput executorInput) { ... }
   private FlowExecutionLogDO createNewExecutionLog(ExecutorInput executorInput) { ... }
   private ExecutorResult executeFlow(Long processId, VariableContext variableContext, ExecuteContext executeContext) { ... }
   private int validateTraceIdCallCount(String traceId, Long processId) { ... }
   ```

**实践任务:**
- [ ] 阅读并理解FlowProcessExecutor的完整执行流程
- [ ] 画出流程执行的时序图
- [ ] 实现一个简单的流程执行器
- [ ] 添加自定义的执行日志

#### Day 4-5: ExecuteContext执行上下文

**学习文件:**
- `ExecuteContext.java` (130行, 23次修改) ⭐⭐⭐⭐⭐
- `ExecuteLog.java` (31行) ⭐⭐⭐⭐⭐

**学习重点:**

1. **ExecuteContext - 线程安全设计**
   ```java
   @Data
   public class ExecuteContext implements Serializable {
       // 节点执行的结果
       private Map<String, Object> nodeProcessResults = new ConcurrentHashMap<>();

       private volatile boolean executeEnd = false;
       private volatile String executionEndNodeTag;
       private volatile Boolean abnormalTermination = Boolean.FALSE;

       // 节点配置数据
       private transient volatile Map<String, NodeData> nodeDataMap = new HashMap<>();

       public void setNodeDataMap(Map<String, NodeData> nodeData) {
           // 防御式复制
           this.nodeDataMap = Collections.unmodifiableMap(nodeData);
       }
   }
   ```

2. **ExecuteLog - 极简主义**
   ```java
   @Data
   public class ExecuteLog {
       private Stopwatch stopwatch;
       private List<String> logs;

       public ExecuteLog() {
           this.stopwatch = Stopwatch.createStarted();
           this.logs = new CopyOnWriteArrayList<>();
       }

       public void addLog(String msg) {
           String log = String.format("[%d] %s",
               stopwatch.elapsed(TimeUnit.MILLISECONDS), msg);
           logs.add(log);
       }
   }
   ```

3. **线程安全最佳实践**
   - ConcurrentHashMap用于高并发读写
   - CopyOnWriteArrayList用于读多写少
   - volatile保证可见性
   - Collections.unmodifiableMap防御式复制

**实践任务:**
- [ ] 实现一个自定义的ExecuteContext
- [ ] 添加自定义的日志字段
- [ ] 测试多线程场景下的线程安全性
- [ ] 实现日志的持久化

#### Day 6-7: 流程恢复与断点续传

**学习文件:**
- `FlowProcessExecutor.java` - `resumeExecution()`方法

**学习重点:**
```java
public ExecutorResult resumeExecution(ExecutorInput executorInput) {
    try {
        // 1. 恢复变量上下文
        VariableContext variableContext = flowContextProvider.restoreVariableContext(executionUuid);

        // 2. 恢复执行上下文
        executeContext = flowContextProvider.restoreExecuteContext(executionUuid);

        // 3. 生成新的执行UUID
        executeContext.setExecutionUuid(UUID.randomUUID().toString());

        // 4. 执行流程
        ExecutorResult result = executeFlow(processId, variableContext, executeContext);

        return result;
    } finally {
        // 5. 保存日志
        executionLog.setLogText(executeContext.getLogText());
        flowExecutionLogRepository.save(executionLog);
    }
}
```

**实践任务:**
- [ ] 实现流程上下文的持久化
- [ ] 实现流程的断点续传
- [ ] 测试流程恢复功能

---

### 📚 第5周:表达式引擎

#### Day 1-3: ExpressionExecutor表达式执行器

**学习文件:**
- `ExpressionExecutor.java` (344行, 16次修改) ⭐⭐⭐⭐⭐

**学习重点:**

1. **递归设计模式**
   ```java
   // 构建完整的条件表达式
   private String buildConditionExpression(OrExpression orExpression) {
       if (orExpression == null || CollectionUtils.isEmpty(orExpression.getAndExpressions())) {
           return "true";  // 边界条件
       }
       List<String> conditionExpressions = new ArrayList<>();
       for (AndExpression andExpresses : orExpression.getAndExpressions()) {
           String andExpression = buildAndExpression(andExpresses);
           if (andExpression != null && !andExpression.trim().isEmpty()) {
               conditionExpressions.add("(" + andExpression + ")");
           }
       }
       return String.join(" || ", conditionExpressions);
   }

   // 构建AND表达式
   private String buildAndExpression(AndExpression andExpression) {
       if (andExpression == null || CollectionUtils.isEmpty(andExpression.getExpressionItems())) {
           return "true";
       }
       List<String> expressionItems = new ArrayList<>();
       for (ExpressionItem expressionItem : andExpression.getExpressionItems()) {
           String itemExpression = buildExpressItemExpression(expressionItem);
           if (itemExpression != null && !itemExpression.trim().isEmpty()) {
               expressionItems.add(itemExpression);
           }
       }
       return String.join(" && ", expressionItems);
   }
   ```

2. **Switch表达式**
   ```java
   public String buildExpression(ExpressionItem expressionItem) {
       switch (expressionItem.getOp()) {
           case EQUALS:
               return String.format("%s == %s", fieldKey, formatValue(expressionItem));
           case CONTAINS:
               return String.format("%s.contains(%s)", fieldKey, formatValue(expressionItem));
           case GREATER_THAN:
               return String.format("%s > %s", fieldKey, formatValue(expressionItem));
           case IS_EMPTY:
               return String.format("(%s == null || %s.isEmpty())", fieldKey, fieldKey);
           // ...
           default:
               throw new UnsupportedOperationException("不支持的操作符: " + expressionItem.getOp());
       }
   }
   ```

3. **StringBuilder的使用**
   ```java
   private String buildContainsAllExpression(ExpressionItem expressionItem) {
       List listValue = (List) expressionItem.getFieldValue();
       StringBuilder sb = new StringBuilder();
       for (int i = 0; i < listValue.size(); i++) {
           if (i > 0) {
               sb.append(" && ");
           }
           sb.append(String.format("%s contains %s", fieldKey, formatValue(listValue.get(i))));
       }
       return sb.toString();
   }
   ```

**实践任务:**
- [ ] 实现一个简单的表达式引擎
- [ ] 支持5种操作符:==, !=, >, <, contains
- [ ] 支持AND和OR逻辑组合
- [ ] 编写单元测试

#### Day 4-5: JEXL与MVEL引擎集成

**学习内容:**
1. JEXL表达式引擎的使用
2. MVEL表达式引擎的使用
3. 自定义Arithmetic扩展
4. 表达式安全性控制

**代码示例:**
```java
public ExpressionExecutor() {
    JexlPermissions permissions = JexlPermissions.UNRESTRICTED;
    this.jexlEngine = new JexlBuilder()
        .permissions(permissions)
        .arithmetic(new ExtJexlArithmetic(false))
        .silent(false)
        .create();
}

public boolean evaluate(OrExpression orExpression, Map<String, Object> vars) {
    String fullExpression = buildConditionExpression(orExpression);
    JexlExpression expression = jexlEngine.createExpression(fullExpression);
    MapContext jc = new MapContext(vars);
    Boolean result = (Boolean) expression.evaluate(jc);
    return result;
}
```

**实践任务:**
- [ ] 使用JEXL引擎实现表达式计算
- [ ] 实现自定义函数
- [ ] 测试表达式安全性

#### Day 6-7: 表达式优化

**学习内容:**
1. 表达式缓存
2. 表达式编译优化
3. 表达式执行性能测试

**实践任务:**
- [ ] 实现表达式缓存
- [ ] 性能测试:1000次表达式执行
- [ ] 优化热点代码

---

### 📚 第6周:节点组件

#### Day 1-3: DataAddNodeComponent数据新增节点

**学习文件:**
- `DataAddNodeComponent.java` (138行, 22次修改) ⭐⭐⭐⭐⭐

**学习重点:**

1. **节点组件的标准结构**
   ```java
   @Slf4j
   @Setter
   @LiteflowComponent("dataAdd")
   public class DataAddNodeComponent extends SkippableNodeComponent {

       @Autowired
       private SemanticDynamicDataApi semanticDynamicDataApi;

       @Override
       public void process() throws Exception {
           // 1. 获取上下文
           ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
           VariableContext variableContext = this.getContextBean(VariableContext.class);
           DataAddNodeData nodeData = (DataAddNodeData) executeContext.getNodeData(this.getTag());

           // 2. 执行业务逻辑
           List<Map<String, Object>> reqDataList = buildReqData(nodeData, variableContext);
           enrichWithSystemFields(reqDataList, executeContext);

           // 3. 调用API
           for (Map<String, Object> reqData : reqDataList) {
               SemanticEntityValueDTO respDTO = semanticDynamicDataApi.insertData(reqDTO);
               respDTOSS.add(respDTO);
           }

           // 4. 处理响应
           processResponse(respDTOSS, variableContext, batchType);
       }
   }
   ```

2. **批处理与单处理**
   ```java
   if (batchType) {
       reqDataList = buildBatchReqData(nodeData, variableContext, conditionItems);
   } else {
       reqDataList = buildSingleReqData(conditionItems, expressionContext);
   }
   ```

3. **租户隔离的应用**
   ```java
   SemanticEntityValueDTO respDTO = TenantManager.withoutTenantCondition(() ->
       ApplicationManager.withApplicationIdAndVersionTag(
           executeContext.getApplicationId(),
           executeContext.getVersionTag(),
           () -> semanticDynamicDataApi.insertData(reqDTO)
       )
   );
   ```

**实践任务:**
- [ ] 实现一个DataUpdateNodeComponent
- [ ] 支持单条更新和批量更新
- [ ] 正确处理租户隔离
- [ ] 编写单元测试

#### Day 4-5: 其他节点组件

**学习文件:**
- `DataUpdateNodeComponent.java` (20次修改)
- `DataQueryNodeComponent.java` (19次修改)
- `DataDeleteNodeComponent.java` (16次修改)
- `IfCaseNodeComponent.java` (15次修改)
- `ModalNodeComponent.java` (14次修改)

**学习重点:**
1. 不同节点组件的设计模式
2. 节点跳过(Skippable)机制
3. 节点异常处理

**实践任务:**
- [ ] 阅读3个以上不同类型的节点组件
- [ ] 对比它们的共同点和差异
- [ ] 实现一个自定义节点组件

#### Day 6-7: 节点组件测试

**学习内容:**
1. 节点组件的单元测试
2. 节点组件的集成测试
3. Mock外部依赖

**实践任务:**
- [ ] 编写DataAddNodeComponent的单元测试
- [ ] 编写节点集成测试
- [ ] 测试覆盖率>80%

---

## 阶段四:App应用模块(第7周)

### 🎯 学习目标

掌握应用菜单、权限管理的设计实现,理解RBAC权限模型。

### 📚 第7周:App应用模块

#### Day 1-3: AppMenuRepository菜单仓储

**学习文件:**
- `AppMenuRepository.java` (20次修改) ⭐⭐⭐⭐⭐

**学习重点:**

1. **菜单树形结构**
   ```java
   public List<AppMenuDO> queryMenuTree(Long applicationId) {
       List<AppMenuDO> allMenus = QueryWrapperUtils.create()
           .where(AppMenuDO::getApplicationId).eq(applicationId)
           .and(AppMenuDO::getVersionTag).eq(versionTag)
           .and(AppMenuDO::getParentId).isNull()
           .orderBy(AppMenuDO::getSortNumber, true)
           .list();

       return buildMenuTree(allMenus);
   }

   private List<AppMenuDO> buildMenuTree(List<AppMenuDO> allMenus) {
       LinkedList<AppMenuDO> rootMenus = new LinkedList<>();
       Map<Long, AppMenuDO> menuMap = new HashMap<>();

       // 先全部放入Map
       for (AppMenuDO menu : allMenus) {
           menuMap.put(menu.getId(), menu);
       }

       // 建立父子关系
       for (AppMenuDO menu : allMenus) {
           if (menu.getParentId() == null) {
               rootMenus.add(menu);
           } else {
               AppMenuDO parent = menuMap.get(menu.getParentId());
               if (parent != null) {
                   if (parent.getChildMenus() == null) {
                       parent.setChildMenus(new LinkedList<>());
                   }
                   parent.getChildMenus().add(menu);
               }
           }
       }

       return rootMenus;
   }
   ```

2. **性能优化**
   - 使用LinkedList而非ArrayList
   - 一次性查询所有菜单
   - 使用Map建立索引

**实践任务:**
- [ ] 实现菜单树查询
- [ ] 实现菜单的CRUD操作
- [ ] 性能测试:1000条菜单的查询

#### Day 4-5: AppAuthPermissionService权限服务

**学习文件:**
- `AppAuthPermissionServiceImpl.java` (26次修改) ⭐⭐⭐⭐⭐

**学习重点:**

1. **RBAC权限模型**
   ```
   User(用户) - UserRole(用户角色) - Role(角色) - RoleMenu(角色菜单) - Menu(菜单)
   ```

2. **权限判断逻辑**
   ```java
   public boolean hasPermission(Long userId, String menuPermission) {
       // 1. 获取用户的所有角色
       List<Long> roleIds = getUserRoleIds(userId);

       // 2. 获取角色的所有菜单
       List<Long> menuIds = getRoleMenuIds(roleIds);

       // 3. 检查菜单权限
       return menuIds.contains(menuPermission);
   }
   ```

**实践任务:**
- [ ] 实现一个简单的RBAC权限系统
- [ ] 支持用户、角色、权限的CRUD
- [ ] 实现权限判断接口

#### Day 6-7: 应用版本管理

**学习内容:**
1. 应用版本发布流程
2. 应用版本回滚
3. 应用版本数据隔离

**实践任务:**
- [ ] 实现应用版本发布
- [ ] 实现应用版本回滚
- [ ] 编写集成测试

---

## 阶段五:实战项目(第8周)

### 🎯 学习目标

综合运用所学知识,完成一个完整的实战项目。

### 📚 第8周:实战项目

#### 项目:实现一个审批流程系统

**需求:**
1. 支持流程设计(可视化设计器可选)
2. 支持流程执行
3. 支持条件分支(如果金额>1000,需要主管审批)
4. 支持审批节点(同意、拒绝、转办)
5. 支持流程历史记录

**技术要求:**
1. 使用Flow引擎
2. 使用表达式引擎
3. 使用租户隔离
4. 使用版本管理

**验收标准:**
- [ ] 流程可以正常执行
- [ ] 条件分支正确判断
- [ ] 审批节点正常工作
- [ ] 流程历史完整记录
- [ ] 单元测试覆盖率>70%
- [ ] 集成测试通过

#### Day 1-2:流程设计

**任务:**
- [ ] 设计流程数据模型
- [ ] 设计流程定义表
- [ ] 设计流程实例表
- [ ] 设计节点配置表

#### Day 3-4:流程执行

**任务:**
- [ ] 实现流程执行器
- [ ] 实现条件判断
- [ ] 实现节点执行
- [ ] 实现流程日志

#### Day 5-6:审批节点

**任务:**
- [ ] 实现审批同意节点
- [ ] 实现审批拒绝节点
- [ ] 实现审批转办节点
- [ ] 实现审批历史

#### Day 7:测试与优化

**任务:**
- [ ] 编写单元测试
- [ ] 编写集成测试
- [ ] 性能优化
- [ ] 文档编写

---

## 学习检查清单

### ✅ 第1-2周检查清单

**框架层基础:**
- [ ] 理解Spring Boot Starter的设计思想
- [ ] 掌握MyBatis-Flex的基本使用
- [ ] 理解租户隔离的实现机制
- [ ] 理解版本管理的实现机制
- [ ] 能够编写自定义的Repository

**设计模式:**
- [ ] 理解模板方法模式
- [ ] 理解策略模式
- [ ] 理解工厂方法模式
- [ ] 能够应用这些模式

**代码规范:**
- [ ] 遵循命名规范
- [ ] 正确处理异常
- [ ] 正确记录日志
- [ ] 代码简洁清晰

### ✅ 第3周检查清单

**ORM框架:**
- [ ] 精读BaseBizRepository源码
- [ ] 精读QueryWrapperUtils源码
- [ ] 理解租户隔离的AOP实现
- [ ] 理解版本管理的实现
- [ ] 能够实现自定义Repository

**实践项目:**
- [ ] 实现版本管理系统
- [ ] 测试覆盖率>80%

### ✅ 第4-6周检查清单

**Flow引擎:**
- [ ] 理解FlowProcessExecutor的执行流程
- [ ] 理解ExecuteContext的线程安全设计
- [ ] 理解ExpressionExecutor的递归设计
- [ ] 理解节点组件的设计模式
- [ ] 能够实现自定义节点组件

**表达式引擎:**
- [ ] 能够使用JEXL引擎
- [ ] 能够使用MVEL引擎
- [ ] 能够扩展自定义函数
- [ ] 能够优化表达式性能

**实践项目:**
- [ ] 实现数据更新节点
- [ ] 实现自定义节点
- [ ] 测试覆盖率>70%

### ✅ 第7周检查清单

**App模块:**
- [ ] 理解菜单树形结构
- [ ] 理解RBAC权限模型
- [ ] 能够实现菜单管理
- [ ] 能够实现权限判断
- [ ] 理解应用版本管理

**实践项目:**
- [ ] 实现菜单树查询
- [ ] 实现权限系统
- [ ] 性能测试通过

### ✅ 第8周检查清单

**实战项目:**
- [ ] 完成审批流程系统
- [ ] 流程执行正常
- [ ] 条件判断正确
- [ ] 审批功能完整
- [ ] 单元测试覆盖率>70%
- [ ] 集成测试通过
- [ ] 代码符合规范

---

## 学习资源

### 📖 推荐书籍

1. **《Effective Java》** - Joshua Bloch
   - 学习Java最佳实践

2. **《设计模式:可复用面向对象软件的基础》** - GoF
   - 学习设计模式

3. **《重构:改善既有代码的设计》** - Martin Fowler
   - 学习代码重构

4. **《Clean Code》** - Robert C. Martin
   - 学习代码整洁之道

### 🌐 在线资源

1. **MyBatis-Flex官方文档**
   - https://mybatis-flex.com/

2. **LiteFlow官方文档**
   - https://liteflow.cc/

3. **Spring Boot官方文档**
   - https://spring.io/projects/spring-boot

### 🛠️ 开发工具

1. **IntelliJ IDEA** - 代码编辑器
2. **DBeaver** - 数据库工具
3. **Postman** - API测试工具
4. **Git** - 版本控制

---

## 学习建议

### 💡 学习技巧

1. **先阅读后实践**
   - 先完整阅读代码
   - 理解设计思想
   - 再动手实践

2. **画图辅助理解**
   - 画类图
   - 画时序图
   - 画流程图

3. **循序渐进**
   - 不要急于求成
   - 按照学习计划
   - 逐步深入

4. **多做笔记**
   - 记录关键代码
   - 记录设计思想
   - 记录遇到的问题

5. **多问为什么**
   - 为什么这样设计?
   - 有没有更好的方式?
   - 如果是我会怎么做?

### ⚠️ 常见错误

1. **只看不动手**
   - ❌ 只是阅读代码
   - ✅ 边看边实践

2. **急于求成**
   - ❌ 想一次性学完
   - ✅ 分阶段逐步学习

3. **不理解设计**
   - ❌ 只记住代码
   - ✅ 理解设计思想

4. **不写测试**
   - ❌ 只写功能代码
   - ✅ 同时编写测试

5. **孤立学习**
   - ❌ 自己一个人闷头学
   - ✅ 与他人讨论交流

### 🤝 获取帮助

1. **向同事请教**
   - Huangjie本人
   - 其他团队成员

2. **查阅文档**
   - 技术文档
   - API文档
   - 设计文档

3. **搜索资料**
   - Stack Overflow
   - GitHub Issues
   - 技术博客

---

## 总结

这份学习路径基于Huangjie的代码贡献,按照**从易到难、从基础到应用**的原则设计,共8周时间。

**核心模块:**
1. ✅ ORM框架 - BaseBizRepository, QueryWrapperUtils
2. ✅ Flow引擎 - FlowProcessExecutor, ExpressionExecutor, 节点组件
3. ✅ App模块 - AppMenuRepository, 权限管理

**学习目标:**
- 掌握OneBase框架的核心技术
- 理解Huangjie的设计思想
- 能够独立开发新功能
- 代码质量达到Huangjie水平

**祝你学习成功!** 🚀

---

**文档版本:** v1.0
**制定日期:** 2025-12-27
**制定者:** Kanten (Claude Code)
**基于:** Huangjie的220+次代码提交

---

> 💡 **提示**: 这份学习路径是一个指南,请根据实际情况灵活调整。重要的是理解设计思想,而不是死记硬背代码。祝学习顺利!
