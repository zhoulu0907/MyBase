package com.cmsr.onebase.module.system.service.permission;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.system.dal.database.RoleDataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.redis.RedisKeyConstants;
import com.cmsr.onebase.module.system.enums.permission.DataScopeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.enums.permission.RoleTypeEnum;
import com.cmsr.onebase.module.system.vo.role.RoleInsertReqVO;
import com.cmsr.onebase.module.system.vo.role.RolePageReqVO;
import com.cmsr.onebase.module.system.vo.role.RoleUpdateReqVO;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;
import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertMap;
import static com.cmsr.onebase.module.system.enums.ErrorCodeConstants.*;
import static com.cmsr.onebase.module.system.enums.LogRecordConstants.*;

/**
 * 角色 Service 实现类
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleDataRepository roleDataRepository;

    /**
     * 自注入 RoleService 的代理对象，确保类内调用时 AOP（如 @Cacheable）生效；
     * 使用 @Lazy 避免启动期自引用导致的循环依赖问题。
     */
    @Resource
    @Lazy
    private RoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_CREATE_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_ROLE_CREATE_SUCCESS)
    public Long createRole(RoleInsertReqVO createReqVO, Integer type) {
        // 0. 生成Code
        if (!StringUtils.hasText(createReqVO.getCode())) {
            createReqVO.setCode("role_" + System.currentTimeMillis());
        }
        // 1. 校验角色
        validateRoleDuplicate(createReqVO.getName(), createReqVO.getCode(), null);

        // 2. 插入到数据库
        RoleDO role = BeanUtils.toBean(createReqVO, RoleDO.class);
        role.setType(ObjectUtils.defaultIfNull(type, RoleTypeEnum.CUSTOM.getType()));
        role.setStatus(ObjectUtils.defaultIfNull(createReqVO.getStatus(), CommonStatusEnum.ENABLE.getStatus()));
        role.setDataScope(DataScopeEnum.ALL.getScope()); // 默认可查看所有数据。
        roleDataRepository.insert(role);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("role", role);
        return role.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#updateReqVO.id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_ROLE_UPDATE_SUCCESS)
    public void updateRole(@Valid RoleUpdateReqVO updateReqVO) {
        // 1.1 校验是否可以更新
        RoleDO role = validateRoleForUpdate(updateReqVO.getId());
        // 1.2 校验角色的唯一字段是否重复
        validateRoleDuplicate(updateReqVO.getName(), updateReqVO.getCode(), updateReqVO.getId());

        // 2. 更新到数据库
        RoleDO updateObj = BeanUtils.toBean(updateReqVO, RoleDO.class);
        roleDataRepository.update(updateObj);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(role, RoleInsertReqVO.class));
        LogRecordContext.putVariable("role", role);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    public void updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        // 校验是否可以更新
        validateRoleForUpdate(id);

        // 更新数据范围
        RoleDO updateObject = new RoleDO();
        updateObject.setId(id);
        updateObject.setDataScope(dataScope);
        updateObject.setDataScopeDeptIds(dataScopeDeptIds);
        roleDataRepository.update(updateObject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_ROLE_DELETE_SUCCESS)
    public void deleteRole(Long id) {
        // 1. 校验是否可以更新
        RoleDO role = validateRoleForUpdate(id);

        // 2.1 标记删除
        roleDataRepository.deleteById(id);
        // 2.2 删除相关数据
        permissionService.processRoleDeleted(id);

        // 3. 记录操作日志上下文
        LogRecordContext.putVariable("role", role);
    }

    /**
     * 校验角色的唯一字段是否重复
     * <p>
     * 1. 是否存在相同名字的角色
     * 2. 是否存在相同编码的角色
     *
     * @param name 角色名字
     * @param code 角色额编码
     * @param id   角色编号
     */
    @VisibleForTesting
    void validateRoleDuplicate(String name, String code, Long id) {
        // 0. 超级管理员，不允许创建
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        // 1. 该 name 名字被其它角色所使用
        if (!StringUtils.hasText(name)) {
            return;
        }
        RoleDO role = roleDataRepository.findOneByName(name);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_NAME_DUPLICATE, name);
        }
        // 2. 是否存在相同编码的角色
        if (!StringUtils.hasText(code)) {
            return;
        }
        // 该 code 编码被其它角色所使用
        role = roleDataRepository.findOneByCode(code);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     * @return 角色对象
     */
    @VisibleForTesting
    RoleDO validateRoleForUpdate(Long id) {
        RoleDO role = roleDataRepository.findById(id);
        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        // 内置角色，不允许删除
        if (RoleTypeEnum.SYSTEM.getType().equals(role.getType())) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
        return role;
    }

    @Override
    public RoleDO getRole(Long id) {
        return roleDataRepository.findById(id);
    }

    @Override
    public RoleDO getRoleIdsByCode(String code) {
        return roleDataRepository.findOneByCode(code);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id",
            unless = "#result == null")
    public RoleDO getRoleFromCache(Long id) {
        return roleDataRepository.findById(id);
    }

    @Override
    public List<RoleDO> getRoleListByStatus(Integer status) {
        return roleDataRepository.findListByStatus(status);
    }

    @Override
    public List<RoleDO> getRoleList() {
        return roleDataRepository.findAll();
    }

    @Override
    public List<RoleDO> getRoleList(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleDataRepository.findAllByIds(ids);
    }

    @Override
    public List<RoleDO> getRoleListFromCache(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 通过代理对象调用，确保 @Cacheable 生效
        return convertList(ids, roleService::getRoleFromCache);
    }

    @Override
    public PageResult<RoleDO> findRolePageOnlyTenant(RolePageReqVO reqVO) {
        return roleDataRepository.findRolePageOnlyTenant(reqVO);
    }

    @Override
    public boolean hasAnySuperOrTenantAdmin(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        // 通过代理对象调用，确保 @Cacheable 生效
        return ids.stream().anyMatch(id -> {
            RoleDO role = roleService.getRoleFromCache(id);
            return role != null && (RoleCodeEnum.isSuperAdmin(role.getCode()) || RoleCodeEnum.isTenantAdmin(role.getCode()));
        });
    }

    @Override
    public boolean hasAnyCorpAdmin(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        // 通过代理对象调用，确保 @Cacheable 生效
        return ids.stream().anyMatch(id -> {
            RoleDO role = roleService.getRoleFromCache(id);
            return role != null && (RoleCodeEnum.isCorpAdmin(role.getCode()));
        });
    }

    @Override
    public void validateRoleList(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<RoleDO> roles = roleDataRepository.findAllByIds(ids);
        Map<Long, RoleDO> roleMap = convertMap(roles, RoleDO::getId);
        // 校验
        ids.forEach(id -> {
            RoleDO role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }

    @Override
    public boolean isTenantAdmin(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        // 通过代理对象调用，确保 @Cacheable 生效
        return ids.stream().anyMatch(id -> {
            RoleDO role = roleService.getRoleFromCache(id);
            return role != null && RoleCodeEnum.TENANT_ADMIN.getCode().equals(role.getCode());
        });
    }

    @Override
    @TenantIgnore
    public RoleDO getRoleIdsByCodeAndTenantId(String code, Long id) {
        return roleDataRepository.getRoleIdsByCodeAndTenantId(code, id);
    }

    public RoleDO getRoleByCode(String code) {
        return roleDataRepository.getRoleByCode(code);
    }

    @Override
    public boolean hasAnyDevloperAdmin(Collection<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return false;
        }
        // 通过代理对象调用，确保 @Cacheable 生效
        return roleIds.stream().anyMatch(id -> {
            RoleDO role = roleService.getRoleFromCache(id);
            return role != null && (RoleCodeEnum.isDevloperAdmin(role.getCode()));
        });
    }

}
