---
description: AI 代码生成完整指南 (SDLC)
---

# AI 代码生成完整指南

本指南专为 AI 代码生成设计，整合了开发流程的所有标准和规范，按照软件开发生命周期 (SDLC) 组织。

## 0. AI 交互偏好

### 语言设置
- **响应语言**: 中文（简体中文）
- **代码注释**: 中文
- **文档说明**: 中文
- **异常信息**: 保持英文原文，但提供中文解释

### 交互风格
- **技术深度**: 提供详细的技术说明和原理解释
- **代码示例**: 包含完整的、可运行的代码示例
- **最佳实践**: 主动提供相关的最佳实践建议
- **问题排查**: 提供系统化的排查步骤和解决方案

---

## 1. 需求分析阶段 (Requirements)

在开始编码前，必须明确需求。

### 1.1 任务分析
- **理解需求**: 仔细阅读需求描述或 Bug 报告
- **复述需求**: 用自己的话复述需求，确认理解正确
- **识别影响**: 确定受影响的模块和组件
- **评估风险**: 识别技术难点和潜在风险
- **制定方案**: 基于最佳实践设计实现方案

---

## 2. 设计阶段 (Design)

### 2.1 分层架构规范
```
Controller 层  → 处理 HTTP 请求，参数验证
    ↓
Service 层     → 业务逻辑，事务控制
    ↓
Repository 层  → 数据访问，SQL 操作
```
**规则**:
- Controller 不直接调用 Repository
- Service 层方法使用 `@Transactional` 控制事务
- 跨模块调用通过接口，避免直接依赖

### 2.2 接口设计
- **RESTful**: 使用标准 HTTP 方法 (GET, POST, PUT, DELETE)
- **URL 命名**: 小写连字符 (e.g., `/api/user-profiles`)
- **响应格式**: 统一使用 `Result<T>`
- **数据传输**: 使用 DTO 传递数据，不暴露 Entity

### 2.3 开发原则
1. **单一职责 (SRP)**: 每个类/方法只做一件事
2. **开闭原则 (OCP)**: 对扩展开放，对修改关闭
3. **依赖倒置 (DIP)**: 依赖抽象而非具体实现
4. **DRY 原则**: 不重复代码
5. **KISS 原则**: 保持简单
6. **YAGNI 原则**: 避免无用功能

---

## 3. 编码阶段 (Coding)

### 3.1 编码规范

#### 3.1.1 Java 代码风格
- **类命名**: PascalCase (`UserController`, `UserService`)
- **方法命名**: camelCase (`findById`, `createUser`)
- **常量命名**: UPPER_SNAKE_CASE
- **包命名**: `com.cmsr.onebase.<module>.<submodule>`

#### 3.1.2 导入规范
- **禁止**: 全限定类名、通配符导入 (`import com.cmsr.*`)
- **顺序**: java.* -> 第三方库 -> 项目内部包

#### 3.1.3 注释规范 (JavaDoc)
- **类/接口**: 必须包含 `@author` (当前主机用户名) 和 `@date`
- **方法**: 必须包含 `@param`, `@return`, `@throws`
- **字段**: 重要字段简要说明
- **TODO**: 标记待办事项

#### 3.1.4 异常处理
- **工具类**: 静态导入 `ServiceExceptionUtil.exception`
- **错误码**: 在 `ErrorCodeConstants` 定义，支持占位符
- **抛出**: `throw exception(ErrorCodeConstants.USER_NOT_EXISTS);`
- **捕获**: 不吞异常，必须记录日志或抛出

#### 3.1.5 日志记录
- **框架**: SLF4J + Logback
- **级别**: INFO (关键流程), ERROR (异常), DEBUG (开发调试)
- **脱敏**: 严禁记录密码、Token 等敏感信息
- **清晰**: 包含足够的上下文信息 (如 `userId`, 参数)

### 3.2 代码模板

#### Controller 模板
```java
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 用户管理控制器
 * @author chengyuansen
 * @date 2025-12-27
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** 用户服务 */
    @Resource
    private UserService userService;

    /**
     * 创建用户
     * @param request 创建用户请求
     * @return 创建的用户信息
     */
    @PostMapping
    public Result<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("创建用户请求: {}", request.getUsername());
        UserDTO user = userService.createUser(request);
        return Result.success(user);
    }
}
```

#### Service 模板
```java
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 用户服务实现
 * @author chengyuansen
 * @date 2025-12-27
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    /**
     * 创建用户
     * @throws ServiceException 用户名已存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw exception(ErrorCodeConstants.USER_USERNAME_EXISTS);
        }
        // ... 业务逻辑
        return UserConverter.toDTO(user);
    }
}
```

#### Repository 模板 (MyBatis-Flex)
```java
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问层
 * @author chengyuansen
 * @date 2025-12-27
 */
@Repository
public class UserDataRepository extends ServiceImpl<UserMapper, UserDO> {

    public UserDO findByUsername(String username) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(UserDO.USERNAME, username);
        return getOne(queryWrapper);
    }
}
```

### 3.3 性能与安全

#### 性能优化
- **数据库**: 添加索引，避免 N+1 (使用 JOIN/批量)，使用分页
- **缓存**: 合理使用 Redis (分布式) 和 Caffeine (本地)
- **代码**: 避免循环内数据库调用，使用 StringBuilder，及时关闭资源

#### 安全实践
- **输入验证**: 使用 `@Valid`, `@NotNull`，过滤特殊字符
- **SQL 注入**: 必须使用参数化查询 (MyBatis/JPA 默认支持)
- **敏感数据**: 密码 BCrypt 加密，日志脱敏

---

## 4. 测试阶段 (Testing)

### 4.1 测试策略
- **金字塔模型**: 单元测试 (Unit) > 集成测试 (Integration) > 端到端测试 (E2E)
- **原则**: 尽早测试，测试即文档

