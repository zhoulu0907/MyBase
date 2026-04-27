package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonActionConfigDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonActionConfigMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class AppCustomButtonActionConfigRepository extends BaseBizRepository<AppCustomButtonActionConfigMapper, AppCustomButtonActionConfigDO> {

    public AppCustomButtonActionConfigDO findByButtonUuid(String buttonUuid) {
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
