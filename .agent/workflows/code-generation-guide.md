---
description: 代码生成规范和模板
---

# 代码生成指南

本指南专为 AI 代码生成设计，提供具体的代码模板、生成规范和验证清单。

## 代码生成规范

### 导入规范
- **强制要求**: 避免使用全限定类名，必须先导入后使用
  - ✅ 正确: `import com.cmsr.onebase.User; ... User user = new User();`
  - ❌ 错误: `com.cmsr.onebase.User user = new com.cmsr.onebase.User();`
- **导入顺序**: 
  1. Java 标准库 (`java.*`, `javax.*`)
  2. 第三方库 (`org.*`, `com.*`)
  3. 项目内部包 (`com.cmsr.onebase.*`)
- **避免通配符**: 不使用 `import com.cmsr.onebase.*`

### 文档和注释规范

使用标准的 JavaDoc 注释规范，确保代码的可读性和可维护性。

#### 类/接口注释
所有类和接口必须使用标准 JavaDoc 注释：
```java
/**
 * 用户服务，处理用户相关业务逻辑
 *
 * @author matianyu
 * @date 2025-12-27
 */
public class UserService {
```

**要求**:
- 必须包含类/接口的定义、主要用途或功能描述
- 必须注明作者（`@author`）和创建日期（`@date`）
- 作者使用当前主机用户名
- 日期使用当前日期（格式：yyyy-MM-dd）

#### 方法注释
所有公共方法必须使用 JavaDoc 注释：
```java
/**
 * 根据 ID 查询用户
 *
 * @param userId 用户 ID
 * @return 用户信息，不存在时抛出异常
 * @throws BusinessException 用户不存在时抛出
 */
public UserDTO findById(Long userId) {
```

**要求**:
- 必须描述方法的主要功能
- 对所有入参使用 `@param` 进行详细说明
- 对返回值使用 `@return` 进行详细说明
- 如有异常抛出，使用 `@throws` 注明异常类型及说明

#### 字段注释
重要字段应添加注释，说明用途和含义：
```java
/** 用户数据访问接口 */
private final UserRepository userRepository;

/** 缓存管理器 */
private final CacheManager cacheManager;
```

#### 代码注释
为复杂逻辑、特殊处理等添加行内注释：
```java
// 业务校验：检查用户名是否已存在
if (userRepository.existsByUsername(request.getUsername())) {
    throw new BusinessException("用户名已存在");
}

// 创建实体并设置默认值
User user = new User();
user.setStatus(UserStatus.ACTIVE); // 默认状态为激活
```

#### TODO 标记
使用 TODO 标记待完成或待优化的工作：
```java
// TODO: 优化批量查询性能
List<User> users = userRepository.findAllById(userIds);

// TODO: 添加缓存支持
public UserDTO findById(Long userId) {
```


### 异常处理规范

#### 导入异常工具类
在类文件顶部使用静态导入：
```java
import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
```

#### 定义错误码
在模块的 `ErrorCodeConstants` 接口中定义错误码：
```java
public interface ErrorCodeConstants {
    // ========== 用户管理 1-002-001-000 ==========
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_002_001_000, "用户不存在");
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1_002_001_001, "用户名已存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1_002_001_002, "邮箱已被使用");
    
    // 支持占位符的错误码
    ErrorCode USER_STATUS_INVALID = new ErrorCode(1_002_001_003, "用户状态[{}]无效");
}
```

**错误码规范**:
- 使用下划线分隔的大写命名
- 错误码采用分段式：`模块_功能_错误类型`
- 数字编码：`1_模块编号_功能编号_错误序号`
- 支持使用 `{}` 作为占位符

#### 抛出异常
使用 `exception()` 方法抛出异常：

**基本用法**:
```java
// 无参数
if (user == null) {
    throw exception(ErrorCodeConstants.USER_NOT_EXISTS);
}

// 带参数（填充占位符）
if (invalidStatus) {
    throw exception(ErrorCodeConstants.USER_STATUS_INVALID, status);
}

// 多个参数
throw exception(ErrorCodeConstants.USER_OPERATION_FAILED, userId, operation);
```

**实际示例**:
```java
/**
 * 根据 ID 查询用户
 *
 * @param userId 用户 ID
 * @return 用户信息
 * @throws ServiceException 用户不存在时抛出
 */
public UserDTO findById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> exception(ErrorCodeConstants.USER_NOT_EXISTS));
    return UserConverter.toDTO(user);
}

/**
 * 创建用户
 *
 * @param request 创建用户请求
 * @return 创建的用户信息
 * @throws ServiceException 用户名已存在时抛出
 */
@Transactional(rollbackFor = Exception.class)
public UserDTO createUser(CreateUserRequest request) {
    // 业务校验：检查用户名是否已存在
    if (userRepository.existsByUsername(request.getUsername())) {
        throw exception(ErrorCodeConstants.USER_USERNAME_EXISTS);
    }
    
    // 业务逻辑...
}
```

