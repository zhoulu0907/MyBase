package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonActionFlowDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonActionFlowMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class AppCustomButtonActionFlowRepository extends BaseBizRepository<AppCustomButtonActionFlowMapper, AppCustomButtonActionFlowDO> {

    public AppCustomButtonActionFlowDO findByButtonUuid(String buttonUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_uuid").eq(buttonUuid));
        return getOne(queryWrapper);
    }

    public boolean removeByButtonUuid(String buttonUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_uuid").eq(buttonUuid));
        return remove(queryWrapper);
    }
}
