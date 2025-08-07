你是一个资深的java专家，请在开发中遵循如下规则：

<<<<<<< HEAD
- 严格遵循 **SOLID、DRY、KISS、YAGNI** 原则
- 遵循 **OWASP 安全最佳实践**（如输入验证、SQL注入防护）
- 采用 **分层架构设计**，确保职责分离
- 代码变更需通过 **单元测试覆盖**（测试覆盖率 ≥ 80%）
- 保持代码风格一致性，遵循团队约定的编码规范

---
=======
## 一、本项目特别指定的规则（important! 重要！强制！请务必要遵守！）
1. 项目中凡是以onebase-module开头的的一级模块均为可独立部署的微服务模块，每个模块内部分为module-api和module-server两个子模块；
2. module-api用于放置跨模块、跨微服务调用的Api类（使用open feign定义和访问）和DTO类（用于模块间数据传输）。凡是跨module模块调用的（除去对公共模块如onebase-framework的调用之外）必须在这里定义相关Api和相关DTO；
3. module-server用于放置本模块的核心服务和实体类等。模块内部的不同类别/领域间服务通过Service层调用，避免跨领域直接调用其他领域的Mapper层，如不可以在UserService中调用RoleMapper；
4. 带@RestController注解的Controller类统一作为前端访问入口，要注意带上@PreAuthorize权限管控，注意这里不要写业务逻辑，保持轻量、清晰；
5. Controller和Service方法大于2个入参优先使用VO，后期增删参数方便 只改VO一处，否则方法都要改签名；
6. 带@FeignClient注解的Api类统一作为跨module调用的API入口，一般无需权限控制，给内部其他module提供服务；
7. 带@Service注解的类统一作为业务逻辑处理类，所有的业务逻辑处理在这里实现，数据服务统一调用DataRepository实现；
8. 避免核心模型的类放在内部作为static class，要独立出来；
9. VO类中需要移除lockversion（乐观锁）、tenantid（租户id）等系统使用且对用户无用的基础字段；
10. HTTP请求路径中统一不设置参数，不推荐@GetMapping("/{datasourceId}/tables") ，采用命名方式：Controller层路径采用"/模块/对象"方式，如@RequestMapping("/system/user")，方法层采用“操作”命名，如@PostMapping("/create")，增强可读性和可维护性。
11. HTTP请求方式建议：get、query、page、list、导入、导出等查询方法使用GET请求方式，create、insert、update、delete等相关方法用POST；未识别操作类型默认采用POST；
>>>>>>> b76fa7a0656b7538ae9d9bf6ff3b7c8b7c4e637e

## 二、技术栈规范

### 技术栈要求

- **框架**：Spring Boot 3.x + Java 17
- **依赖**：
  - 核心：Spring Web, Lombok, AnyLine(操作数据库)
  - 数据库：PostgreSQL
  - 其他：Swagger (SpringDoc), Spring Security (如需权限控制)
  - 日志：SLF4J + Logback
  - 测试：JUnit 5, Mockito

<<<<<<< HEAD
---
=======
## 三、Java编程规约（重要！important!）
### 性能优化
- **算法复杂度**：选择合适的算法和数据结构
- **缓存策略**：合理使用缓存减少重复计算
- **懒加载**：对于昂贵的操作使用懒加载
- **批量处理**：批量处理数据库操作和网络请求
>>>>>>> b76fa7a0656b7538ae9d9bf6ff3b7c8b7c4e637e

## 三、项目结构规则

- 项目中分多个模块，以onebase-module开头，如onebase-module-app，每个模块内部分为api和server两个子模块，如onebase-module-app-api和onebase-module-app-server。
- 子模块server用于放置本模块的核心服务和实体类等。
- 模块分包括：应用管理app、系统管理system、数据管理data等模块。
- 包结构遵循如下规范：
  - 控制器: com.cmsr.onebase.module.${moduleName}.controller.app
  - 视图对象: com.cmsr.onebase.module.${moduleName}.controller.app.vo  
  - 服务: com.cmsr.onebase.module.${moduleName}.service
  - 数据访问: com.cmsr.onebase.module.${moduleName}.dal.database
  - 实体类: com.cmsr.onebase.module.${moduleName}.dal.dataobject
  - 公共工具: com.cmsr.onebase.module.${moduleName}.util
  - 配置: com.cmsr.onebase.module.${moduleName}.config

<<<<<<< HEAD
---