#### 异常捕获和处理
**不吞异常**: 必须记录日志或向上抛出
```java
// ✅ 正确：记录日志并抛出
try {
    // 业务逻辑
} catch (Exception e) {
    log.error("操作失败: userId={}", userId, e);
    throw exception(ErrorCodeConstants.USER_OPERATION_FAILED, userId);
}

// ❌ 错误：空 catch 块
try {
    // 业务逻辑
} catch (Exception e) {
    // 空 catch 块，异常被吞掉
}
```

**异常转换**:
```java
try {
    // 调用外部服务
    externalService.call();
} catch (ExternalException e) {
    log.error("外部服务调用失败", e);
    throw exception(ErrorCodeConstants.EXTERNAL_SERVICE_ERROR, e.getMessage());
}
```

### 日志记录规范
- **关键节点记录**: 方法入口、关键分支、异常
  ```java
  public UserDTO createUser(CreateUserRequest request) {
      log.info("创建用户: {}", request.getUsername());
      
      // 业务逻辑
      
      log.info("用户创建成功: userId={}", user.getId());
      return userDTO;
  }
  ```

- **日志级别使用**:
  - `DEBUG`: 详细的调试信息（仅开发环境）
  - `INFO`: 重要的业务流程节点
  - `WARN`: 潜在问题，但不影响运行
  - `ERROR`: 错误异常，需要关注

- **敏感信息脱敏**: 不记录密码、token 等
  ```java
  log.info("用户登录: username={}", username); // ✅
  log.info("用户登录: password={}", password); // ❌
  ```

## 代码模板

### Controller 层模板

```java
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户管理控制器
 *
 * @author matianyu
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
     *
     * @param request 创建用户请求
     * @return 创建的用户信息
     */
    @PostMapping
    public Result<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("创建用户请求: {}", request.getUsername());
        UserDTO user = userService.createUser(request);
        return Result.success(user);
    }

    /**
     * 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return Result.success(user);
    }

    /**
     * 更新用户
     *
     * @param id 用户 ID
     * @param request 更新用户请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, 
                                    @Valid @RequestBody UpdateUserRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
```

**关键点**:
- 使用 `@Resource` 注入依赖（不建议使用 `@Autowired`）
- 使用 `@Valid` 进行参数校验
- 统一返回 `Result<T>` 格式
- 记录关键操作日志

### Service 层模板

```java
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 用户服务实现
 *
 * @author matianyu
 * @date 2025-12-27
 */
@Slf4j
@Service
public class UserService {

    /** 用户数据访问接口 */
    @Resource
    private UserRepository userRepository;

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 创建的用户信息
     * @throws ServiceException 用户名已存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDTO createUser(CreateUserRequest request) {
        log.info("创建用户: {}", request.getUsername());
        
        // 业务校验：检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw exception(ErrorCodeConstants.USER_USERNAME_EXISTS);
        }
        
        // 创建实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // 保存
        user = userRepository.save(user);
        
        log.info("用户创建成功: userId={}", user.getId());
        return UserConverter.toDTO(user);
    }

    /**
     * 根据 ID 查询用户
     *
     * @param userId 用户 ID
     * @return 用户信息
     * @throws ServiceException 用户不存在时抛出
     */
    public UserDTO findById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> exception(ErrorCodeConstants.USER_NOT_EXISTS));
        return UserConverter.toDTO(user);
    }

    /**
     * 更新用户
     *
     * @param userId 用户 ID
     * @param request 更新用户请求
     * @throws ServiceException 用户不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> exception(ErrorCodeConstants.USER_NOT_EXISTS));
        
        user.setEmail(request.getEmail());
        userRepository.save(user);
        
        log.info("用户更新成功: userId={}", userId);
    }

    /**
     * 删除用户
     *
     * @param userId 用户 ID
     * @throws ServiceException 用户不存在时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw exception(ErrorCodeConstants.USER_NOT_EXISTS);
        }
        
        userRepository.deleteById(userId);
        log.info("用户删除成功: userId={}", userId);
    }
}
```

**关键点**:
- 静态导入 `ServiceExceptionUtil.exception`
- 使用 `@Resource` 注入依赖（不建议使用 `@Autowired`）
- 写操作使用 `@Transactional(rollbackFor = Exception.class)`
- 使用 `exception(ErrorCodeConstants.XXX)` 抛出业务异常
- 记录关键操作日志
- 使用 Converter 转换实体和 DTO

### Repository 层模板

