package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.mybatis.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthFieldMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 应用权限字段数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthFieldRepository extends BaseAppRepository<AppAuthFieldMapper, AuthFieldDO> {

    public List<AuthFieldDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthFieldDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthFieldDO::getRoleId, reqVO.getRoleId())
                .eq(AuthFieldDO::getMenuId, reqVO.getMenuId());
        return list(queryWrapper);
    }

    public AuthFieldDO findByQuery(AuthPermissionReq reqVO, Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthFieldDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthFieldDO::getRoleId, reqVO.getRoleId())
                .eq(AuthFieldDO::getMenuId, reqVO.getMenuId())
                .eq(AuthFieldDO::getFieldId, fieldId);
        return this.getOne(queryWrapper);
    }

    public void deleteByQuery(AuthPermissionReq reqVO) {
        this.updateChain()
                .eq(AuthFieldDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthFieldDO::getRoleId, reqVO.getRoleId())
                .eq(AuthFieldDO::getMenuId, reqVO.getMenuId())
                .remove();
    }

    public List<AuthFieldDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthFieldDO::getApplicationId, applicationId)
                .in(AuthFieldDO::getRoleId, roleIds)
                .eq(AuthFieldDO::getMenuId, menuId);
        return list(queryWrapper);
    }
}