package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourcePagesetMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppPageSetRepository extends ServiceImpl<AppResourcePagesetMapper, AppResourcePagesetDO> {

    public AppResourcePagesetDO findPageSetByMenuId(Long menuId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourcePagesetDO::getMenuId, menuId);
        return getOne(queryWrapper);
    }

    public List<AppResourcePagesetDO> findByMenuIds(List<Long> menuIds) {
        QueryWrapper queryWrapper = this.query().in(AppResourcePagesetDO::getMenuId, menuIds);
        return list(queryWrapper);
    }

    public void deletePageSetByMenuId(Long menuId) {
        QueryWrapper queryWrapper = this.query().eq(AppResourcePagesetDO::getMenuId, menuId);
        remove(queryWrapper);
    }

    public List<AppResourcePagesetDO> findByMenuIdAndType(List<Long> menuIds, Integer pageSetType) {
        QueryWrapper queryWrapper = this.query().in(AppResourcePagesetDO::getMenuId, menuIds)
                .eq(AppResourcePagesetDO::getPageSetType, pageSetType, pageSetType != null);
        return list(queryWrapper);
    }

}
