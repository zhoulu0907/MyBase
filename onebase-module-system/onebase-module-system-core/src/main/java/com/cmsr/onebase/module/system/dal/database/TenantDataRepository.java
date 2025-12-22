package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemTenantMapper;
import com.cmsr.onebase.module.system.enums.tenant.SortEnum;
import com.cmsr.onebase.module.system.enums.tenant.TenantCodeEnum;
import com.cmsr.onebase.module.system.vo.tenant.TenantPageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.system.dal.flex.table.SystemTenantTableDef.SYSTEM_TENANT;

/**
 * 租户数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class TenantDataRepository extends BaseDataServiceImpl<SystemTenantMapper, TenantDO> {

    /**
     * 根据租户名称查询租户
     *
     * @param name 租户名称
     * @return 租户对象
     */
    public TenantDO findByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return getOne(query().eq(TenantDO.NAME, name));
    }

    /**
     * 根据网站域名查询租户
     *
     * @param website 网站域名
     * @return 租户对象
     */
    public TenantDO findByWebsite(String website) {
        if (StringUtils.isBlank(website)) {
            return null;
        }
        return getOne(query().eq(TenantDO.WEBSITE, website));
    }

    /**
     * 根据套餐ID统计租户数量
     *
     * @param packageId 套餐ID
     * @return 租户数量
     */
    public long countByPackageId(Long packageId) {
        if (packageId == null) {
            return 0L;
        }
        return count(query().eq(TenantDO.PACKAGE_ID, packageId));
    }

    /**
     * 根据状态统计租户数量
     *
     * @param status 状态
     * @return 租户数量
     */
    public long countByStatus(Integer status) {
        if (status == null) {
            return 0L;
        }
        return count(query().eq(TenantDO.STATUS, status));
    }

    /**
     * 根据套餐ID查询租户列表
     *
     * @param packageId 套餐ID
     * @return 租户列表
     */
    public List<TenantDO> findAllByPackageId(Long packageId) {
        if (packageId == null) {
            return List.of();
        }
        return list(query().eq(TenantDO.PACKAGE_ID, packageId));
    }

    /**
     * 根据状态查询租户列表
     *
     * @param status 状态
     * @return 租户列表
     */
    public List<TenantDO> findAllByStatus(Integer status) {
        if (status == null) {
            return List.of();
        }
        return list(query().eq(TenantDO.STATUS, status).orderBy(TenantDO.CREATE_TIME, false));
    }

    /**
     * 查询所有租户列表
     *
     * @return 租户列表
     */
    public List<TenantDO> findAll() {
        return list();
    }

    /**
     * 根据状态统计租户数量（排除平台租户）
     *
     * @param status 状态
     * @param excludeTenantId 排除的租户ID（可为 null）
     * @return 租户数量
     */
    public long countByStatusExcludePlatform(Integer status, Long excludeTenantId) {
        if (status == null) {
            return 0L;
        }
        QueryWrapper queryWrapper = query()
                .eq(TenantDO.STATUS, status)
                .ne(TenantDO.TENANT_CODE, TenantCodeEnum.PLATFORM_TENANT.getCode());
        if (excludeTenantId != null) {
            queryWrapper.ne(TenantDO.COL_ID, excludeTenantId);
        }
        return count(queryWrapper);
    }

    /**
     * 根据状态查询租户列表（排除平台租户）
     *
     * @param status 状态
     * @param excludeTenantId 排除的租户ID（可为 null）
     * @return 租户列表
     */
    public List<TenantDO> findAllByStatusExcludePlatform(Integer status, Long excludeTenantId) {
        if (status == null) {
            return List.of();
        }
        QueryWrapper queryWrapper = query()
                .eq(TenantDO.STATUS, status)
                .ne(TenantDO.TENANT_CODE, TenantCodeEnum.PLATFORM_TENANT.getCode());
        if (excludeTenantId != null) {
            queryWrapper.ne(TenantDO.COL_ID, excludeTenantId);
        }
        return list(queryWrapper);
    }

    /**
     * 分页查询租户
     *
     * @param pageReqVO 分页查询条件
     * @return 分页结果
     */
    public PageResult<TenantDO> findPage(TenantPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query();

        // 根据关键词模糊查询
        if (StringUtils.isNotBlank(pageReqVO.getKeyword())) {
            queryWrapper.and(SYSTEM_TENANT.NAME.like(pageReqVO.getKeyword())
                    .or(SYSTEM_TENANT.TENANT_CODE.like(pageReqVO.getKeyword()))
            );
        }

        // 按状态查询
        if (pageReqVO.getStatus() != null) {
            queryWrapper.eq(TenantDO.STATUS, pageReqVO.getStatus());
        }

        // 排除平台租户
        queryWrapper.ne(TenantDO.TENANT_CODE, TenantCodeEnum.PLATFORM_TENANT.getCode());

        // 排序
        if (pageReqVO.getSortType() != null && pageReqVO.getSortType().equals(SortEnum.DESC.getValue())) {
            queryWrapper.orderBy(BaseDO.CREATE_TIME, false);
        } else {
            queryWrapper.orderBy(BaseDO.CREATE_TIME, true);
        }

        Page<TenantDO> page = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }
}