### 4.2 测试分类
1. **单元测试**: JUnit 5 + Mockito。针对 Service/Utils，不依赖外部资源。
2. **集成测试**: `@SpringBootTest`。验证组件协作、DB 交互。
3. **API 测试**: `MockMvc`。验证 Controller 接口。

### 4.3 最佳实践
- **隔离**: 测试用例独立，自动清理数据 (`@Transactional`)
- **断言**: 清晰明确 (AssertJ)
- **Mock**: 只 Mock 外部依赖
- **覆盖率**: 核心分支 > 85%

### 4.4 调试技巧
- **日志调试**: 开启 DEBUG 日志
- **常见问题**:
  - `400`: 参数校验失败
  - `404`: 路径错误
  - `500`: 查看异常堆栈
  - `NPE`: Mock 对象未注入

---

## 5. 构建阶段 (Build)

### 5.1 环境准备
- **JDK**: Java 17
- **Maven**: 3.8+

### 5.2 构建命令
```bash
# 快速构建 (跳过测试) - 开发用
mvn clean install -DskipTests -T 8

# 完整构建 (含测试) - CI 用
mvn clean install -T 8

# 指定模块构建
mvn clean install -pl module-name -am -T 8
```

---

## 6. 开发工具配置

### Maven 配置
- 编码: UTF-8
- 版本管理: `onebase-dependencies`

### IDE 配置
- Lombok: 开启
- 格式化: 遵循项目规范
- 自动导入: 优化顺序

---

## 7. 跨模块协作

### 模块依赖原则
- 业务模块不直接依赖其他业务模块
- 通过接口和事件进行模块间通信
- 共享代码放在 `onebase-framework`
- 避免循环依赖

### 接口设计
- 定义清晰的接口契约
- 使用 DTO 传递数据
- 版本化 API 接口

---

## 8. ✅ 验证清单 (Checklist)

生成代码后，AI 必须自动检查以下所有项目：

### 8.1 基础规范
- [ ] 所有类都有导入语句，无全限定类名
- [ ] 所有类/接口都有 JavaDoc 注释（包含 `@author` 和 `@date`）
- [ ] 所有公共方法都有 JavaDoc 注释（包含 `@param`、`@return`、`@throws`）
- [ ] 重要字段都有注释说明
- [ ] 复杂逻辑有行内注释
- [ ] 待完成工作有 TODO 标记
- [ ] 类、方法、属性命名准确易懂

### 8.2 异常处理
- [ ] 静态导入 `ServiceExceptionUtil.exception`
- [ ] 在 `ErrorCodeConstants` 中定义错误码
- [ ] 使用 `exception(ErrorCodeConstants.XXX)` 抛出异常
- [ ] 所有 catch 块都记录日志或抛出异常
- [ ] 异常信息使用错误码，支持占位符

### 8.3 日志记录
- [ ] 关键操作有日志记录（INFO 级别）
- [ ] 异常有日志记录（ERROR 级别）
- [ ] 敏感信息已脱敏
- [ ] 日志信息清晰且必要

### 8.4 事务管理
- [ ] 写操作使用 `@Transactional(rollbackFor = Exception.class)`
- [ ] 事务边界在 Service 层

### 8.5 响应格式
- [ ] Controller 返回统一的 `Result<T>` 格式
- [ ] 使用 `@Valid` 进行参数校验

### 8.6 依赖注入
- [ ] 统一使用 `@Resource` 注入依赖
- [ ] 避免使用 `@Autowired`

### 8.7 编译验证
- [ ] 代码可以成功编译，无语法错误
- [ ] 所有导入的类和包都存在
- [ ] 方法签名和类型匹配正确

### 8.8 测试验证
- [ ] 同步修改影响范围内的单元测试代码
- [ ] 运行影响范围内的单元测试
- [ ] 确保所有测试用例通过
- [ ] 新增功能必须有对应的测试用例
- [ ] 测试分支覆盖率达到要求（> 85%）

### 8.9 质量保证
- [ ] 功能完整性：是否满足所有需求，无遗漏
- [ ] 逻辑正确性：代码逻辑是否正确，无缺陷
- [ ] 性能优化：是否存在性能隐患（N+1 查询、大循环等）
- [ ] 安全检查：是否存在安全漏洞（SQL 注入、XSS、权限绕过等）

### 8.10 影响范围说明
- [ ] 明确说明本次代码变更影响的模块
- [ ] 说明是否影响现有接口（新增/修改/删除）
- [ ] 说明是否需要数据库变更
- [ ] 说明是否影响其他模块的调用

---

## 9. 常见场景示例

### 9.1 分页查询
```java
@GetMapping
public Result<Page<UserDTO>> listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<User> userPage = userRepository.findAll(pageable);
    return Result.success(userPage.map(UserConverter::toDTO));
}
```

### 9.2 条件查询
```java
public List<UserDTO> searchUsers(UserSearchRequest request) {
    QueryWrapper query = QueryWrapper.create();
    if (StringUtils.hasText(request.getName())) {
        query.like(UserDO.NAME, request.getName());
    }
    return list(query).stream().map(UserConverter::toDTO).toList();
}
```

### 9.3 批量操作
```java
@Transactional(rollbackFor = Exception.class)
public void batchUpdateStatus(List<Long> ids, Integer status) {
    log.info("批量更新: ids={}, status={}", ids, status);
    QueryWrapper query = QueryWrapper.create().in(UserDO.ID, ids);
    UserDO update = new UserDO();
    update.setStatus(status);
    update(update, query);
}
```

### 9.4 缓存使用
```java
@Cacheable(value = "users", key = "#id")
public UserDTO findById(Long id) {
    // ...
}
```
