# 租户管理业务流程文档

## 一、业务模块概述

### 1.1 模块位置
- 模块: onebase-module-system
- Controller: `TenantController.java` (onebase-module-system-build)
- Service: `TenantService.java` 和 `TenantServiceImpl.java` (onebase-module-system-core)
- 数据层: `TenantDataRepository.java`

### 1.2 核心功能
租户管理模块负责多租户系统的租户生命周期管理,包括创建、查询、更新租户信息。

## 二、接口服务分析

### 2.1 Controller层接口

#### 接口1: 根据租户名获取租户ID
- 路径: `GET /system/tenant/get-id-by-name`
- 权限: `@PermitAll` (无需登录)
- 场景: 登录界面根据用户输入的租户名获取租户编号
- 参数: `name` (租户名称)
- 返回: 租户ID
- 代码位置: `TenantController.java:42`

#### 接口2: 获取启用的租户列表
- 路径: `GET /system/tenant/simple-list`
- 权限: `@PermitAll` (无需登录)
- 场景: 首页选择租户下拉框
- 返回: 启用状态的租户简要信息列表
- 代码位置: `TenantController.java:51`

#### 接口3: 根据域名获取租户信息
- 路径: `GET /system/tenant/get-by-website`
- 权限: `@PermitAll` (无需登录)
- 场景: 登录界面根据域名自动识别租户
- 参数: `website` (域名)
- 返回: 租户简要信息
- 代码位置: `TenantController.java:62`

#### 接口4: 更新租户信息
- 路径: `POST /system/tenant/update`
- 权限: `@PreAuthorize("@ss.hasPermission('tenant:info:update')")`
- 场景: 租户信息修改
- 参数: `TenantUpdateReqVO` (更新请求对象)
- 返回: Boolean
- 代码位置: `TenantController.java:73`

#### 接口5: 获取租户详细信息
- 路径: `GET /system/tenant/get`
- 权限: `@PreAuthorize("@ss.hasPermission('tenant:info:query')")`
- 场景: 查看租户详细信息(包含应用数量)
- 参数: `id` (租户ID)
- 返回: 租户详细信息
- 代码位置: `TenantController.java:83`

#### 接口6: 获取租户简要信息(免登录)
- 路径: `GET /system/tenant/get-simple-tenant-by-id`
- 权限: `@PermitAll` (无需登录)
- 场景: 获取租户基本信息
- 参数: `id` (租户ID)
- 返回: 租户简要信息
- 代码位置: `TenantController.java:91`

## 三、核心业务流程

### 3.1 创建租户流程

#### 流程图
```
开始
  ↓
1. 校验租户名称是否重复
  ↓
2. 校验租户域名是否为空/重复
  ↓
3. 获取租户套餐(PackageTypeEnum.ALL)
  ↓
4. 设置过期时间(默认2099-02-19)
  ↓
5. 检查License限制
  ├─ 5.1 检查租户数量是否超限
  └─ 5.2 检查用户数量是否超限
  ↓
6. 创建租户数据对象并保存
  ↓
7. 在新租户上下文中执行
  ├─ 7.1 创建管理员角色
  ├─ 7.2 创建开发者角色
  ├─ 7.3 创建普通用户角色
  └─ 7.4 创建系统管理员用户并分配角色
  ↓
8. 记录操作日志
  ↓
返回租户ID
```

#### 详细步骤 (TenantServiceImpl.java:174)

**步骤1: 数据校验**
- 校验租户名称唯一性
- 校验域名唯一性和非空
- 代码: `validTenantNameDuplicate()`, `validTenantWebsiteDuplicate()`

**步骤2: 套餐和时间设置**
- 获取"全量"套餐 (PackageTypeEnum.ALL)
- 设置过期时间,默认为2099-02-19

**步骤3: License限制检查**
- 获取最新激活的License
- 检查租户数量: 现有租户数 < License租户限制
- 检查用户数量: (其他租户用户数 + 新租户分配数) ≤ License用户限制
- 代码位置: `TenantServiceImpl.java:194-214`

**步骤4: 创建租户记录**
- 设置发布模式(默认内部模式)
- 保存到数据库
- 代码: `tenantDataRepository.insert(tenant)`

**步骤5: 创建租户初始数据 (在租户上下文中)**
使用 `TenantUtils.execute(tenant.getId(), ...)` 切换到新租户上下文

5.1 创建管理员角色
- 方法: `createTenantAdminRole()`
- 返回角色ID

5.2 创建开发者角色
- 方法: `createDeveloperAdminRole()`

5.3 创建普通用户角色
- 方法: `createNormalUserRole()`

5.4 创建系统管理员用户
- 方法: `createSystemUser(roleId, createReqVO)`
- 处理流程:
  - 遍历管理员用户列表
  - 从平台用户中获取手机号和邮箱
  - 设置默认密码: "AdminChina2025!"
  - 创建用户并分配管理员角色
- 代码位置: `TenantServiceImpl.java:271-299`

**步骤6: 记录操作日志**
- 使用 `@LogRecord` 注解
- 记录租户创建操作

### 3.2 更新租户流程

#### 主要验证点
1. 校验租户是否存在
2. 校验租户名称唯一性(排除自身)
3. 校验域名唯一性(排除自身)
4. 检查License用户数量限制

### 3.3 查询租户流程

#### 按名称查询
- Service方法: `getTenantByName(String name)`
- 直接查询数据库返回

#### 按域名查询
- Service方法: `getTenantByWebsite(String website)`
- 查询并过滤禁用状态的租户

#### 按状态查询列表
- Service方法: `getTenantListByStatus(Integer status)`
- 返回指定状态的所有租户

## 四、关键数据结构

### 4.1 租户数据对象 (TenantDO)
- id: 租户ID
- name: 租户名称
- website: 租户域名
- status: 状态 (启用/禁用)
- expireTime: 过期时间
- packageId: 租户套餐ID
- accountCount: 账号数量限制
- publishModel: 发布模式

### 4.2 关键依赖服务
- UserService: 用户管理
- RoleService: 角色管理
- MenuService: 菜单管理
- PermissionService: 权限管理
- LicenseService: License管理
- TenantPackageService: 租户套餐管理

## 五、注意事项

### 5.1 租户上下文
- 使用 `@TenantIgnore` 注解忽略租户隔离
- 使用 `TenantUtils.execute()` 在指定租户上下文中执行操作
- 通过 `TenantContextHolder` 获取当前租户ID

### 5.2 权限控制
- 公开接口: 使用 `@PermitAll`
- 需要权限的接口: 使用 `@PreAuthorize`

### 5.3 默认密码
- 租户管理员默认密码: `AdminChina2025!`
- 代码位置: `TenantServiceImpl.java:81`

## 六、待优化点

1. 创建租户时的事务管理需要确保原子性
2. 默认密码应该通过配置文件管理
3. License检查逻辑可以抽取为独立的验证器
4. 需要添加单元测试覆盖核心业务流程