## 四、应用逻辑设计规范
=======
- **类/接口注释**：所有类和接口必须使用标准JavaDoc注释，包含以下内容：
  ```
  /**
   * 类或接口的功能描述
   *
   * @author 作者(当前用户名如matianyu)
   * @date 创建日期(当前日期如2025-07-25)
   */
  ```
  - 必须包含类/接口的定义、主要用途或功能描述。
  - 建议注明作者、创建日期等元信息。

- **方法注释**：所有公共方法必须使用JavaDoc注释，包含以下内容：
  ```
  /**
   * 方法功能简要描述
   *
   * @param 参数名 参数说明
   * @return 返回值说明
   * @throws 异常类型 异常说明（如有）
   */
  ```
  - 必须描述方法的主要功能。
  - 对所有入参、出参进行详细说明。
  - 如有异常抛出，需注明异常类型及说明。
>>>>>>> b76fa7a0656b7538ae9d9bf6ff3b7c8b7c4e637e

### 1. 分层架构原则

| 层级            | 职责                              | 约束条件                                                          |
|----------------|----------------------------------|---------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口  | - 禁止直接操作数据库<br>- 必须通过 Service 层调用<br>- 负责参数校验和响应格式化          |
| **Service**    | 业务逻辑实现，事务管理，数据校验       | - 必须通过 Repository 访问数据库<br>- 返回 VO 而非实体类（除非必要）<br>- 处理业务异常     |
| **Repository** | 数据持久化操作，定义数据库查询逻辑     | - 必须继承 `DataRepository`<br>- 只包含简单的数据访问逻辑<br>- 避免复杂业务逻辑          |
| **Entity**     | 数据库表结构映射对象                 | - 必须继承 `TenantBaseDO`<br>- 仅用于数据库交互<br>- 禁止直接返回给前端（需通过 VO 转换） |
| **VO/DTO**     | 数据传输对象，前后端交互             | - 必须添加字段验证注解<br>- 必须添加 Swagger 文档注解<br>- 避免包含敏感信息             |

### 2. 异常处理规范

- 抛出异常使用ServiceExceptionUtil.exception方法。
- 使用ErrorCode举类定义异常码。
- 业务异常需包含明确的错误码和错误信息
- 避免捕获所有异常而不做具体处理
- 禁止在控制器层捕获异常（除非有特殊处理需求）

### 3. 日志记录规范

- 使用 SLF4J 接口记录日志，避免直接使用 Logback 等实现类
- 日志级别使用规范：
  - DEBUG: 开发调试信息
  - INFO: 业务流程关键节点
  - WARN: 需关注但不影响系统运行的异常
  - ERROR: 影响系统运行的错误
- 日志需包含必要上下文信息（如用户ID、请求ID等）
- 禁止在生产环境输出 DEBUG 级别的日志
- 敏感信息（如密码、token）需进行脱敏处理

---

## 五、核心代码规范

注意：下面的代码示例为Java语言，请根据实际情况进行修改。其中`Prefix`为模块的名称，注意替换为实际的模块名称，如App、System等。

### 1. 实体类（Entity）规范

```java
@Entity
@Table(name = "prefix_user") // 明确指定表名
@Data // Lombok 注解
public class UserDO extends TenantBaseDO {

    @Column(name = "user_name", nullable = false, length = 64, comment = "用户名")
    private String username;

    @Column(name = "email", nullable = false, length = 64, comment = "邮箱地址")
    private String email;
    
    @Column(name = "status", nullable = false, length = 1, comment = "状态：0-禁用，1-启用")
    private Integer status;
    
}
```

### 2. 数据访问层（Repository）规范

- Repository类每个方法都要满足JPA命名规范。

```java
import com.cmsr.onebase.framework.aynline.DataRepository;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Repository
public class PrefixUserRepository extends DataRepository {

  public PrefixUserRepository() { // 修正构造函数名称
    super(UserDO.class);
  }

  public List<UserDO> findByUserName(String userName) { // 修正参数类型
      ConfigStore configs = new DefaultConfigStore();
      configs.eq("user_name", userName);
      return findAll(UserDO.class, configs);
  }
  
  public List<UserDO> findByStatus(Integer status) {
      ConfigStore configs = new DefaultConfigStore();
      configs.eq("status", status);
      return findAll(UserDO.class, configs);
  }
  
}
```

### 3. 服务层（Service）规范

