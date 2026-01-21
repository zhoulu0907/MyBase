# OneBase v3 开发指南

## 快速开始

### 环境要求
- **JDK**: 17+
- **Maven**: 3.6+
- **数据库**: PostgreSQL 12+ (推荐) / MySQL 8+ / Oracle
- **Redis**: 6.0+
- **IDE**: IntelliJ IDEA (推荐)

### 项目初始化
```bash
# 1. 克隆项目
git clone http://git.virtueit.net/s25029301301/onebase-v3-be.git
cd onebase-v3-be

# 2. 安装依赖
mvn clean install -Dmaven.test.skip=true

# 3. 导入数据库
psql -U postgres -d onebase -f sql/init.sql

# 4. 配置文件
cp onebase-server/src/main/resources/application-dev.yaml.example \
   onebase-server/src/main/resources/application-dev.yaml
# 修改数据库连接信息

# 5. 启动项目
cd onebase-server
mvn spring-boot:run
```

## 开发规范

### 1. 代码结构规范

#### 模块分层
每个业务模块必须遵循以下分层结构：
```
module-{name}/
├── module-{name}-api/          # 对外API定义
│   └── src/main/java/
│       └── com/cmsr/onebase/module/{name}/api/
│           ├── dto/            # 数据传输对象
│           └── {Module}Api.java  # API接口
├── module-{name}-build/        # 构建态(管理端)
│   └── src/main/java/
│       └── com/cmsr/onebase/module/{name}/build/
│           ├── controller/     # REST控制器
│           ├── service/        # 业务服务
│           └── vo/             # 视图对象
├── module-{name}-runtime/      # 运行态(用户端)
│   └── src/main/java/
│       └── com/cmsr/onebase/module/{name}/runtime/
│           ├── controller/
│           ├── service/
│           └── vo/
└── module-{name}-core/         # 核心逻辑
    └── src/main/java/
        └── com/cmsr/onebase/module/{name}/core/
            ├── dal/            # 数据访问层
            │   ├── dataobject/ # DO对象
            │   ├── mapper/     # MyBatis Mapper
            │   └── database/   # Repository
            ├── service/        # 核心服务接口
            │   └── impl/       # 服务实现
            ├── convert/        # 对象转换器
            ├── enums/          # 枚举
            └── util/           # 工具类
```

#### 包命名约定
```
com.cmsr.onebase.module.{module-name}
├── api/                        # 跨模块API
├── build/                      # 构建态
├── runtime/                    # 运行态
└── core/                       # 核心逻辑
    ├── dal/
    │   ├── dataobject/         # 数据对象，对应数据库表
    │   ├── mapper/             # MyBatis接口
    │   └── database/           # 数据库访问Repository
    ├── service/                # 业务服务
    │   ├── impl/
    │   └── {Service}.java
    ├── convert/                # MapStruct转换器
    ├── enums/                  # 枚举定义
    └── util/                   # 工具类
```

### 2. 数据库规范

#### 表命名
- 统一使用模块名前缀：`{module}_{table_name}`
- 使用下划线命名法，如：`system_user`, `app_application`
- 索引命名：`idx_{table_name}_{column_name}`
- 唯一索引：`uk_{table_name}_{column_name}`

#### 实体类设计
```java
@Data
@TableName("system_user")
public class SystemUserDO {
    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 租户ID (多租户字段)
     */
    @Column(name = "tenant_id")
    private Long tenantId;
}
```

#### Repository 接口
```java
public interface SystemUserRepository extends
    BaseDataRepository<SystemUserDO, Long> {

    /**
     * 根据用户名查询用户
     */
    default SystemUserDO selectByUsername(String username) {
        return selectOne("username", username);
    }

    /**
     * 查询用户列表
     */
    default List<SystemUserDO> selectListByDeptId(Long deptId) {
        return selectListBy("dept_id", deptId);
    }
}
```

### 3. API 设计规范

#### REST 接口设计
```java
@RestController
@RequestMapping("/admin-api/system/user")
public class SystemUserController {

    @PostMapping("/create")
    public CommonResult<Long> createUser(@Valid @RequestBody SystemUserCreateReqVO createReqVO) {
        return success(systemUserService.createUser(createReqVO));
    }

    @PutMapping("/update")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody SystemUserUpdateReqVO updateReqVO) {
        systemUserService.updateUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteUser(@PathVariable("id") Long id) {
        systemUserService.deleteUser(id);
        return success(true);
    }

    @GetMapping("/get/{id}")
    public CommonResult<SystemUserRespVO> getUser(@PathVariable("id") Long id) {
        return success(systemUserService.getUser(id));
    }

    @GetMapping("/page")
    public CommonResult<PageResult<SystemUserRespVO>> getUserPage(
            @Valid SystemUserPageReqVO pageReqVO) {
        return success(systemUserService.getUserPage(pageReqVO));
    }
}
```

#### VO 设计规范
- **ReqVO (请求)**: 用于接收请求参数
- **RespVO (响应)**: 用于返回数据
- **PageReqVO**: 分页查询请求
- **CreateReqVO**: 创建请求
- **UpdateReqVO**: 更新请求

### 4. 服务层规范

#### 服务接口定义
```java
public interface SystemUserService {

    /**
     * 创建用户
     */
    Long createUser(SystemUserCreateReqVO createReqVO);

    /**
     * 更新用户
     */
    void updateUser(SystemUserUpdateReqVO updateReqVO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 获取用户
     */
    SystemUserRespVO getUser(Long id);

    /**
     * 获取用户分页
     */
    PageResult<SystemUserRespVO> getUserPage(SystemUserPageReqVO pageReqVO);
}
```

