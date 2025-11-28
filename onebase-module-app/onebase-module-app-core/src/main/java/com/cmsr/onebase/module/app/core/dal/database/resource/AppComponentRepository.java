package com.cmsr.onebase.module.app.core.dal.database.resource;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourceComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppResourceComponentMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppComponentRepository extends ServiceImpl<AppResourceComponentMapper, AppResourceComponentDO> {

    public void deleteComponentByPageId(Long pageId) {
        QueryWrapper queryWrapper = query().eq(AppResourceComponentDO::getPageId, pageId);
        this.remove(queryWrapper);
    }

    public List<AppResourceComponentDO> findByPageId(Long pageId) {
        QueryWrapper queryWrapper = query().eq(AppResourceComponentDO::getPageId, pageId).orderBy(AppResourceComponentDO::getComponentIndex, true);
        return this.list(queryWrapper);
    }
}
