# OneBase v3.0 学习日志 - Day 2 学习计划

**学习日期**: 2026-01-01  
**学习主题**: 数据模型与ORM框架  
**学习者**: Kanten  
**预计时长**: 8小时

---

## 一、学习目标

### 今日目标
- [ ] 理解OneBase的数据模型设计
- [ ] 掌握通用字段约定
- [ ] 理解多租户隔离机制
- [ ] 掌握ORM框架的核心功能

### 预期成果
- 能说出通用字段的7个字段及其作用
- 能解释多租户隔离的实现机制
- 能解释版本管理的三种状态
- 能熟练使用QueryWrapperUtils进行查询
- 能使用UpdateChain进行链式更新

---

## 二、上午学习 (9:00-12:00)

### 1. 数据模型概览 (9:00-10:00)

#### 学习内容
阅读数据模型文档，理解表结构设计

#### 学习文件
- [`docs/data-models-backend.md`](data-models-backend.md)

#### 学习笔记

**通用字段约定**（7个字段）：
```sql
-- 所有业务表都包含的通用字段
id              BIGINT          -- 主键ID
tenant_id       BIGINT          -- 租户ID（0=平台级，>0=租户级）
application_id  BIGINT          -- 应用ID
version_tag     INT             -- 版本标签（0=编辑态，1=运行态，2,3,4...=历史版）
creator         VARCHAR(64)     -- 创建人
create_time     TIMESTAMP      -- 创建时间
updater         VARCHAR(64)     -- 更新人
update_time     TIMESTAMP      -- 更新时间
deleted         BOOLEAN         -- 逻辑删除标记
```

**表分类**（100+张表）：
```
System模块（30+张）：
├── system_users              -- 用户表
├── system_role               -- 角色表
├── system_tenant             -- 租户表
└── ...

App模块（20+张）：
├── app_application           -- 应用表
├── app_menu                  -- 菜单表
├── app_auth_role             -- 角色表
└── ...

Flow模块（10+张）：
├── flow_process              -- 流程定义表
├── flow_process_exec         -- 流程执行表
├── flow_execution_log        -- 执行日志表
└── ...

Metadata模块（20+张）：
├── metadata_business_entity  -- 业务实体表
├── metadata_entity_field     -- 实体字段表
└── ...
```

#### 理解程度
- [ ] 理解了通用字段的7个字段及其作用
- [ ] 理解了表的分类和数量
- [ ] 理解了多租户和版本管理的字段含义

### 2. 核心表结构学习 (10:00-11:00)

#### 学习内容
学习核心表的结构和关系

#### 学习文件
- 使用DBeaver连接数据库查看表结构
- 查看ER图（如果有）

#### 学习笔记

**App模块核心表**：
```sql
-- 应用表
CREATE TABLE app_application (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    application_name VARCHAR(100),
    application_code VARCHAR(50),
    version_tag INT DEFAULT 0,
    ...
);

-- 菜单表
CREATE TABLE app_menu (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    parent_id BIGINT,              -- 父菜单ID，NULL表示根菜单
    menu_name VARCHAR(100),
    menu_code VARCHAR(50),
    sort_number INT,               -- 排序号
    version_tag INT DEFAULT 0,
    ...
);

-- 角色表
CREATE TABLE app_auth_role (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    role_name VARCHAR(100),
    role_code VARCHAR(50),
    version_tag INT DEFAULT 0,
    ...
);
```

**System模块核心表**：
```sql
-- 用户表
CREATE TABLE system_users (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    username VARCHAR(50),
    password VARCHAR(100),
    ...
);

-- 租户表
CREATE TABLE system_tenant (
    id BIGINT PRIMARY KEY,
    tenant_name VARCHAR(100),
    tenant_code VARCHAR(50),
    ...
);
```

**Flow模块核心表**：
```sql
-- 流程定义表
CREATE TABLE flow_process (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    process_name VARCHAR(100),
    process_code VARCHAR(50),
    nodes JSONB,                   -- 节点配置（JSON格式）
    edges JSONB,                   -- 连线配置（JSON格式）
    version_tag INT DEFAULT 0,
    ...
);

-- 流程执行表
CREATE TABLE flow_process_exec (
    id BIGINT PRIMARY KEY,
    process_id BIGINT NOT NULL,
    execution_uuid VARCHAR(50),
    status VARCHAR(20),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    ...
);

-- 执行日志表
CREATE TABLE flow_execution_log (
    id BIGINT PRIMARY KEY,
    process_id BIGINT NOT NULL,
    execution_uuid VARCHAR(50),
    log_text TEXT,
    error_message TEXT,
    ...
);
```