#### 服务实现规范
```java
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl implements SystemUserService {

    private final SystemUserRepository userRepository;
    private final SystemUserConvert userConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(SystemUserCreateReqVO createReqVO) {
        // 1. 参数校验
        validateUserForCreateOrUpdate(null, createReqVO.getUsername(),
            createReqVO.getEmail(), createReqVO.getMobile());

        // 2. 插入数据库
        SystemUserDO user = userConvert.convert(createReqVO);
        user.setId(IdUtils.generateId());
        userRepository.insert(user);

        // 3. 返回ID
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SystemUserUpdateReqVO updateReqVO) {
        // 1. 校验存在
        validateUserExists(updateReqVO.getId());

        // 2. 参数校验
        validateUserForCreateOrUpdate(updateReqVO.getId(),
            updateReqVO.getUsername(), updateReqVO.getEmail(),
            updateReqVO.getMobile());

        // 3. 更新数据库
        SystemUserDO user = userConvert.convert(updateReqVO);
        userRepository.updateById(user);
    }
}
```

### 5. 异常处理规范

#### 定义业务异常
```java
public interface SystemErrorCodeConstants {
    CommonResultCode USER_USERNAME_EXISTS = new CommonResultCode(
        100100100, "用户名已存在", "用户名已存在，请使用其他名称");

    CommonResultCode USER_NOT_EXISTS = new CommonResultCode(
        100100101, "用户不存在", "用户不存在");

    CommonResultCode USER_PASSWORD_ERROR = new CommonResultCode(
        100100102, "密码错误", "用户密码错误");
}
```

#### 使用异常
```java
if (userRepository.selectByUsername(username) != null) {
    throw exception(USER_USERNAME_EXISTS);
}

SystemUserDO user = userRepository.selectById(id);
if (user == null) {
    throw exception(USER_NOT_EXISTS);
}
```

### 6. 多租户开发规范

#### 租户隔离
```java
@Service
@RequiredArgsConstructor
public class AppApplicationServiceImpl implements AppApplicationService {

    // 自动过滤当前租户数据
    @Override
    public PageResult<ApplicationRespVO> getApplicationPage(
            ApplicationPageReqVO pageReqVO) {
        // Repository层会自动添加租户ID过滤
        return applicationRepository.selectPage(pageReqVO);
    }

    // 创建时自动注入租户ID
    @Override
    @TenantIgnore  // 某些情况下忽略租户
    public List<ApplicationDO> getAllApplications() {
        return applicationRepository.selectList();
    }
}
```

#### 租户上下文
```java
// 获取当前租户ID
Long tenantId = TenantContextHolder.getTenantId();

// 忽略租户隔离
@TenantIgnore
public List<SystemUserDO> selectAllUsers() {
    return userRepository.selectList();
}
```

### 7. 缓存使用规范

#### 使用 JetCache
```java
@Service
public class SystemUserServiceImpl implements SystemUserService {

    @Cached(name = "user:id:", key = "#id", expire = 3600)
    public SystemUserRespVO getUser(Long id) {
        return userConvert.convert(
            userRepository.selectById(id));
    }

    @CacheInvalidate(name = "user:id:", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

### 8. 测试规范

#### 单元测试
```java
@ExtendWith(MockitoExtension.class)
class SystemUserServiceTest {

    @Mock
    private SystemUserRepository userRepository;

    @InjectMocks
    private SystemUserServiceImpl userService;

    @Test
    void testCreateUser() {
        // given
        SystemUserCreateReqVO createReqVO = new SystemUserCreateReqVO();
        createReqVO.setUsername("testuser");

        // when
        Long userId = userService.createUser(createReqVO);

        // then
        assertNotNull(userId);
    }
}
```

### 9. 代码审查检查清单

提交代码前确保：
- [ ] 通过所有单元测试
- [ ] 代码符合阿里巴巴 Java 开发规范
- [ ] 添加必要的注释和文档
- [ ] 没有调试代码和 TODO
- [ ] SQL 语句经过性能优化
- [ ] 异常处理完善
- [ ] 日志级别正确
- [ ] 多租户隔离正确
- [ ] 敏感数据已脱敏

## 常见问题

### Q1: 如何调试多租户问题？
A: 在 SQL 日志中查看 tenant_id 是否正确注入：
```yaml
logging:
  level:
    com.cmsr.onebase.framework.mybatis: DEBUG
```

### Q2: 如何处理跨模块调用？
A: 使用 SPI 机制或直接注入 API 接口：
```java
@Service
@RequiredArgsConstructor
public class MyServiceImpl {

    private final SystemUserApi systemUserApi;

    public void doSomething() {
        SystemUserDTO user = systemUserApi.getUser(userId);
    }
}
```

### Q3: 如何添加新的数据库支持？
A: 在 `onebase-dependencies/pom.xml` 添加驱动依赖，并在对应模块配置数据源。

## 最佳实践

1. **优先使用框架提供的能力**，避免重复造轮子
2. **善用 AI 辅助工具**，但要人工审查代码质量
3. **保持代码简洁**，避免过度设计
4. **及时提交代码**，小步快跑
5. **编写有意义的测试**，覆盖核心业务逻辑
6. **注意性能优化**，避免 N+1 查询
7. **做好日志记录**，便于问题排查
8. **关注安全性**，防止 SQL 注入、XSS 等攻击

## 参考资源
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Flex 文档](https://mybatis-flex.com/)
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)
- [项目架构设计](./architecture-plugin-system.md)
