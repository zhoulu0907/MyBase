package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemRoleMapper;
import com.cmsr.onebase.module.system.enums.permission.RoleCodeEnum;
import com.cmsr.onebase.module.system.vo.role.RolePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 角色数据访问层
 * <p>
 * 基于 MyBatis-Flex 实现角色相关的数据操作，方法签名与 anyline 版本保持一致，便于平滑迁移。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class RoleDataRepository extends BaseDataServiceImpl<SystemRoleMapper, RoleDO> {

    /**
     * 根据角色名称查询角色
     *
     * @param name 角色名称
     * @return 角色对象
     */
    public RoleDO findOneByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return getOne(query().eq(RoleDO.NAME, name));
    }

    /**
     * 根据角色编码查询角色
     *
     * @param code 角色编码
     * @return 角色对象
     */
    public RoleDO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(query().eq(RoleDO.CODE, code));
    }

    /**
     * 根据状态查询角色列表
     *
     * @param status 状态
     * @return 角色列表
     */
    public List<RoleDO> findListByStatus(Integer status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return list(query()
                .eq(RoleDO.STATUS, status)
                .orderBy(RoleDO.TYPE, true)
                .orderBy(RoleDO.SORT, true)
                .orderBy(RoleDO.CREATE_TIME, false));
    }

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    public List<RoleDO> findAllRoles() {
        return list(query());
    }

    /**
     * 分页查询角色（仅租户侧，排除系统内置的企业/平台管理员）
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<RoleDO> findRolePageOnlyTenant(RolePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = buildRolePageQueryWrapper(pageReqVO)
                .notIn(RoleDO.CODE, RoleCodeEnum.CORP_ADMIN.getCode(), RoleCodeEnum.SUPER_ADMIN.getCode())
                .orderBy(RoleDO.SORT, true)
                .orderBy(BaseDO.CREATE_TIME, false);

        Page<RoleDO> page = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    /**
     * 分页查询角色
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<RoleDO> findRolePage(RolePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = buildRolePageQueryWrapper(pageReqVO)
                .orderBy(RoleDO.SORT, true)
                .orderBy(BaseDO.CREATE_TIME, false);

        Page<RoleDO> page = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    private QueryWrapper buildRolePageQueryWrapper(RolePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query();

        if (pageReqVO == null) {
            return queryWrapper;
        }

        // 按名称模糊查询
        if (StringUtils.isNotBlank(pageReqVO.getName())) {
            queryWrapper.like(RoleDO.NAME, pageReqVO.getName());
        }

        // 按编码模糊查询
        if (StringUtils.isNotBlank(pageReqVO.getCode())) {
            queryWrapper.like(RoleDO.CODE, pageReqVO.getCode());
        }

        // 按状态查询
        if (pageReqVO.getStatus() != null) {
            queryWrapper.eq(RoleDO.STATUS, pageReqVO.getStatus());
        }

        // 按创建时间范围查询
        if (pageReqVO.getCreateTime() != null && pageReqVO.getCreateTime().length == 2) {
            if (pageReqVO.getCreateTime()[0] != null) {
                queryWrapper.ge(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[0]);
            }
            if (pageReqVO.getCreateTime()[1] != null) {
                queryWrapper.le(BaseDO.CREATE_TIME, pageReqVO.getCreateTime()[1]);
            }
        }

        return queryWrapper;
    }

    /**
     * 根据角色编码列表查询角色
     *
     * @param codes 角色编码列表
     * @return 角色列表
     */
    public List<RoleDO> findAllByCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(RoleDO.CODE, codes));
    }

    /**
     * 根据角色编码和租户ID查询角色
     *
     * @param code 角色编码
     * @param tenandID 租户ID
     * @return 角色对象
     */
    public RoleDO getRoleIdsByCodeAndTenantId(String code, Long tenandID) {
        if (StringUtils.isBlank(code) || tenandID == null) {
            return null;
        }
        return getOne(query().eq(RoleDO.CODE, code).eq(RoleDO.TENANT_ID, tenandID));
    }

    /**
     * 根据角色编码查询角色
     *
     * @param codes 角色编码
     * @return 角色对象
     */
    public RoleDO getRoleByCode(String codes) {
        if (StringUtils.isBlank(codes)) {
            return null;
        }
        return getOne(query().eq(RoleDO.CODE, codes));
    }

    /**
     * 根据ID集合查询角色列表
     *
     * @param ids 角色ID集合
     * @return 角色列表
     */
    public List<RoleDO> findAllByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return listByIds(ids);
    }
}