#### 理解程度
- [ ] 理解了App模块的核心表结构
- [ ] 理解了System模块的核心表结构
- [ ] 理解了Flow模块的核心表结构
- [ ] 能说出表之间的关联关系

### 3. 多租户与版本管理 (11:00-12:00)

#### 学习内容
深入理解多租户隔离和版本管理机制

#### 学习笔记

**多租户隔离机制**：
```
隔离级别：表隔离（共享数据库、共享表、租户字段隔离）

实现方式：
1. 所有业务表都有tenant_id字段
2. tenant_id = 0：平台级数据（所有租户可见）
3. tenant_id > 0：租户级数据（仅该租户可见）
4. 查询时自动注入tenant_id条件（通过BaseBizRepository）

示例：
SELECT * FROM app_menu 
WHERE tenant_id = 1 
  AND application_id = 100 
  AND version_tag = 1;
```

**版本管理机制**：
```
版本状态：
- version_tag = 0：编辑态（BUILD）
  - 设计时使用，可以修改
  - 前端设计器操作的数据
  
- version_tag = 1：运行态（RUNTIME）
  - 运行时使用，只读
  - 用户实际使用的数据
  
- version_tag = 2,3,4...：历史版（HISTORY）
  - 历史版本，只读
  - 用于版本回滚

版本发布流程：
编辑态(0) → 运行态(1) → 历史版(2,3,4...)

版本回滚流程：
历史版(2) → 运行态(1)
```

**租户+版本隔离**：
```
查询条件自动注入：
WHERE tenant_id = ?           -- 租户隔离
  AND application_id = ?      -- 应用隔离
  AND version_tag = ?         -- 版本隔离
  AND deleted = false         -- 逻辑删除
```

#### 理解程度
- [ ] 理解了多租户隔离的实现机制
- [ ] 理解了版本管理的三种状态
- [ ] 理解了版本发布和回滚流程
- [ ] 理解了租户+版本的双重隔离

---

## 三、下午学习 (14:00-17:00)

### 1. ORM框架基础 (14:00-15:00)

#### 学习内容
学习MyBatis-Flex ORM框架的基础知识

#### 学习文件
- MyBatis-Flex官方文档：https://mybatis-flex.com/

#### 学习笔记

**MyBatis-Flex简介**：
```
MyBatis-Flex是基于MyBatis的增强框架，提供：
1. 链式查询API
2. 自动填充
3. 逻辑删除
4. 乐观锁
5. 多租户
6. 数据审计
```

**基础用法**：
```java
// 1. 实体类定义
@Table("app_menu")
public class AppMenuDO {
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private Long tenantId;
    private Long applicationId;
    private Long parentId;
    private String menuName;
    private Integer sortNumber;
    private Integer versionTag;
    
    // 通用字段
    private String creator;
    private LocalDateTime createTime;
    private String updater;
    private LocalDateTime updateTime;
    private Boolean deleted;
}

// 2. Mapper接口
public interface AppMenuMapper extends BaseMapper<AppMenuDO> {
}

// 3. 基础查询
List<AppMenuDO> menus = appMenuMapper.selectList(
    QueryWrapper.create()
        .where(AppMenuDO::getApplicationId).eq(applicationId)
        .and(AppMenuDO::getVersionTag).eq(versionTag)
        .and(AppMenuDO::getParentId).isNull()
        .orderBy(AppMenuDO::getSortNumber, true)
);

// 4. 基础插入
AppMenuDO menu = new AppMenuDO();
menu.setMenuName("测试菜单");
menu.setSortNumber(1);
appMenuMapper.insert(menu);

// 5. 基础更新
appMenuMapper.update(
    UpdateChain.of(AppMenuDO.class)
        .set(AppMenuDO::getMenuName, "新名称")
        .where(AppMenuDO::getId).eq(id)
);

// 6. 基础删除
appMenuMapper.deleteById(id);
```

#### 理解程度
- [ ] 理解了MyBatis-Flex的基本概念
- [ ] 掌握了基础的CRUD操作
- [ ] 理解了链式查询API的使用

### 2. BaseBizRepository源码阅读 (15:00-16:00)

#### 学习内容
阅读BaseBizRepository源码，理解核心功能

