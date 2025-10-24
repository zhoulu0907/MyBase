你是一个资深的java专家，请在开发中遵循如下规则：

- 严格遵循 **SOLID、DRY、KISS、YAGNI** 原则
- 遵循 **OWASP 安全最佳实践**（如输入验证、SQL注入防护）
- 采用 **分层架构设计**，确保职责分离
- 代码变更需通过 **单元测试覆盖**（测试覆盖率 ≥ 80%）
- 保持代码风格一致性，遵循团队约定的编码规范

---

## 二、技术栈规范

### 技术栈要求

- **框架**：Spring Boot 3.x + Java 17
- **依赖**：
  - 核心：Spring Web, Lombok, AnyLine(操作数据库)
  - 数据库：PostgreSQL
  - 其他：Swagger (SpringDoc), Spring Security (如需权限控制)
  - 日志：SLF4J + Logback
  - 测试：JUnit 5, Mockito

---

## 三、项目结构规则

- 项目中分多个模块，以onebase-module开头，如onebase-module-app，每个模块内部分为api、core、build、runtime四个子模块，如onebase-module-app-api和onebase-module-app-core等。
- 子模块core用于放置本模块的核心服务和实体类等,包结构遵循如下规范：
  - 数据访问: com.cmsr.onebase.module.${moduleName}.core.dal.database.${subPackageName}
  - 实体类: com.cmsr.onebase.module.${moduleName}.core.dal.dataobject.${subPackageName}
  - 公共工具: com.cmsr.onebase.module.${moduleName}.core.util
  - 配置: com.cmsr.onebase.module.${moduleName}.config
- 子模块build用于放置本模块的构建接口服务: 
  - 控制器: com.cmsr.onebase.module.${moduleName}.build.controller.${subPackageName}
  - 视图对象: com.cmsr.onebase.module.${moduleName}.build.vo.${subPackageName}
  - 服务: com.cmsr.onebase.module.${moduleName}.build.service.${subPackageName}
- 子模块runtime用于放置本模块的允许接口服务:
  - 控制器: com.cmsr.onebase.module.${moduleName}.runtime.controller.${subPackageName}
  - 视图对象: com.cmsr.onebase.module.${moduleName}.runtime.vo.${subPackageName}
  - 服务: com.cmsr.onebase.module.${moduleName}.runtime.service.${subPackageName}
- 子模块api用于放置本模块的对其他模块提供的接口服务:
  - 控制器: com.cmsr.onebase.module.${moduleName}.api.${subPackageName}

---

## 四、应用逻辑设计规范

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
import com.cmsr.onebase.framework.aynline.DataDDLRepository;
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
- 更新、删除接口，都是用Post方法。
- Post方法接口参数超过2个及以上，使用@RequestBody注解，参数全部放到Body里面。

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