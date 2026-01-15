package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemTenantPackageMapper;
import com.cmsr.onebase.module.system.vo.tenant.TenantPackagePageReqVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.framework.data.base.BaseDO.ID;

/**
 * 租户套餐数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class TenantPackageDataRepository extends BaseDataRepository<SystemTenantPackageMapper, TenantPackageDO> {

    /**
     * 根据名称查找租户套餐
     *
     * @param name 套餐名称
     * @return 租户套餐
     */
    public TenantPackageDO findOneByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return getOne(query().eq(TenantPackageDO.NAME, name));
    }

    /**
     * 根据套餐编码查找租户套餐
     *
     * @param code 套餐编码
     * @return 租户套餐
     */
    public TenantPackageDO findOneByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return getOne(query().eq(TenantPackageDO.CODE, code));
    }

    /**
     * 根据状态查询租户套餐列表
     *
     * @param status 状态
     * @return 租户套餐列表
     */
    public List<TenantPackageDO> findListByStatus(Integer status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return list(query().eq(TenantPackageDO.STATUS, status));
    }

    /**
     * 分页查询租户套餐
     *
     * @param pageReqVO 分页查询参数
     * @return 分页结果
     */
    public PageResult<TenantPackageDO> findPage(TenantPackagePageReqVO pageReqVO) {
        QueryWrapper queryWrapper = query()
                .like(TenantPackageDO.NAME, pageReqVO.getName(), StringUtils.isNotBlank(pageReqVO.getName()))
                .eq(TenantPackageDO.STATUS, pageReqVO.getStatus(), pageReqVO.getStatus() != null)
                .orderBy(ID, false);

        Page<TenantPackageDO> pageResult = page(Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }
}