#### 学习文件
- [`onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java)

#### 学习笔记

**BaseBizRepository核心功能**：
```java
/**
 * 业务基础仓储
 * 
 * 核心功能：
 * 1. 自动注入租户条件（AOP思想）
 * 2. 版本管理（编辑态、运行态、历史版）
 * 3. UpdateChain链式更新
 * 
 * 设计模式：模板方法模式
 */
public class BaseBizRepository<T> extends ServiceImpl<M, T> {
    
    // ========== 核心功能1：自动注入租户条件 ==========
    
    /**
     * 查询单个对象（自动注入租户和版本条件）
     */
    @Override
    public T getOne(QueryWrapper query) {
        // 模板方法：自动注入查询条件
        injectQueryFilter(query);
        return getMapper().selectOneByQuery(query);
    }
    
    /**
     * 查询列表（自动注入租户和版本条件）
     */
    @Override
    public List<T> list(QueryWrapper query) {
        injectQueryFilter(query);
        return getMapper().selectListByQuery(query);
    }
    
    /**
     * 注入查询过滤条件（租户+版本）
     */
    protected void injectQueryFilter(QueryWrapper queryWrapper) {
        // 1. 检查是否忽略应用条件
        if (ApplicationManager.isIgnoreApplicationCondition()) {
            return;
        }
        
        // 2. 获取当前应用ID和版本标签
        Long applicationId = ApplicationManager.getApplicationId();
        Long versionTag = ApplicationManager.getVersionTag();
        
        // 3. 创建查询列
        QueryColumn applicationIdColumn = QueryWrapperUtils.createApplicationIdColumn(this, queryWrapper);
        QueryColumn versionTagColumn = QueryWrapperUtils.createVersionTagColumn(this, queryWrapper);
        
        // 4. 注入条件
        queryWrapper.and(applicationIdColumn.eq(applicationId));
        queryWrapper.and(versionTagColumn.eq(versionTag));
    }
    
    // ========== 核心功能2：版本管理 ==========
    
    /**
     * 版本发布：编辑态 → 运行态
     */
    public void copyEditToRuntime(Long applicationId) {
        // 1. 查询编辑态数据
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(applicationIdColumn.eq(applicationId))
            .where(versionTagColumn.eq(VersionTagEnum.BUILD.getValue()));
        List<T> entities = this.getMapper().selectListByQuery(queryWrapper);
        
        // 2. 复制数据到运行态
        entities.forEach(entity -> {
            entity.setId(null);                    // 清空ID，生成新记录
            entity.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        });
        
        // 3. 批量保存
        super.saveBatch(entities);
    }
    
    /**
     * 版本回滚：历史版 → 运行态
     */
    public void copyHistoryToRuntime(Long applicationId, Long historyVersionTag) {
        // 1. 查询历史版本数据
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(applicationIdColumn.eq(applicationId))
            .where(versionTagColumn.eq(historyVersionTag));
        List<T> entities = this.getMapper().selectListByQuery(queryWrapper);
        
        // 2. 复制数据到运行态
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setVersionTag(VersionTagEnum.RUNTIME.getValue());
        });
        
        // 3. 批量保存
        super.saveBatch(entities);
    }
    
    // ========== 核心功能3：链式更新 ==========
    
    /**
     * 链式更新（支持租户和版本条件）
     */
    public UpdateChain updateChain() {
        return UpdateChain.of(getMapper());
    }
}
```

**设计模式分析**：
```
模板方法模式：
- BaseBizRepository定义了查询的模板（getOne、list）
- 子类无需关心租户隔离，自动注入条件
- 通过injectQueryFilter方法实现横切关注点

AOP思想：
- 在父类统一处理租户隔离
- 子类无需重复代码
- 类似于Spring AOP的切面编程
```

#### 理解程度
- [ ] 理解了BaseBizRepository的3大核心功能
- [ ] 理解了自动注入租户条件的实现
- [ ] 理解了版本管理的实现逻辑
- [ ] 理解了模板方法模式的应用

### 3. QueryWrapperUtils工具类 (16:00-17:00)

#### 学习内容
学习QueryWrapperUtils工具类的使用

#### 学习文件
- [`onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java)

#### 学习笔记