- 服务层要分接口类和实现类。
- 实现类要加上@Service注解。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PrefixUserServiceImpl implements PrefixUserService {
    private static final Logger logger = LoggerFactory.getLogger(PrefixUserServiceImpl.class);
    
    @Autowired
    private PrefixUserRepository userRepository;

    @Transactional
    public ApiResponse<UserDTO> createUser(UserDTO dto) {
        logger.info("创建用户: {}", dto.getUsername());
        
        // 业务逻辑实现
        UserDO user = new UserDO();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setStatus(1); // 默认启用
        
        UserDO savedUser = userRepository.save(user);
        logger.info("用户创建成功, ID: {}", savedUser.getId());
        
        return ApiResponse.success(UserDTO.fromEntity(savedUser));
    }
    
    @Override
    public ApiResponse<List<UserDTO>> getUsersByStatus(Integer status) {
        logger.info("查询状态为{}的用户", status);
        
        List<UserDO> userDOs = userRepository.findByStatus(status);
        List<UserDTO> userDTOs = userDOs.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
                
        return ApiResponse.success(userDTOs);
    }
}
```

### 4. 控制器（RestController）规范

- 每个接口都要加上swagger的注释，注释内容是接口的用途。

```java
@RestController
@RequestMapping("/system/users")
@Tag(name = "用户管理", description = "用户相关接口")
public class PrefixUserController {
    private static final Logger logger = LoggerFactory.getLogger(PrefixUserController.class);
    
    @Autowired
    private PrefixUserService userService;

    @PostMapping
    @Operation(summary = "创建用户")
    public CommonResult<CreateUserRespVO> createUser(@RequestBody @Valid CreateUserReqVO dto) {
        logger.info("收到创建用户请求: {}", dto.getUsername());
        CreateUserRespVO response = userService.createUser(dto);
        return CommonResult.success(response);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询用户")
    public CommonResult<List<CreateUserRespVO>> getUsersByStatus(@PathVariable Integer status) {
        logger.info("收到查询状态为{}的用户请求", status);
        List<CreateUserRespVO> response = userService.getUsersByStatus(status);
        return CommonResult.success(response);
    }
}
```

---

## 六、数据视图对象（VO）规范

- VO类中需要移除lockversion（乐观锁）、tenantid（租户id）等系统使用且对用户无用的基础字段。
- VO类中需要添加swagger注释，注释内容为该类及各个字段的用途。
- 添加必要的字段验证注解。

```java
//  @Data 注解 和 @Schema
@Data
@Schema(description = "应用管理 - 创建用户 Request VO")
public class CreateUserReqVO {

  @Schema(description = "用户名", required = true, example = "admin")
  @NotBlank(message = "用户名不能为空")
  @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间")
  private String username;

  @Schema(description = "邮箱地址", required = true, example = "admin@example.com")
  @Email(message = "邮箱格式不正确")
  @NotBlank(message = "邮箱不能为空")
  private String email;
  
  @Schema(description = "密码", required = true)
  @NotBlank(message = "密码不能为空")
  @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
  private String password;
  
}
```

---

## 七、测试规范

- 单元测试覆盖率需达到80%以上
- 核心业务逻辑必须编写单元测试
- 使用 JUnit 5 和 Mockito 进行测试
- 测试类命名规范：被测试类名 + Test
- 测试方法命名规范：test + 被测试方法名 + 测试场景

```java
@ExtendWith(MockitoExtension.class)
public class PrefixUserServiceImplTest {

    @Mock
    private PrefixUserRepository userRepository;

    @InjectMocks
    private PrefixUserServiceImpl userService;

    @Test
    public void testCreateUser() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setEmail("test@example.com");
        
        UserDO savedUser = new UserDO();
        savedUser.setId(1L);
        savedUser.setUsername("test");
        savedUser.setEmail("test@example.com");
        
        // 模拟Repository方法调用
        when(userRepository.save(any(UserDO.class))).thenReturn(savedUser);
        
        // 执行测试
        ApiResponse<UserDTO> response = userService.createUser(userDTO);
        
        // 验证结果
        assertTrue(response.isSuccess());
        assertEquals(1L, response.getData().getId());
        assertEquals("test", response.getData().getUsername());
    }
}
```

---

## 八、性能优化规范

- 避免在循环中进行数据库操作
- 合理使用索引提升查询性能
- 大结果集查询需使用分页
- 避免不必要的对象创建
- 对频繁调用的方法进行性能优化