```java
import com.cmsr.onebase.module.user.dal.dataflexdo.UserDO;
import com.cmsr.onebase.module.user.dal.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问层
 *
 * @author matianyu
 * @date 2025-12-27
 */
@Repository
public class UserDataRepository extends ServiceImpl<UserMapper, UserDO> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息，不存在返回 null
     */
    public UserDO findByUsername(String username) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(UserDO.USERNAME, username);
        return getOne(queryWrapper);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    public boolean existsByUsername(String username) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(UserDO.USERNAME, username);
        return count(queryWrapper) > 0;
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱地址
     * @return 用户信息，不存在返回 null
     */
    public UserDO findByEmail(String email) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(UserDO.EMAIL, email);
        return getOne(queryWrapper);
    }

    /**
     * 查询所有激活用户，按创建时间降序
     *
     * @return 用户列表
     */
    public List<UserDO> findAllActive() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(UserDO.STATUS, UserStatus.ACTIVE);
        queryWrapper.orderBy(UserDO.CREATE_TIME, false);
        return list(queryWrapper);
    }
}
```

**关键点**:
- 继承 `ServiceImpl<Mapper, DO>`（MyBatis-Flex）
- 使用 `QueryWrapper` 构建查询条件
- 方法命名清晰描述查询意图
- 添加完整的 JavaDoc 注释

## 验证清单

生成代码后，AI 应自动检查以下项：

### 基础规范
- [ ] 所有类都有导入语句，无全限定类名
- [ ] 所有类/接口都有 JavaDoc 注释（包含 `@author` 和 `@date`）
- [ ] 所有公共方法都有 JavaDoc 注释（包含 `@param`、`@return`、`@throws`）
- [ ] 重要字段都有注释说明
- [ ] 复杂逻辑有行内注释
- [ ] 待完成工作有 TODO 标记

### 异常处理
- [ ] 静态导入 `ServiceExceptionUtil.exception`
- [ ] 在 `ErrorCodeConstants` 中定义错误码
- [ ] 使用 `exception(ErrorCodeConstants.XXX)` 抛出异常
- [ ] 所有 catch 块都记录日志或抛出异常
- [ ] 异常信息使用错误码，支持占位符

### 日志记录
- [ ] 关键操作有日志记录（INFO 级别）
- [ ] 异常有日志记录（ERROR 级别）
- [ ] 敏感信息已脱敏

### 事务管理
- [ ] 写操作使用 `@Transactional(rollbackFor = Exception.class)`
- [ ] 事务边界在 Service 层

### 响应格式
- [ ] Controller 返回统一的 `Result<T>` 格式
- [ ] 使用 `@Valid` 进行参数校验

### 依赖注入
- [ ] 统一使用 `@Resource` 注入依赖
- [ ] 避免使用 `@Autowired`

### 编译验证
- [ ] 代码可以成功编译，无语法错误
- [ ] 所有导入的类和包都存在
- [ ] 方法签名和类型匹配正确

### 测试验证
- [ ] 同步修改影响范围内的单元测试代码
- [ ] 运行影响范围内的单元测试
- [ ] 确保所有测试用例通过
- [ ] 新增功能必须有对应的测试用例
- [ ] 测试分支覆盖率达到要求（> 85%）

### 影响范围说明
- [ ] 明确说明本次代码变更影响的模块
- [ ] 说明是否影响现有接口（新增/修改/删除）
- [ ] 说明是否需要数据库变更
- [ ] 说明是否影响其他模块的调用

## 常见场景示例

### 场景 1: 分页查询

```java
@GetMapping
public Result<Page<UserDTO>> listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<User> userPage = userRepository.findAll(pageable);
    Page<UserDTO> dtoPage = userPage.map(UserConverter::toDTO);
    
    return Result.success(dtoPage);
}
```

### 场景 2: 条件查询

```java
public List<UserDTO> searchUsers(UserSearchRequest request) {
    // 使用 Specification 或 QueryDSL
    Specification<User> spec = (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        
        if (StringUtils.hasText(request.getUsername())) {
            predicates.add(cb.like(root.get("username"), "%" + request.getUsername() + "%"));
        }
        
        if (request.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), request.getStatus()));
        }
        
        return cb.and(predicates.toArray(new Predicate[0]));
    };
    
    List<User> users = userRepository.findAll(spec);
    return users.stream().map(UserConverter::toDTO).collect(Collectors.toList());
}
```

### 场景 3: 批量操作

```java
@Transactional(rollbackFor = Exception.class)
public void batchUpdateStatus(List<Long> userIds, UserStatus status) {
    log.info("批量更新用户状态: count={}, status={}", userIds.size(), status);
    
    List<User> users = userRepository.findAllById(userIds);
    users.forEach(user -> user.setStatus(status));
    userRepository.saveAll(users);
    
    log.info("批量更新完成");
}
```

### 场景 4: 缓存使用

```java
@Cacheable(value = "users", key = "#userId")
public UserDTO findById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException("用户不存在"));
    return UserConverter.toDTO(user);
}

@CacheEvict(value = "users", key = "#userId")
public void updateUser(Long userId, UpdateUserRequest request) {
    // 更新逻辑
}
```
