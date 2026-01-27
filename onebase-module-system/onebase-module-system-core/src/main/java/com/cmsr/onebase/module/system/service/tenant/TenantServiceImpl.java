package com.cmsr.onebase.module.system.service.tenant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.UserTypeEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.date.DateUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.config.TenantProperties;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.AppServiceApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUserService;
import com.cmsr.onebase.module.screen.api.DashboardProjectApi;
import com.cmsr.onebase.module.system.api.user.AdminUserRoleApi;
import com.cmsr.onebase.module.system.convert.tenant.TenantConvert;
import com.cmsr.onebase.module.system.dal.database.*;
import com.cmsr.onebase.module.system.dal.database.dept.DeptDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.corp.CorpDO;
import com.cmsr.onebase.module.system.dal.dataobject.dept.DeptDO;
import com.cmsr.onebase.module.system.dal.dataobject.dict.DictTypeDO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.flex.repo.UserDataRepository;
import com.cmsr.onebase.module.system.enums.corp.CorpConstant;
import com.cmsr.onebase.module.system.enums.permission.AdminTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.PackageTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantStatusEnum;
import com.cmsr.onebase.module.system.enums.user.UserStatusEnum;
import com.cmsr.onebase.module.system.framework.security.core.PwdEnHelper;
import com.cmsr.onebase.module.system.service.config.SystemConfigService;
import com.cmsr.onebase.module.system.service.corp.CorpService;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.dict.DictDataService;
import com.cmsr.onebase.module.system.service.dict.DictTypeService;
import com.cmsr.onebase.module.system.service.license.LicenseService;
import com.cmsr.onebase.module.system.service.permission.MenuService;
import com.cmsr.onebase.module.system.service.permission.PermissionService;
import com.cmsr.onebase.module.system.service.permission.RoleService;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantInfoHandler;
import com.cmsr.onebase.module.system.service.tenant.handler.TenantMenuHandler;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.role.RoleInsertReqVO;
import com.cmsr.onebase.module.system.vo.tenant.*;
import com.cmsr.onebase.module.system.vo.user.UserInsertReqVO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;
import static java.util.Collections.singleton;

