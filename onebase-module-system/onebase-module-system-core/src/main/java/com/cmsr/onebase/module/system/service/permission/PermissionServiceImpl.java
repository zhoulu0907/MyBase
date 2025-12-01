package com.cmsr.onebase.module.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cmsr.onebase.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.common.security.dto.LoginUser;
import com.cmsr.onebase.framework.common.util.collection.CollectionUtils;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.convert.auth.AuthConvert;
import com.cmsr.onebase.module.system.dal.database.RoleMenuDataRepository;
import com.cmsr.onebase.module.system.dal.database.UserRoleDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import com.cmsr.onebase.module.system.enums.permission.DataScopeEnum;
import com.cmsr.onebase.module.system.enums.permission.MenuConstants;
import com.cmsr.onebase.module.system.enums.permission.PackageTypeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.service.dept.DeptService;
import com.cmsr.onebase.module.system.service.tenant.TenantPackageService;
import com.cmsr.onebase.module.system.service.tenant.TenantService;
import com.cmsr.onebase.module.system.service.user.UserService;
import com.cmsr.onebase.module.system.vo.auth.AuthPermissionInfoRespVO;
import com.cmsr.onebase.module.system.vo.permission.PermissionMenuRespVO;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.security.SecurityFrameworkUtils.getLoginUserId;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertSet;
import static com.cmsr.onebase.framework.common.util.json.JsonUtils.toJsonString;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;