**QueryWrapperUtils核心功能**：
```java
/**
 * 查询包装工具类
 * 
 * 核心功能：
 * 1. 自动创建applicationId和versionTag列
 * 2. 判断查询是否适合自动注入
 * 3. 反射获取表名
 */
public class QueryWrapperUtils {
    
    /**
     * 创建查询包装器（链式API）
     */
    public static QueryWrapper create() {
        return QueryWrapper.create();
    }
    
    /**
     * 创建applicationId列
     */
    public static QueryColumn createApplicationIdColumn(ServiceImpl serviceImpl, QueryWrapper queryWrapper) {
        // 1. 获取表名
        String tableName = getTableName(serviceImpl.getMapper());
        
        // 2. 创建列
        return QueryColumn.of().table(tableName).as("application_id");
    }
    
    /**
     * 创建versionTag列
     */
    public static QueryColumn createVersionTagColumn(ServiceImpl serviceImpl, QueryWrapper queryWrapper) {
        String tableName = getTableName(serviceImpl.getMapper());
        return QueryColumn.of().table(tableName).as("version_tag");
    }
    
    /**
     * 判断查询是否适合自动注入
     */
    public static boolean isQueryFilterable(QueryWrapper queryWrapper) {
        // 1. 不处理UNION类型
        List<UnionWrapper> unions = CPI.getUnions(queryWrapper);
        if (CollectionUtils.isNotEmpty(unions)) {
            return false;
        }
        
        // 2. 不处理子查询
        List<QueryWrapper> childSelect = CPI.getChildSelect(queryWrapper);
        if (CollectionUtils.isNotEmpty(childSelect)) {
            return false;
        }
        
        // 3. 不处理JOIN查询
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtils.isNotEmpty(joins)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 反射获取表名
     */
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
}
```

**使用示例**：
```java
// 1. 基础查询
List<AppMenuDO> menus = QueryWrapperUtils.create()
    .where(AppMenuDO::getApplicationId).eq(applicationId)
    .and(AppMenuDO::getVersionTag).eq(versionTag)
    .and(AppMenuDO::getParentId).isNull()
    .orderBy(AppMenuDO::getSortNumber, true)
    .list();

// 2. 复杂查询
List<AppMenuDO> menus = QueryWrapperUtils.create()
    .where(AppMenuDO::getApplicationId).eq(applicationId)
    .and(AppMenuDO::getVersionTag).eq(versionTag)
    .and(AppMenuDO::getMenuName).like("测试")
    .or(AppMenuDO::getMenuCode).eq("TEST")
    .orderBy(AppMenuDO::getSortNumber, true)
    .limit(10)
    .list();

// 3. 链式更新
UpdateChain.of(appMenuMapper)
    .set(AppMenuDO::getMenuName, "新名称")
    .set(AppMenuDO::getSortNumber, 100)
    .where(AppMenuDO::getId).eq(id)
    .update();
```

#### 理解程度
- [ ] 理解了QueryWrapperUtils的核心功能
- [ ] 掌握了链式查询API的使用
- [ ] 理解了自动创建列的实现
- [ ] 理解了反射获取表名的方法

---

## 四、晚上学习 (19:00-21:00)

### 1. ORM实践 (19:00-20:00)

#### 实践任务
编写测试代码，验证ORM框架的功能

#### 实践代码

```java
/**
 * ORM框架实践测试
 */
@SpringBootTest
public class OrmPracticeTest {
    
    @Autowired
    private AppMenuRepository appMenuRepository;
    
    @Autowired
    private AppMenuMapper appMenuMapper;
    
    /**
     * 练习1：基础查询
     */
    @Test
    public void testBasicQuery() {
        // 设置应用上下文
        ApplicationManager.setApplicationId(100L);
        ApplicationManager.setVersionTag(1L);
        
        // 查询菜单列表
        List<AppMenuDO> menus = QueryWrapperUtils.create()
            .where(AppMenuDO::getApplicationId).eq(100L)
            .and(AppMenuDO::getVersionTag).eq(1L)
            .and(AppMenuDO::getParentId).isNull()
            .orderBy(AppMenuDO::getSortNumber, true)
            .list();
        
        System.out.println("查询到 " + menus.size() + " 条菜单");
    }
    
    /**
     * 练习2：使用Repository查询（自动注入租户条件）
     */
    @Test
    public void testRepositoryQuery() {
        // 设置应用上下文
        ApplicationManager.setApplicationId(100L);
        ApplicationManager.setVersionTag(1L);
        
        // 使用Repository查询（自动注入租户和版本条件）
        List<AppMenuDO> menus = appMenuRepository.list(
            QueryWrapper.create()
                .where(AppMenuDO::getParentId).isNull()
        );
        
        System.out.println("查询到 " + menus.size() + " 条菜单");
    }
    
    /**
     * 练习3：链式更新
     */
    @Test
    public void testUpdateChain() {
        UpdateChain.of(appMenuMapper)
            .set(AppMenuDO::getMenuName, "新名称")
            .where(AppMenuDO::getId).eq(1L)
            .update();
    }
    
    /**
     * 练习4：版本管理
     */
    @Test
    public void testVersionManagement() {
        // 版本发布：编辑态 → 运行态
        appMenuRepository.copyEditToRuntime(100L);
        
        // 版本回滚：历史版 → 运行态
        appMenuRepository.copyHistoryToRuntime(100L, 2L);
    }
}
```

