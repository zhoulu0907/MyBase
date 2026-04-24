package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonConditionGroupDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonConditionGroupMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public class AppCustomButtonConditionGroupRepository extends BaseBizRepository<AppCustomButtonConditionGroupMapper, AppCustomButtonConditionGroupDO> {

    public List<AppCustomButtonConditionGroupDO> findByButtonUuid(String buttonUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_uuid").eq(buttonUuid));
        List<AppCustomButtonConditionGroupDO> result = list(queryWrapper);
        result.sort(Comparator.comparing(v -> v.getSortNo() == null ? 0 : v.getSortNo()));
        return result;
    }

    public boolean removeByButtonUuid(String buttonUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_uuid").eq(buttonUuid));
        return remove(queryWrapper);
    }
}
