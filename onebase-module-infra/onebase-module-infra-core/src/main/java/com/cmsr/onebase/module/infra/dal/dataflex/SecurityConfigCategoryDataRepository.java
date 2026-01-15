package com.cmsr.onebase.module.infra.dal.dataflex;

import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.infra.dal.dataflexdo.ssecurity.SecurityConfigCategoryDO;
import com.cmsr.onebase.module.infra.dal.mapper.ssecurity.SecurityConfigCategoryMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 安全配置分类数据访问层
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Repository
public class SecurityConfigCategoryDataRepository extends ServiceImpl<SecurityConfigCategoryMapper, SecurityConfigCategoryDO> {

    /**
     * 查询所有有效的分类，按排序号升序
     *
     * @return 分类列表
     */
    @TenantIgnore
    public List<SecurityConfigCategoryDO> findAllActive() {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.orderBy(SecurityConfigCategoryDO.SORT_ORDER, true);
        return list(queryWrapper);
    }

    /**
     * 根据分类编码列表，查询所有有效的分类，按排序号升序
     *
     * @return 分类列表
     */
    @TenantIgnore
    public List<SecurityConfigCategoryDO> findActiveByCodes(List<String> categoryCodes) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(SecurityConfigCategoryDO.CATEGORY_CODE, categoryCodes);
        queryWrapper.orderBy(SecurityConfigCategoryDO.SORT_ORDER, true);
        return list(queryWrapper);
    }

}