#### 实践结果
- [ ] 成功执行基础查询
- [ ] 成功使用Repository查询
- [ ] 成功执行链式更新
- [ ] 成功执行版本管理

### 2. 笔记整理 (20:00-21:00)

#### 整理内容
整理今天的学习笔记，绘制架构图

#### 架构图

```
┌─────────────────────────────────────────────────────────┐
│                   ORM框架架构                           │
└─────────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              BaseBizRepository（业务基础仓储）            │
│                                                         │
│  核心功能：                                             │
│  1. 自动注入租户条件（AOP思想）                         │
│  2. 版本管理（编辑态、运行态、历史版）                   │
│  3. UpdateChain链式更新                                 │
│                                                         │
│  设计模式：模板方法模式                                  │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│            QueryWrapperUtils（查询工具）                  │
│                                                         │
│  核心功能：                                             │
│  1. 自动创建applicationId和versionTag列                  │
│  2. 判断查询是否适合自动注入                             │
│  3. 反射获取表名                                        │
└────────────┬────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│              MyBatis-Flex（ORM框架）                     │
│                                                         │
│  功能：                                                 │
│  1. 链式查询API                                         │
│  2. 自动填充                                            │
│  3. 逻辑删除                                            │
│  4. 乐观锁                                              │
│  5. 多租户                                              │
│  6. 数据审计                                            │
└─────────────────────────────────────────────────────────┘
```

#### 多租户隔离流程图

```
┌──────────────┐
│  查询请求     │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Repository  │
│  getOne()   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ injectQuery │
│  Filter()   │  ←── 自动注入租户和版本条件
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Mapper     │
│  selectOne  │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  执行SQL     │
│  WHERE ...  │
│  tenant_id  │
│  application_id
│  version_tag│
└──────────────┘
```

#### 版本管理流程图

```
编辑态(0)                    运行态(1)                    历史版(2,3,4...)
    │                            │                            │
    │  copyEditToRuntime()       │  copyHistoryToRuntime()    │
    └───────────────────────────┼────────────────────────────┘
                                 │
                                 ▼
                          版本发布/回滚
```

---

## 五、今日总结

### 完成情况
- [ ] 理解OneBase的数据模型设计
- [ ] 掌握通用字段约定
- [ ] 理解多租户隔离机制
- [ ] 掌握ORM框架的核心功能

### 验收标准检查
- [ ] 能说出通用字段的7个字段及其作用
- [ ] 能解释多租户隔离的实现机制
- [ ] 能解释版本管理的三种状态
- [ ] 能熟练使用QueryWrapperUtils进行查询
- [ ] 能使用UpdateChain进行链式更新

### 学习成果
1. **数据模型理解**：理解了100+张表的分类和通用字段约定
2. **多租户机制**：理解了表隔离的实现方式和自动注入机制
3. **版本管理**：理解了编辑态、运行态、历史版的管理流程
4. **ORM框架**：掌握了BaseBizRepository和QueryWrapperUtils的使用

### 遇到的问题
1. **反射获取表名**：理解了通过反射获取@Table注解的value值
2. **AOP思想**：理解了在父类统一处理横切关注点的设计
3. **模板方法模式**：理解了BaseBizRepository的模板方法设计

### 学习心得
1. **框架设计优秀**：BaseBizRepository的设计非常优雅，自动注入租户条件
2. **代码复用性强**：通过继承BaseBizRepository，子类无需关心租户隔离
3. **版本管理清晰**：编辑态、运行态、历史版的划分很清晰
4. **链式API友好**：QueryWrapperUtils的链式API使用起来很方便

### 明日计划
- 开始Flow模块学习
- 使用API调试方法学习Flow模块
- 理解FlowProcessExecutor的执行流程
- 理解ExecuteContext的上下文管理

---

## 六、学习资源

### 今日阅读的文档
- [`docs/data-models-backend.md`](data-models-backend.md)
- [`onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java)
- [`onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java`](../onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java)

### 今日参考的代码
- `onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/repository/BaseBizRepository.java`
- `onebase-framework/onebase-spring-boot-starter-orm/src/main/java/com/cmsr/onebase/framework/orm/util/QueryWrapperUtils.java`

### 今日使用的工具
- DBeaver - 数据库工具
- IntelliJ IDEA - 代码编辑器
- MyBatis-Flex文档 - 在线文档

---

**学习日志版本**: 1.0  
**最后更新**: 2026-01-01 21:00  
**学习者**: Kanten