/**
 * 权限 Service 实现类
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private RoleService          roleService;
    @Resource
    private MenuService          menuService;
    @Resource
    private DeptService          deptService;
    @Resource @Lazy
    private UserService          userService;
    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    @Lazy // 延迟，避免循环依赖报错
    private TenantService        tenantService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private UserRoleDataRepository userRoleDataRepository;
    @Resource
    private RoleMenuDataRepository roleMenuDataRepository;

    @Override
    public boolean isPlatformSuperAdmin(Long userId) {
        // 获得当前登录的角色。如果为空，说明没有权限
        List<RoleDO> roles = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roles)) {
            return false;
        }
        return roleService.hasAnySuperOrTenantAdmin(convertSet(roles, RoleDO::getId));
    }

    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(permissions)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        List<RoleDO> roles = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roles)) {
            return false;
        }

        // 情况一：如果是平台管理员，赋予所有权限
        boolean isPlatformSuperAdmin = roleService.hasAnySuperOrTenantAdmin(convertSet(roles, RoleDO::getId));
        if (isPlatformSuperAdmin) {
            return true;
        }

        // 情况二：如果是租户管理员，赋予所有租户的权限
        boolean isTenantAdmin = roleService.isTenantAdmin(convertSet(roles, RoleDO::getId));
        if (isTenantAdmin) {
            // 读取 tenant package，获取租户所有的权限点 tenantAllPermissions
            TenantDO tenant = tenantService.getTenant(TenantContextHolder.getRequiredTenantId());
            TenantPackageDO tenantPackage = tenantPackageService.getTenantPackage(tenant.getPackageId());
            Set<String> tenantAllPermissions = null;
            if (PackageTypeEnum.ALL.getCode().equals(tenantPackage.getCode())) {
                // 若是 PackageTypeEnum.ALL, tenantAllPermissions = tenant、app开头的权限
                // List<MenuDO> menuList = menuService.getMenuList();
                // 过滤出permission字段值为app和tenant开头的菜单项
                // tenantAllPermissions = menuList.stream()
                //         .filter(menu -> menu.getPermission() != null &&
                //                 (menu.getPermission().startsWith(MenuConstants.MENU_APP)
                //                         || menu.getPermission().startsWith(MenuConstants.MENU_TENANT)
                //                 || menu.getPermission().startsWith(MenuConstants.MENU_SYSTEM)))
                //         .map(MenuDO::getPermission)
                //         .collect(Collectors.toSet());
                // 租户管理员拥有所有权限
                return true;
            } else {
                // 不是All，tenantAllPermissions = package下写入的所有权限点
                Set<Long> menuIds = tenantPackage.getMenuIds();
                List<MenuDO> menuList = menuService.getAllActiveMenuList(menuIds);
                tenantAllPermissions = menuList.stream().map(MenuDO::getPermission).filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
            // permissions 和 tenantAllPermissions对比，命中一个即返回true
            for (String permission : permissions) {
                if (tenantAllPermissions.contains(permission)) {
                    return true;
                }
            }
        }

        // 情况二：如果是企业管理员，赋予所有企业相关权限
        boolean isCorpAdmin = roleService.hasAnyCorpAdmin(convertSet(roles, RoleDO::getId));
        if (isCorpAdmin) {
            Set<Long> menuIds = getAllCorpActiveMenuIds();
            List<MenuDO> menuList = menuService.getAllActiveMenuList(menuIds);
            Set<String> tenantAllPermissions = menuList.stream().map(MenuDO::getPermission).filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            // permissions 和 tenantAllPermissions对比，命中一个即返回true
            for (String permission : permissions) {
                if (tenantAllPermissions.contains(permission)) {
                    return true;
                }
            }
        }
        // 情况二：如果是开发管理员，赋予所有开发相关权限
        boolean isDevAdmin = roleService.hasAnyDevloperAdmin(convertSet(roles, RoleDO::getId));
        if (isDevAdmin) {
            // 所有开发者的权限
            Set<String> menuCodes = RoleCodeEnum.APP_DEVELOPER.getDevloperPermissionCodes();
            for (String permission : permissions) {
                if (menuCodes.contains(permission)) {
                    return true;
                }
            }
        }

        // 情况三：遍历判断每个权限，如果有一满足，说明有权限
        for (String permission : permissions) {
            if (hasAnyPermission(roles, permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断指定角色，是否拥有该 permission 权限
     *
     * @param roles      指定角色数组
     * @param permission 权限标识
     * @return 是否拥有
     */
    private boolean hasAnyPermission(List<RoleDO> roles, String permission) {
        List<Long> menuIds = menuService.getMenuIdListByPermissionFromCache(permission);
        // 采用严格模式，如果权限找不到对应的 Menu 的话，也认为没有权限
        if (CollUtil.isEmpty(menuIds)) {
            return false;
        }

        // 判断是否有权限
        Set<Long> roleIds = convertSet(roles, RoleDO::getId);
        for (Long menuId : menuIds) {
            // 获得拥有该菜单的角色编号集合
            Set<Long> menuRoleIds = getSelf().getMenuRoleIdListByMenuIdFromCache(menuId);
            // 如果有交集，说明有权限
            if (CollUtil.containsAny(menuRoleIds, roleIds)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(roles)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        List<RoleDO> roleList = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roleList)) {
            return false;
        }

        // 判断是否有角色
        Set<String> userRoles = convertSet(roleList, RoleDO::getCode);
        return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
    }

    // ========== 角色-菜单的相关方法  ==========

    @Override
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
                    allEntries = true),
            @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
                    allEntries = true) // allEntries 清空所有缓存，主要一次更新涉及到的 menuIds 较多，反倒批量会更快
    })
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSION_ASSIGN_ROLE_MENU_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_PERMISSION_ASSIGN_ROLE_MENU_SUCCESS)
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
        // 获得角色拥有菜单编号
        Set<Long> dbMenuIds = convertSet(roleMenuDataRepository.findListByRoleId(roleId), RoleMenuDO::getMenuId);
        // 计算新增和删除的菜单编号
        Set<Long> menuIdList = CollUtil.emptyIfNull(menuIds);
        Collection<Long> createMenuIds = CollUtil.subtract(menuIdList, dbMenuIds);
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbMenuIds, menuIdList);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (CollUtil.isNotEmpty(createMenuIds)) {
//            bug fixed: class java.lang.String cannot be cast to class java.lang.Long
            List<RoleMenuDO> entities = new ArrayList<>();
            for (Object menuId : createMenuIds) {
                Long mId = Long.parseLong(menuId.toString());
                entities.add(new RoleMenuDO().setMenuId(mId).setRoleId(roleId));
            }
            roleMenuDataRepository.insertBatch(entities);
        }
        if (CollUtil.isNotEmpty(deleteMenuIds)) {
            roleMenuDataRepository.deleteByRoleIdAndMenuIds(roleId, deleteMenuIds);
        }
        // 3. 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        List<MenuDO> allActiveMenuList = menuService.getAllActiveMenuList(menuIds);
        Stream<String> menuNames = allActiveMenuList.stream().map(men -> men.getName());
        RoleDO role = roleService.getRole(roleId);

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("role", role);
        LogRecordContext.putVariable("menuNames", menuNames.collect(Collectors.joining(",")));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST,
                    allEntries = true), // allEntries 清空所有缓存，此处无法方便获得 roleId 对应的 menu 缓存们
            @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST,
                    allEntries = true) // allEntries 清空所有缓存，此处无法方便获得 roleId 对应的 user 缓存们
    })
    public void processRoleDeleted(Long roleId) {
        // 标记删除 UserRole
        userRoleDataRepository.deleteByRoleId(roleId);
        // 标记删除 RoleMenu
        roleMenuDataRepository.deleteByRoleId(roleId);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public void processMenuDeleted(Long menuId) {
        roleMenuDataRepository.deleteByMenuId(menuId);
    }

    @Override
    public Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }

        // 如果是平台、空间管理员的情况下，获取全部菜单编号
        if (roleService.hasAnySuperOrTenantAdmin(roleIds)) {
            return getAllValidActiveMenuIds();
        }
        // 如果是企业管理员的情况下，获取企业菜单编号
        if (roleService.hasAnyCorpAdmin(roleIds)) {
            return getAllCorpActiveMenuIds();
        }
        // 如果是开发管理员的情况下，获取开发菜单编号
        if (roleService.hasAnyDevloperAdmin(roleIds)) {
            Set<String> menuCode = RoleCodeEnum.APP_DEVELOPER.getDevloperPermissionCodes();
            List<MenuDO> menudoList = menuService.getAllActiveMenuListByCodes(menuCode);
            return convertSet(menudoList, MenuDO::getId);
        }

        // 如果是非管理员的情况下，获得拥有的菜单编号
        return convertSet(roleMenuDataRepository.findListByRoleIds(roleIds), RoleMenuDO::getMenuId);
    }

    @Override
    public Set<Long> getAllCorpActiveMenuIds() {
        // 获取所有权限
        List<MenuDO> menuList = menuService.getAllEnableMenuList();
        // 过滤出 tenantAllPermissions = tenant、app开头的菜单项
        Set<Long> tenantAllPermissions = menuList.stream()
                .filter(menu -> menu.getPermission() != null
                        && menu.getPermission().startsWith(MenuConstants.MENU_CORP))
                .map(MenuDO::getId)
                .collect(Collectors.toSet());
        return tenantAllPermissions;
    }

    @Override
    public Set<Long> getAllValidActiveMenuIds() {
        // 获取所有权限
        List<MenuDO> menuList = menuService.getAllEnableMenuList();
        // 过滤出 tenantAllPermissions = tenant、app开头的菜单项
        Set<Long> tenantAllPermissions = menuList.stream()
                .filter(menu -> menu.getPermission() != null
                        && (menu.getPermission().startsWith(MenuConstants.MENU_TENANT)
                        || menu.getPermission().startsWith(MenuConstants.MENU_CORP)
                        || menu.getPermission().startsWith(MenuConstants.MENU_SYSTEM)))
                .map(MenuDO::getId)
                .collect(Collectors.toSet());
        return tenantAllPermissions;
    }


    @Override
    @Cacheable(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public Set<Long> getMenuRoleIdListByMenuIdFromCache(Long menuId) {
        return convertSet(roleMenuDataRepository.findListByMenuId(menuId), RoleMenuDO::getRoleId);
    }

    // ========== 用户-角色的相关方法  ==========

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSION_ASSIGN_USER_ROLES_SUB_TYPE, bizNo = "{{#user.id}}",
            success = SYSTEM_PERMISSION_ASSIGN_USER_ROLES_SUCCESS)
    public void assignUserRoles(Long userId, Set<Long> roleIds) {
        AdminUserDO user = userService.getUser(userId);
        // 获得角色拥有角色编号
        Set<Long> dbRoleIds = convertSet(userRoleDataRepository.findListByUserId(userId), UserRoleDO::getRoleId);
        // 计算新增和删除的角色编号
        Set<Long> roleIdList = CollUtil.emptyIfNull(roleIds);
        Collection<Long> createRoleIds = CollUtil.subtract(roleIdList, dbRoleIds);
        Collection<Long> deleteRoleIds = CollUtil.subtract(dbRoleIds, roleIdList);
        // 执行新增和删除。对于已经授权的角色，不用做任何处理
        if (!CollUtil.isEmpty(createRoleIds)) {
            userRoleDataRepository.insertBatch(CollectionUtils.convertList(createRoleIds, roleId -> {
                UserRoleDO entity = new UserRoleDO();
                entity.setUserId(userId);
                entity.setRoleId(roleId);
                return entity;
            }));
        }
        if (!CollUtil.isEmpty(deleteRoleIds)) {
            userRoleDataRepository.deleteByUserIdAndRoleIds(userId, deleteRoleIds);
        }

        List<RoleDO> roleList = roleService.getRoleList(roleIds);

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("user", user);
        LogRecordContext.putVariable("roleNames", roleList.stream().map(RoleDO::getName).collect(Collectors.joining(",")));
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void processUserDeleted(Long userId) {
        userRoleDataRepository.deleteByUserId(userId);
    }

    @Override
    public Set<Long> getRoleIdsListByUserId(Long userId) {
        return convertSet(userRoleDataRepository.findListByUserId(userId), UserRoleDO::getRoleId);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public Set<Long> getUserRoleIdListByUserIdFromCache(Long userId) {
        return getRoleIdsListByUserId(userId);
    }

    @Override
    public Set<Long> getUserIdsListByRoleIds(Collection<Long> roleIds) {
        return convertSet(userRoleDataRepository.findListByRoleIds(roleIds), UserRoleDO::getUserId);
    }

    /**
     * 获得用户拥有的角色，并且这些角色是开启状态的
     *
     * @param userId 用户编号
     * @return 用户拥有的角色
     */
    @VisibleForTesting
    List<RoleDO> getEnableUserRoleListByUserIdFromCache(Long userId) {
        // 获得用户拥有的角色编号
        Set<Long> roleIds = getSelf().getUserRoleIdListByUserIdFromCache(userId);
        // 获得角色数组，并移除被禁用的
        List<RoleDO> roles = roleService.getRoleListFromCache(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus()));
        return roles;
    }

    // ========== 用户-部门的相关方法  ==========

    @Override
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSION_ASSIGN_ROLE_DATA_SCOPE_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_PERMISSION_ASSIGN_ROLE_DATA_SCOPE_SUCCESS)
    public void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        RoleDO role = roleService.getRole(roleId);

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("role", role);
        LogRecordContext.putVariable("dataScopeDeptIds", dataScopeDeptIds);
    }

    @Override
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        // 获得用户的角色
        List<RoleDO> roles = getEnableUserRoleListByUserIdFromCache(userId);

        // 如果角色为空，则只能查看自己
        DeptDataPermissionRespDTO result = new DeptDataPermissionRespDTO();
        if (CollUtil.isEmpty(roles)) {
            result.setSelf(true);
            return result;
        }

        // 获得用户的部门编号的缓存，通过 Guava 的 Suppliers 惰性求值，即有且仅有第一次发起 DB 的查询
        Supplier<Long> userDeptId = Suppliers.memoize(() -> userService.getUser(userId).getDeptId());
        // 遍历每个角色，计算
        for (RoleDO role : roles) {
            // 为空时，跳过
            if (role.getDataScope() == null) {
                continue;
            }
            // 情况一，ALL
            if (Objects.equals(role.getDataScope(), DataScopeEnum.ALL.getScope())) {
                result.setAll(true);
                continue;
            }
            // 情况二，DEPT_CUSTOM
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_CUSTOM.getScope())) {
                CollUtil.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
                // 自定义可见部门时，保证可以看到自己所在的部门。否则，一些场景下可能会有问题。
                // 例如说，登录时，基于 t_user 的 username 查询会可能被 dept_id 过滤掉
                CollUtil.addAll(result.getDeptIds(), userDeptId.get());
                continue;
            }
            // 情况三，DEPT_ONLY
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_ONLY.getScope())) {
                CollectionUtils.addIfNotNull(result.getDeptIds(), userDeptId.get());
                continue;
            }
            // 情况四，DEPT_DEPT_AND_CHILD
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_AND_CHILD.getScope())) {
                CollUtil.addAll(result.getDeptIds(), deptService.getChildDeptIdListFromCache(userDeptId.get()));
                // 添加本身部门编号
                CollUtil.addAll(result.getDeptIds(), userDeptId.get());
                continue;
            }
            // 情况五，SELF
            if (Objects.equals(role.getDataScope(), DataScopeEnum.SELF.getScope())) {
                result.setSelf(true);
                continue;
            }
            // 未知情况，error log 即可
            log.error("[getDeptDataPermission][LoginUser({}) role({}) 无法处理]", userId, toJsonString(result));
        }
        return result;
    }

    @Override
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSIONSUB_ADD_ROLE_USERS_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_PERMISSION_ADD_ROLE_USERS__SUCCESS)
    public long addRoleUsers(Long roleId, Set<Long> userIds) {

        RoleDO role = roleService.getRole(roleId);
        roleService.validateRoleList(Collections.singleton(roleId));
        if (CollUtil.isEmpty(userIds)) {
            return 0;
        }

        // 批量插入新的用户角色关系
        List<UserRoleDO> userRoleList = userIds.stream()
                .map(userId -> {
                    UserRoleDO userRole = new UserRoleDO();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());

        List<UserRoleDO> insertedList = userRoleDataRepository.upsertBatch(userRoleList);
        List<AdminUserDO> userList = userService.getUserList(userIds);

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("role", role);
        LogRecordContext.putVariable("userNames", userList.stream().map(AdminUserDO::getNickname)
                .collect(Collectors.joining(",")));
        return CollUtil.isEmpty(insertedList) ? 0 : insertedList.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TenantIgnore
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSION_DELETE_ROLE_USERS_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_PERMISSION_DELETE_ROLE_USERS__SUCCESS)
    public long deleteRoleUsers(Long roleId, Set<Long> userIds) {
        // 参数校验
        if (CollUtil.isEmpty(userIds)) {
            return 0;
        }

        long deleted = userRoleDataRepository.deleteByRoleIdAndUserIds(roleId, userIds);

        RoleDO role = roleService.getRole(roleId);

        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        List<AdminUserDO> userList = userService.getUserList(userIds);

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("role", role);
        LogRecordContext.putVariable("userNames", userList.stream().map(AdminUserDO::getNickname)
                .collect(Collectors.joining(",")));
        // 删除指定角色下的指定用户关系
        return deleted;
    }

    @Override
    public UserRoleDO getUserRoleByUserAndRoleId(Long userId, Long roleId) {
        return userRoleDataRepository.findOne(new DefaultConfigStore()
                .eq(UserRoleDO.USER_ID, userId).eq(UserRoleDO.ROLE_ID, roleId));
    }

    @Override
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSION_ADD_ROLE_MENUS_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_PERMISSION_ADD_ROLE_MENUS_SUCCESS)
    public long addRoleMenus(Long roleId, Set<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return 0;
        }
        RoleDO role = roleService.getRole(roleId);
        roleService.validateRoleList(Collections.singleton(roleId));
        // 批量插入新的角色菜单关系
        List<RoleMenuDO> roleMenuList = menuIds.stream()
                .map(menuId -> {
                    RoleMenuDO roleMenu = new RoleMenuDO();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    return roleMenu;
                })
                .collect(Collectors.toList());

        List<RoleMenuDO> insertedList = roleMenuDataRepository.upsertBatch(roleMenuList);
        // 记录操作日志上下文
        List<MenuDO> allActiveMenuList = menuService.getAllActiveMenuList(menuIds);
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("role", role);
        LogRecordContext.putVariable("menuNames", allActiveMenuList.stream().map(MenuDO::getName)
                .collect(Collectors.joining(",")));

        return CollUtil.isEmpty(insertedList) ? 0 : insertedList.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_PERMISSION_TYPE, subType = SYSTEM_PERMISSION_DELETE_ROLE_MENUS_SUB_TYPE, bizNo = "{{#roleId}}",
            success = SYSTEM_PERMISSION_DELETE_ROLE_MENUS_SUCCESS)
    public long deleteRoleMenus(Long roleId, Set<Long> menuIds) {
        // 参数校验
        if (CollUtil.isEmpty(menuIds)) {
            return 0;
        }
        RoleDO role = roleService.getRole(roleId);
        roleService.validateRoleList(Collections.singleton(roleId));

        // 删除指定角色下的指定菜单关系
        long deleted = roleMenuDataRepository.deleteByRoleIdAndMenuIds(roleId, menuIds);
        // 记录操作日志上下文
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        List<MenuDO> allActiveMenuList = menuService.getAllActiveMenuList(menuIds);

        LogRecordContext.putVariable("loginUser", loginUser);
        LogRecordContext.putVariable("role", role);
        LogRecordContext.putVariable("menuNames", allActiveMenuList.stream().map(MenuDO::getName)
                .collect(Collectors.joining(",")));
        return deleted;
    }

    @Override
    public AuthPermissionInfoRespVO getPermissionInfo(String code) {
        // 1.1 获得用户信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getRoleIdsListByUserId(getLoginUserId());
        if (CollUtil.isEmpty(roleIds)) {
            return AuthConvert.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList(), code);
        }
        List<RoleDO> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, RoleDO::getId));
        List<MenuDO> menuList = menuService.getAllActiveMenuList(menuIds);

        // 1.4检查当前租户是否inner模式，并移除企业权限
       TenantDO tenantDO= tenantService.getTenant(user.getTenantId());
        if (CommonPublishModelEnum.InnerModel.getValue().equals(tenantDO.getPublishModel())) {
            // 排除企业权限
            menuList.removeIf(menu -> menu.getPermission() != null
                    && menu.getPermission().startsWith(MenuConstants.MENU_TENANT_CORP));
        }

        return AuthConvert.INSTANCE.convert(user, roles, menuList, code);
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PermissionServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

    /**
     * 根据菜单ID集合获取菜单详细信息列表
     *
     * @param menuIds 菜单ID集合
     * @return 菜单详细信息列表
     */
    @Override
    public List<PermissionMenuRespVO> getMenuDetailListByIds(Set<Long> menuIds) {
        if (CollUtil.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        // 查询菜单实体
        List<MenuDO> menuDOList = menuService.getAllActiveMenuList(menuIds);
        // 转换为VO
        return BeanUtils.toBean(menuDOList, PermissionMenuRespVO.class);
    }
}
