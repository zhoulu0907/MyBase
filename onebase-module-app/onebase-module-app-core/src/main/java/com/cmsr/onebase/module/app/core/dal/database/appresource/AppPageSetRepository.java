package com.cmsr.onebase.module.app.core.dal.database.appresource;

import com.cmsr.onebase.module.app.core.dal.dataobject.appresource.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.mapper.appresource.AppPageSetMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppPageSetRepository extends ServiceImpl<AppPageSetMapper, PageSetDO> {

    public PageSetDO findPageSetByMenuId(Long menuId) {
        QueryWrapper queryWrapper = this.query().eq(PageSetDO::getMenuId, menuId);
        return getOne(queryWrapper);
    }

    public List<PageSetDO> findByMenuIds(List<Long> menuIds) {
        QueryWrapper queryWrapper = this.query().in(PageSetDO::getMenuId, menuIds);
        return list(queryWrapper);
    }

    public void deletePageSetByMenuId(Long menuId) {
        QueryWrapper queryWrapper = this.query().eq(PageSetDO::getMenuId, menuId);
        remove(queryWrapper);
    }

    public List<PageSetDO> findByMenuIdAndType(List<Long> menuIds, Integer pageSetType) {
        QueryWrapper queryWrapper = this.query().in(PageSetDO::getMenuId, menuIds)
                .eq(PageSetDO::getPageSetType, pageSetType, pageSetType != null);
        return list(queryWrapper);
    }

}