/**
 * 空间 Service 实现类
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    // 空间管理员设置默认密码
    private static final String TENANT_ADMIN_PASSWORD = "AdminChina2025!";
    public static final String TENANT = "tenant";

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Resource // 由于 onebase.tenant.enable 配置项，可以关闭多租户的功能，所以这里只能不强制注入
    private TenantProperties tenantProperties;

    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private UserService          userService;

    @Resource
    private RoleService       roleService;
    @Resource
    private MenuService       menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private LicenseService    licenseService;
    @Resource
    private AppApplicationApi appApplicationApi;

    @Resource
    private TenantDataRepository tenantDataRepository;

    @Resource
    private CorpService corpService;

    @Resource
    private AdminUserRoleApi adminUserRoleApi;

    @Resource
    private DeptService deptService;

    @Resource
    private PwdEnHelper pwdEnHelper;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeService dictTypeService;

    @Resource
    private SecurityConfigApi securityConfigApi;

    @Resource
    private AppServiceApi appServiceApi;

    @Resource
    private DashboardProjectApi dashboardProjectApi;

    @Override
    public List<Long> getTenantIdList() {
        List<TenantDO> tenants = tenantDataRepository.findAll();
        return CollectionUtils.convertList(tenants, TenantDO::getId);
    }

    @Override
    public void validTenant(Long id) {
        TenantDO tenant = getTenant(id);
        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        if (tenant.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_DISABLE, tenant.getName());
        }
        if (DateUtils.isExpired(tenant.getExpireTime())) {
            throw exception(TENANT_EXPIRE, tenant.getName());
        }
    }

    @Override
    public Long getAvailableAccountCount() {
        LicenseDO license = licenseService.getLatestActiveLicense();
        Integer userCount = userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
        if (license != null) {
            // 获取license总人数限制
            Integer licenseUserLimit = license.getUserLimit();
            // 如果license总人数限制小于已分配人员数量，则返回0
            if (userCount > licenseUserLimit) {
                return 0L;
            }
            // 返回剩余可分配人员数量
            return (long) (licenseUserLimit - userCount);
        }
        return 0L;
    }

    @Override
    public Long getOtherTenantUserLimitCount(Long tenantId) {

        List<TenantDO> tenantDOList = tenantDataRepository.findAllByStatusExcludePlatform(
                TenantStatusEnum.NORMAL.getStatus(), tenantId);
        long sum = tenantDOList.stream()
                .filter(tenantDO -> tenantDO.getAccountCount() != null)
                .mapToLong(TenantDO::getAccountCount)
                .sum();
        return sum;
    }

    @Override
    public Long getTenantExistUserCount(Long tenantId) {
        // 查询当前租户下已分配的用户数量
        return TenantUtils.execute(tenantId, () -> {
            return Long.valueOf(userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus()));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_TENANT_TYPE, subType = SYSTEM_TENANT_CREATE_SUB_TYPE, bizNo = "{{#tenant.id}}",
            success = SYSTEM_TENANT_CREATE_SUCCESS)
    public Long createTenant(TenantInsertReqVO createReqVO) {
        // 1.1 校验租户名称是否重复
        validTenantNameDuplicate(createReqVO.getName(), null);
        // 1.2 校验租户域名是否重复
        if (StringUtils.isEmpty(createReqVO.getWebsite())) {
            throw exception(TENANT_WEBSITE_IS_NULL);
        } else {
            // 校验租户域名是否重复
            validTenantWebsiteDuplicate(createReqVO.getWebsite(), null);
        }
        // 2. 根据租户套餐编号获取租户套餐
        TenantPackageDO tenantPackage = tenantPackageService.getTenantPackageByCode(PackageTypeEnum.ALL.getCode());
        createReqVO.setPackageId(tenantPackage.getId());

        // 3. 处理有效期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expireTime = LocalDateTime.parse("2099-02-19 00:00:00", formatter);
        if (createReqVO.getExpireTime() == null) {
            createReqVO.setExpireTime(expireTime);
        }

        // 4. 判断用户上限

        // 4.1 先判断上限人数是否大于管理员数量
        if (createReqVO.getAccountCount() < createReqVO.getTenantAdminUserReqVOList().size()) {
            throw exception(LENANT_PERSON_COUNT_LESS_THEN_ADMIN, createReqVO.getAccountCount());
        }

        // 4.2 判断整体分配人数是否超过license限制
        LicenseDO license = licenseService.getLatestActiveLicense();
        // 检查分配人员数量是否超过license限制
        if (license != null) {
            // 获取license总租户数限制
            Integer totalTenantLimit = license.getTenantLimit();
            // 获取现有租户数量
            Integer existTenantCount = getTenantCountExcludePlatform();
            if (existTenantCount >= totalTenantLimit) {
                throw exception(LICENSE_TENANT_COUNT_NOT_ENOUGH);
            }
            // 获取license总人数限制
            Integer totalUserLimit = license.getUserLimit();

            // 获取现有租户已分配的用户数量
            Long otherUserCount = getOtherTenantUserLimitCount(null);
            // 如果传入的分配人员数量加上已分配数量超过license限制，则报错
            if (otherUserCount + createReqVO.getAccountCount() > totalUserLimit) {
                Integer remainingCount = (int) (totalUserLimit - otherUserCount);
                throw exception(LICENSE_USER_COUNT_NOT_ENOUGH, totalUserLimit, remainingCount);
            }
        }

        // 5. 保存数据
        TenantDO tenant = BeanUtils.toBean(createReqVO, TenantDO.class);
        tenant.setPublishModel(createReqVO.getPublishModel() == null ? CommonPublishModelEnum.InnerModel.getValue() : createReqVO.getPublishModel());
        tenantDataRepository.insert(tenant);

        // 6. 创建租户的管理员1
        TenantUtils.execute(tenant.getId(), () -> {
            // 创建管理员角色
            Long roleId = createTenantAdminRole();
            //  开发者角色 判断是否存在开发者，不存在就新增开发者角色
            createDeveloperAdminRole();
            // 创建普通角色权限
            createNormalUserRole();
            // 创建用户，并分配角色
            createSystemUser(roleId, createReqVO);
        });

        // 7. 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("tenant", tenant);

        return tenant.getId();
    }

    private Map<String, AdminUserDO> getUserMobileByUserNames(Set<String> usernamesList) {
        List<AdminUserDO> userDOList = userService.getPlatformUserByUsernames(usernamesList);

        Map<String, List<AdminUserDO>> userGroupMap = userDOList.stream()
                .collect(Collectors.groupingBy(AdminUserDO::getUsername));

        return userGroupMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  // username作为key
                        entry -> {
                            List<AdminUserDO> userList = entry.getValue();
                            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(userList)) {
                                return userList.get(0);
                            }
                            return new AdminUserDO();  // 如果为空则返回空字符串
                        }
                ));

    }


    private void createSystemUser(Long roleId, TenantInsertReqVO insertReqVO) {
        List<TenantAdminUserReqVO> adminUserReqVOList = insertReqVO.getTenantAdminUserReqVOList();

        Set<String> usernamesList = adminUserReqVOList.stream()
                .map(TenantAdminUserReqVO::getAdminUserName)
                .collect(Collectors.toSet());

        Map<String, AdminUserDO> usernameToFirstMobileMap = getUserMobileByUserNames(usernamesList);

        adminUserReqVOList.forEach(adminUserReqVO -> {
            UserInsertReqVO reqVO = new UserInsertReqVO();
            reqVO.setUsername(adminUserReqVO.getAdminUserName());
            reqVO.setNickname(adminUserReqVO.getAdminNickName());
            if (null != usernameToFirstMobileMap.get(adminUserReqVO.getAdminUserName())) {
                AdminUserDO platUser = usernameToFirstMobileMap.get(adminUserReqVO.getAdminUserName());
                if (null != platUser) {
                    reqVO.setMobile(platUser.getMobile());
                    reqVO.setEmail(platUser.getEmail());
                    reqVO.setPlatformUserId(platUser.getId());
                }
            }
            reqVO.setAdminType(AdminTypeEnum.SYSTEM.getType());
            reqVO.setPassword(pwdEnHelper.encryptHexStr(TENANT_ADMIN_PASSWORD));
            // 通过平台创建空间（Tenant）用户
            reqVO.setUserType(UserTypeEnum.TENANT.getValue());
            // 创建用户
            Long userId = userService.createUser(reqVO);
            // 分配 管理员角色
            permissionService.assignUserRoles(userId, singleton(roleId));
        });
    }

    private Long createTenantAdminRole() {
        // 创建角色
        RoleInsertReqVO reqVO = new RoleInsertReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName()).setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        return roleId;
    }


    private void createDeveloperAdminRole() {
        RoleDO roleDO = roleService.getRoleByCode(RoleCodeEnum.APP_DEVELOPER.getCode());
        if (roleDO == null) {
            // 创建角色
            RoleInsertReqVO reqVO = new RoleInsertReqVO();
            reqVO.setName(RoleCodeEnum.APP_DEVELOPER.getName()).setCode(RoleCodeEnum.APP_DEVELOPER.getCode())
                    .setSort(0).setRemark("系统自动生成");
            roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        }
    }

    private void createNormalUserRole() {
        RoleDO roleDO = roleService.getRoleByCode(RoleCodeEnum.NORMAL_USER.getCode());
        if (roleDO == null) {
            // 创建角色
            RoleInsertReqVO reqVO = new RoleInsertReqVO();
            reqVO.setName(RoleCodeEnum.NORMAL_USER.getName()).setCode(RoleCodeEnum.NORMAL_USER.getCode())
                    .setSort(0).setRemark("系统自动生成");
            roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_TENANT_TYPE, subType = SYSTEM_TENANT_UPDATE_SUB_TYPE, bizNo = "{{#tenant.id}}",
            success = SYSTEM_TENANT_UPDATE_SUCCESS)
    public void updateTenant(@Valid TenantUpdateReqVO updateReqVO) {
        // 校验存在
        TenantDO tenant = validateUpdateTenant(updateReqVO.getId());

        // 校验租户名称是否重复
        validTenantNameDuplicate(updateReqVO.getName(), updateReqVO.getId());
        // 校验租户域名是否重复
        if (StringUtils.isNotBlank(updateReqVO.getWebsite())) {
            validTenantWebsiteDuplicate(updateReqVO.getWebsite(), updateReqVO.getId());
        }

        LicenseDO license = licenseService.getLatestActiveLicense();
        // 检查分配人员数量是否超过license限制
        if (license != null) {
            // 获取license总人数限制
            Integer userLimit = license.getUserLimit();
            // 获取除当前租户外的其他租户已分配人员数量，判断上限
            Long otherUserCount = getOtherTenantUserLimitCount(updateReqVO.getId());
            // 如果传入的分配人员数量加上已分配数量超过license限制，则报错
            if (updateReqVO.getAccountCount() != null &&
                    (otherUserCount + updateReqVO.getAccountCount()) > userLimit) {

                Integer remainingCount = (int) (userLimit - otherUserCount);
                throw exception(LICENSE_USER_COUNT_NOT_ENOUGH,
                        userLimit,
                        remainingCount);
            }
            if (updateReqVO.getAccountCount() == null) {
                updateReqVO.setAccountCount(tenant.getAccountCount());
            }
            TenantUtils.execute(tenant.getId(), () -> {
                // 查询当前租户下已分配的用户数量，下限
                Integer count = userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
                if (updateReqVO.getAccountCount() < count) {
                    throw exception(LENANT_ALLOCATE_PERSON_COUNT_LESS_THEN_ALLOCATED,
                            count);
                }
            });
        }
        // 先更新租户
        TenantDO updateObj = new TenantDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setName(updateReqVO.getName());
        updateObj.setWebsite(updateReqVO.getWebsite());
        updateObj.setAccountCount(updateReqVO.getAccountCount());
        updateObj.setStatus(updateReqVO.getStatus());
        updateObj.setPublishModel(updateReqVO.getPublishModel());
        updateObj.setLogoUrl(updateReqVO.getLogoUrl());
        tenantDataRepository.update(updateObj);
        // 修改租户管理员
        if (updateReqVO.getTenantAdminUserUpdateReqVOSList() != null) {
            if (updateReqVO.getTenantAdminUserUpdateReqVOSList().isEmpty()) {
                throw exception(TENANT_ADMIN_ISNULL);
            }
            TenantUtils.execute(updateObj.getId(), () -> {

                // 管理员变了，把旧管理员角色移除，并降级为自定义
                Long roleId = getAdminRoleAndDeleteOldUserRole(tenant);
                // 判断管理员是否发生变更
                List<String> adminUserRespDTOS = adminUserRoleApi.getUserRoleByRoleIdAndTenantId(roleId, tenant.getId());
                List<Long> userIds = adminUserRespDTOS.stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                // 使用AdminUserService批量获取用户数据
                List<AdminUserDO> users = userService.getUserList(userIds);
                Map<String, Long> usernameIdMap = users.stream()
                        .collect(Collectors.toMap(AdminUserDO::getUsername, AdminUserDO::getId));
                // 删除处理
                Map<String, String> adminUsernameMap = updateReqVO.getTenantAdminUserUpdateReqVOSList().stream()
                        .collect(Collectors.toMap(TenantAdminUserReqVO::getAdminUserName, TenantAdminUserReqVO::getAdminUserName));

                // 获取需要删除的用户（在现有用户中但不在新管理员列表中的用户）
                Map<String, Long> usersToDelete = usernameIdMap.entrySet().stream()
                        .filter(entry -> !adminUsernameMap.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                for (Long userId : usersToDelete.values()) {
                    // 移除旧用户租户管理员角色
                    UserRoleDO userRoleDO = permissionService.getUserRoleByUserAndRoleId(userId, roleId);
                    if (userRoleDO != null) {
                        permissionService.deleteRoleUsers(roleId, singleton(userRoleDO.getUserId()));
                    }
                    // 将旧管理员设置为普通用户,降级
                    userService.updateAdminType(userId, AdminTypeEnum.CUSTOM.getType());
                }

                updateReqVO.getTenantAdminUserUpdateReqVOSList().forEach(adminUserReqVO -> {
                    // 获取用户手机
                    Set<String> usernamesList = updateReqVO.getTenantAdminUserUpdateReqVOSList().stream()
                            .map(TenantAdminUserReqVO::getAdminUserName)
                            .collect(Collectors.toSet());
                    Map<String, AdminUserDO> usernameToFirstMobileMap = getUserMobileByUserNames(usernamesList);

                    // 已存在的用户
                    AdminUserDO newAdminUser = userService.getUserByUsername(adminUserReqVO.getAdminUserName());
                    // 判断前端上送的管理员是否已存在，存在则分配角色且不是老用户
                    if (newAdminUser == null) {

                        // 新管理员用户不存在，创建用户，并分配角色
                        UserInsertReqVO userInsertReqVO = new UserInsertReqVO();
                        userInsertReqVO.setUsername(adminUserReqVO.getAdminUserName());
                        userInsertReqVO.setNickname(adminUserReqVO.getAdminNickName());
                        if (null != usernameToFirstMobileMap.get(adminUserReqVO.getAdminUserName())) {
                            AdminUserDO platUser = usernameToFirstMobileMap.get(adminUserReqVO.getAdminUserName());
                            if (null != platUser) {
                                userInsertReqVO.setMobile(platUser.getMobile());
                                userInsertReqVO.setEmail(platUser.getEmail());
                                userInsertReqVO.setPlatformUserId(platUser.getId());
                            }
                        }
                        userInsertReqVO.setAdminType(AdminTypeEnum.SYSTEM.getType());
                        userInsertReqVO.setPassword(pwdEnHelper.encryptHexStr(TENANT_ADMIN_PASSWORD));
                        // 新增的都是空间管理员
                        userInsertReqVO.setUserType(UserTypeEnum.TENANT.getValue());
                        // 创建用户
                        Long userId = userService.createUser(userInsertReqVO);
                        // 分配管理员权限
                        permissionService.assignUserRoles(userId, singleton(roleId));
                    } else {
                        // 新管理员用户存在，直接分配角色
                        permissionService.assignUserRoles(newAdminUser.getId(), singleton(roleId));
                        // 将新管理员设置为内置用户类型
                        userService.updateAdminType(newAdminUser.getId(), AdminTypeEnum.SYSTEM.getType());
                    }
                });
            });
        }

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("tenant", tenantDataRepository.findById(updateObj.getId()));

    }

    private Long getAdminRoleAndDeleteOldUserRole(TenantDO tenant) {
        RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
        Long roleId;
        if (roleDO == null) {
            // 创建角色
            roleId = createTenantAdminRole();
        } else {
            roleId = roleDO.getId();
        }
        return roleId;
    }

    private void validTenantNameDuplicate(String name, Long id) {

        if (StringUtils.isBlank(name)) {
            return;
        }
        TenantDO tenant = tenantDataRepository.findByName(name);

        if (tenant == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同名字的租户
        if (id == null) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_NAME_DUPLICATE, name);
        }
    }

    private void validTenantWebsiteDuplicate(String website, Long id) {
        if (StrUtil.isEmpty(website)) {
            return;
        }
        TenantDO tenant = tenantDataRepository.findByWebsite(website);

        if (tenant == null) {
            return;
        }
        // 如果 租户id 为空，则报错已存在同网站的租户
        if (id == null) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
        if (!tenant.getId().equals(id)) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
    }

    @Override
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        TenantUtils.execute(tenantId, () -> {
            // 获得所有角色
            List<RoleDO> roles = roleService.getRoleList();
            roles.forEach(role -> Assert.isTrue(tenantId.equals(role.getTenantId()), "角色({}/{}) 租户不匹配",
                    role.getId(), role.getTenantId(), tenantId)); // 兜底校验
            // 重新分配每个角色的权限
            roles.forEach(role -> {
                // 如果是租户管理员，重新分配其权限为租户套餐的权限
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                    log.info("[updateTenantRoleMenu][租户管理员({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), menuIds);
                    return;
                }
                // 如果是其他角色，则去掉超过套餐的权限
                Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                roleMenuIds = CollUtil.intersectionDistinct(roleMenuIds, menuIds);
                permissionService.assignRoleMenu(role.getId(), roleMenuIds);
                log.info("[updateTenantRoleMenu][角色({}/{}) 的权限修改为({})]", role.getId(), role.getTenantId(), roleMenuIds);
            });
        });
    }

    @Resource
    private UserDataRepository userDataRepository;
    @Resource
    private UserPostDataRepository userPostDataRepository;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;

    @Resource
    private AppAuthRoleUserService appAuthRoleUserService;

    @Resource
    private RoleDataRepository roleDataRepository;

    @Resource
    private RoleMenuDataRepository roleMenuDataRepository;

    @Resource
    private DeptDataRepository deptDataRepository;

    @Resource
    private CorpDataRepository corpDataRepository;

    @Resource
    private CorpAppRelationDataRepository corpAppRelationDataRepository;

    @Resource
    private DictTypeRepository dictTypeRepository;

    @Resource
    private DictDataRepository dictDataRepository;

    @Resource
    private SystemConfigDataRepository systemConfigDataRepository;

    @Override
    @LogRecord(type = SYSTEM_TENANT_TYPE, subType = SYSTEM_TENANT_DELETE_SUB_TYPE, bizNo = "{{#tenant.id}}",
            success = SYSTEM_TENANT_DELETE_SUCCESS)
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long tenantId) {
        // 校验存在
        TenantDO tenant = validateUpdateTenant(tenantId);
        // 在租户上下文中执行其他删除操作
        TenantUtils.execute(tenantId, () -> {
            // 1. 删除用户
            userDataRepository.removeByTenant(tenantId);
            // 1.1. 删除用户&应用关联
            appAuthRoleUserService.deleteByTenant(tenantId);
            // 1.2 删除用户&角色关联
            userRoleDataRepository.removeByTenant(tenantId);
            // 1.3 删除用户岗位
            userPostDataRepository.removeByTenant(tenantId);
            // 2. 删除角色
            roleDataRepository.removeByTenant(tenantId);
            // 2.1 删除角色&权限点
            roleMenuDataRepository.removeByTenant(tenantId);
            // 3. 删除部门
            deptDataRepository.removeByTenant(tenantId);
            // 4. 删除企业
            corpDataRepository.removeByTenant(tenantId);
            // 4.1 删除企业&应用授权
            corpAppRelationDataRepository.removeByTenant(tenantId);
            // 5. 删除租户级别字典Dict
            List<DictTypeDO> dictTypeDOList = dictTypeRepository.findAllListByOwner(TENANT, tenantId);
            // 5.1 删除字典类型对应的数据
            dictDataRepository.removeDictDataByType(dictTypeDOList.stream().map(DictTypeDO::getType).collect(Collectors.toList()));
            // 5.2 删除字典类型
            dictTypeRepository.removeByDictOwnerId(tenantId);
            // 6. 删除租户级别配置项Config
            systemConfigDataRepository.removeByTenant(tenantId);
            // 7. 删除安全配置和安全记录
            securityConfigApi.removeSecurityConfigsByTenantId(tenant.getId());
            securityConfigApi.removeSecurityRecordsByTenantId(tenant.getId());
            // 8. 删除应用和大屏
            List<ApplicationDTO> applications = appApplicationApi.getSimpleAllAppList(tenant.getId());
            for (ApplicationDTO application : applications) {
                appServiceApi.deleteApplication(application.getId(), application.getAppName());
            }
        });
        // 删除空间
        tenantDataRepository.deleteById(tenantId);
        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("tenant", tenant);
    }

    private TenantDO validateUpdateTenant(Long id) {
        TenantDO tenant = tenantDataRepository.findById(id);

        if (tenant == null) {
            throw exception(TENANT_NOT_EXISTS);
        }
        // 内置租户，不允许删除
        if (isPlatformTenant(tenant)) {
            throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
        }
        return tenant;
    }

    @Override
    public TenantDO getTenant(Long id) {
        return tenantDataRepository.findById(id);
    }


    @Override
    public TenantRespVO getTenantWithAppCount(Long id) {
        // 1. 非平台管理员，仅允许获取自己的租户信息
        boolean isPlatformAdmin = permissionService.isPlatformSuperAdmin(SecurityFrameworkUtils.getLoginUserId());
        if (!isPlatformAdmin) {
            Long loginTenantId = TenantContextHolder.getTenantId();
            if (!Objects.equals(loginTenantId, id)) {
                throw exception(TENANT_ONLY_GET_SELF);
            }
        }

        // 2. 获取空间的企业数量信息
        Map<Long, Integer> corpCountMap = findCorpCount();
        TenantDO tenantDO = getTenant(id);
        // 查询当前租户下的已有的正常状态的用户数量
        Integer count = userService.getUserCountByStatus(UserStatusEnum.NORMAL.getStatus());
        TenantRespVO tenantRespVO = TenantConvert.INSTANCE.convert(tenantDO);
        tenantRespVO.setExistUserCount(count);
        Long appCountResult = appApplicationApi.countApplicationByTenantId(id);
        // Long 转 Integer
        tenantRespVO.setAppCount(appCountResult != null ? appCountResult.intValue() : 0);
        Integer corpCount = corpCountMap.get(tenantDO.getId());
        if (corpCount == null) {
            corpCount = CorpConstant.ZERO; // 默认值处理
        }
        tenantRespVO.setCorpCount(corpCount);
        // 3. 获取当前空间的管理员角色id
        //  RoleDO roleDO = roleService.getRoleIdsByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
        RoleDO roleDO = roleService.getRoleIdsByCodeAndTenantId(RoleCodeEnum.TENANT_ADMIN.getCode(), id);
        if (roleDO != null) {
            List<String> adminUserRespDTOS = adminUserRoleApi.getUserRoleByRoleIdAndTenantId(roleDO.getId(), id);
            List<Long> userIds = adminUserRespDTOS.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            // 使用AdminUserService批量获取用户数据
            // 获取角色对应的管理员
            // 获取租户管理员用户信息
            List<TenantAdminUserResVO> adminUserList = new ArrayList<>();
            if (!userIds.isEmpty()) {
                List<AdminUserDO> adminUsers = userService.getUserList(userIds);
                List<Long> deptIds = adminUsers.stream().map(AdminUserDO::getDeptId).filter(Objects::nonNull).toList();

                Map<Long, DeptDO> deptIdToDeptMap = new HashMap<>();
                if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(deptIds)) {
                    List<DeptDO> deptDOList = deptService.getDeptList(deptIds);
                    deptIdToDeptMap = deptDOList.stream()
                            .collect(Collectors.toMap(
                                    DeptDO::getId,  // 以部门ID作为key
                                    dept -> dept    // 以部门对象作为value
                            ));
                }


                Map<Long, DeptDO> finalDeptIdToDeptMap = deptIdToDeptMap;
                adminUserList = adminUsers.stream()
                        .filter(Objects::nonNull)
                        .map(uservo -> new TenantAdminUserResVO()
                                .setAdminUserName(uservo.getUsername())
                                .setAdminMobile(uservo.getMobile())
                                .setAdminUserId(uservo.getId())
                                .setAdminNickName(uservo.getNickname())
                                .setAdminEmail(uservo.getEmail())
                                .setPlatformUserId(uservo.getPlatformUserId())
                                .setAdminAvatar(uservo.getAvatar())
                                .setDeptName(finalDeptIdToDeptMap.get(uservo.getDeptId()) != null ? finalDeptIdToDeptMap.get(uservo.getDeptId()).getName() : "")


                        )
                        .collect(Collectors.toList());
            }
            tenantRespVO.setTenantAdminUserList(adminUserList);
        }
        return tenantRespVO;
    }

    @Override
    public TenantRespVO getTenantAndPlatformAdminInfo(Long id) {
        TenantRespVO tenantPlatformInfo = getTenantWithAppCount(id);

        // 过滤出tenantAdminUserList中platformUserId不为空的数据
        if (tenantPlatformInfo.getTenantAdminUserList() != null) {
            List<TenantAdminUserResVO> filteredAdminUserList = tenantPlatformInfo.getTenantAdminUserList().stream()
                    .filter(adminUser -> adminUser.getPlatformUserId() != null)
                    .collect(Collectors.toList());
            tenantPlatformInfo.setTenantAdminUserList(filteredAdminUserList);
        }

        return tenantPlatformInfo;
    }

    /**
     * 获取所有企业的数据，并根据tenantId分组获取条数
     *
     * @return Map<tenantId, count>
     */
    @TenantIgnore
    public Map<Long, Integer> findCorpCount() {
        List<CorpDO> corpList = corpService.findCorpAll();
        return corpList.stream()
                .collect(Collectors.groupingBy(
                        CorpDO::getTenantId,
                        Collectors.summingInt(corp -> 1) // 使用summingInt替代counting
                ));
    }

    /**
     * 获取所有应用
     *
     * @return
     */
    @TenantIgnore
    public Map<Long, Integer> findAppCount() {
        return appApplicationApi.countAppByTenantId();
    }

    @TenantIgnore
    @Override
    public PageResult<TenantRespVO> getTenantPage(TenantPageReqVO reqVO) {
        PageResult<TenantDO> tenantDOPageResult = tenantDataRepository.findPage(reqVO);
        if (CollUtil.isEmpty(tenantDOPageResult.getList())) {
            return PageResult.empty();
        }
        List<TenantDO> tenantDOList = tenantDOPageResult.getList();
        List<Long> tenantIds = CollectionUtils.convertList(tenantDOList, TenantDO::getId);
        Map<Long, Integer> existUserCountMap = userService.getTenantExistUserCountByIds(tenantIds);
        Map<Long, Integer> coupCountMap = findCorpCount();
        Map<Long, Integer> appCountMap = findAppCount();
        // 转换为VO并设置昵称

        List<TenantRespVO> tenantRespVOList = tenantDOPageResult.getList().stream()
                .map(tenantDO -> {
                    TenantRespVO tenantRespVO = TenantConvert.INSTANCE.convert(tenantDO);
                    tenantRespVO.setLogoUrl(tenantDO.getLogoUrl());
                    Integer existUserCount = existUserCountMap.get(tenantDO.getId());
                    if (existUserCount == null) {
                        existUserCount = CorpConstant.ZERO; // 默认值处理
                    }
                    tenantRespVO.setExistUserCount(existUserCount);

                    Integer corpCount = coupCountMap.get(tenantDO.getId());
                    if (corpCount == null) {
                        corpCount = CorpConstant.ZERO; // 默认值处理
                    }
                    tenantRespVO.setCorpCount(corpCount);

                    Integer appCount = appCountMap.get(tenantDO.getId());
                    if (appCount == null) {
                        appCount = CorpConstant.ZERO; // 默认值处理
                    }
                    tenantRespVO.setAppCount(appCount);
                    return tenantRespVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(tenantRespVOList, tenantDOPageResult.getTotal());

    }

    @Override
    public TenantDO getTenantByName(String name) {
        return tenantDataRepository.findByName(name);
    }

    @Override
    public TenantDO getTenantByWebsite(String website) {
        return tenantDataRepository.findByWebsite(website);
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return tenantDataRepository.countByPackageId(packageId);
    }

    @Override
    public Integer getTenantCountExcludePlatform() {
        return (int) tenantDataRepository.countExcludePlatform();
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return tenantDataRepository.findAllByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByStatus(Integer status) {
        return tenantDataRepository.findAllByStatus(status);
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        // 执行处理器
        handler.handle(tenant);
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        // 如果禁用，则不执行逻辑
        if (isTenantDisable()) {
            return;
        }
        // 获得租户，然后获得菜单
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isPlatformTenant(tenant)) { // 系统租户，菜单是全量的
            menuIds = permissionService.getAllValidActiveMenuIds();
        } else {
            TenantPackageDO tenantPackage = tenantPackageService.getTenantPackage(tenant.getPackageId());
            Set<String> tenantAllPermissions = null;
            if (PackageTypeEnum.ALL.getCode().equals(tenantPackage.getCode())) {
                // 若是 PackageTypeEnum.ALL, tenantAllPermissions = tenant、app开头的权限
                menuIds = permissionService.getAllValidActiveMenuIds();
            } else {
                // 不是All，tenantAllPermissions = package下写入的所有权限点
                menuIds = tenantPackage.getMenuIds();
            }
        }
        // 执行处理器
        handler.handle(menuIds);
    }

    private static boolean isPlatformTenant(TenantDO tenant) {

        return Objects.equals(tenant.getTenantCode(), TenantCodeEnum.PLATFORM_TENANT.getCode());
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }
}

