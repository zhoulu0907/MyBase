package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.module.app.core.dal.dataobject.ComponentDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppComponentMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppComponentRepository extends ServiceImpl<AppComponentMapper, ComponentDO> {

    public void deleteComponentByPageId(Long pageId) {
        QueryWrapper queryWrapper = query().eq(ComponentDO::getPageId, pageId);
        this.remove(queryWrapper);
    }

    public List<ComponentDO> findByPageId(Long pageId) {
        QueryWrapper queryWrapper = query().eq(ComponentDO::getPageId, pageId).orderBy(ComponentDO::getComponentIndex, true);
        return this.list(queryWrapper);
    }
}
