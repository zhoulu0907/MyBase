---
description: 测试策略和执行指南
---

# 测试策略和执行指南

## 测试哲学
- **测试金字塔**: 单元测试 > 集成测试 > 端到端测试
- **尽早测试**: 开发阶段即编写测试 (TDD 或并在开发)
- **持续集成**: 代码提交必须通过自动化测试
- **测试即文档**: 测试用例应清晰描述业务行为

## 测试分类及规范

### 1. 单元测试 (Unit Tests)
- **范围**: 单个类或方法，不依赖外部资源 (DB, Redis, Network)
- **工具**: JUnit 5, Mockito
- **位置**: `src/test/java` 对应包路径
- **命名**: `*Test.java`
- **特点**: 执行快，隔离性强

### 2. 集成测试 (Integration Tests)
- **范围**: 多个组件协作，可能依赖 Spring 上下文或嵌入式 DB
- **工具**: Spring Boot Test, H2/TestContainers
- **注解**: `@SpringBootTest`
- **特点**: 验证组件间交互，执行较慢

**示例**:
```java
@SpringBootTest
@Transactional
class UserIntegrationTest {
    @Autowired
    private UserService userService;
    
    @Test
    void testCreateAndFindUser() {
        UserDTO created = userService.createUser(new CreateUserRequest("Bob"));
        UserDTO found = userService.findUserById(created.getId());
        assertEquals("Bob", found.getName());
    }
}
```

### 3. API 测试 (Controller Tests)
- **范围**: Controller 层接口验证
- **工具**: MockMvc
- **注解**: `@WebMvcTest` 或 `@SpringBootTest` + `AutoConfigureMockMvc`
- **特点**: 验证 HTTP 请求映射、参数校验、序列化

**示例**:
```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void testGetUser() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"));
    }
}
```

## 通用测试场景

### 数据库交互测试
- 使用 `@DataJpaTest` 进行 Repository 测试
- 使用 H2 内存数据库模拟环境
- 使用 `@Transactional` 自动回滚测试数据
- 验证 CRUD 操作和复杂查询

### 业务逻辑测试
- 覆盖正常路径 (Happy Path)
- 覆盖异常路径 (Error Path)
- 覆盖边界条件 (Boundary Cases)
- 验证事务回滚机制

### 异常处理测试
- 验证是否抛出预期的异常类型
-验证错误信息是否准确
- 验证全局异常处理器是否生效

## 测试最佳实践

### 1. 测试隔离
- 每个测试用例应独立运行，不依赖其他测试的执行顺序
- 自动清理测试数据 (`@Transactional` 或 `@BeforeEach`/`@AfterEach`)
- 避免修改共享的全局状态

### 2. Mock 策略
- 只 Mock 外部依赖，不 Mock 被测类本身
- Mock 网络请求、第三方服务、复杂计算
- 确保 Mock 行为符合实际契约

### 3. 断言规范
- 使用 AssertJ 或 JUnit 自带断言
- 断言信息应清晰明确
- 避免过度断言，关注核心结果

### 4. 测试数据
- 使用 Builder 模式或测试工厂创建对象
- 避免硬编码的魔术数字/字符串
- 尽量使用随机生成的有效数据

## 运行测试

### 命令行运行
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 运行特定测试方法
mvn test -Dtest=UserServiceTest#testFindUserById

# 生成覆盖率报告
mvn test jacoco:report
```

### IDE 运行
- 右键点击测试类或方法 -> Run
- 使用 Coverage 模式运行查看覆盖率
- 使用 Debug 模式调试失败的测试

## 常见测试问题排查

### 测试失败
1. **AssertionError**: 预期值与实际值不符 → 检查业务逻辑或断言
2. **ContextLoadException**: Spring 上下文加载失败 → 检查 Bean 配置
3. **NullPointerException**: 未初始化的 Mock 或对象 → 检查 Mock 设置

### 随机失败 (Flaky Tests)
- 检查是否依赖了不确定的顺序或时间
- 检查是否存在并发竞争
- 检查测试数据是否被污染

## 模块特定测试提示

### 插件模块
- 关注类加载器行为
- 测试插件生命周期 (加载/卸载)
- 验证隔离性

### 业务模块
- 关注领域逻辑正确性
- 验证数据库约束
- 测试模块间接口

### 框架模块
- 测试通用工具类的稳健性
- 验证扩展机制的灵活性
- 高并发场景下的线程安全
