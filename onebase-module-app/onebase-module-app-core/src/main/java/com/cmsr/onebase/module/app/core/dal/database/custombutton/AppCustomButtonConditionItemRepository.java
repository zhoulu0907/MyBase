package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonConditionItemDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonConditionItemMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Repository
public class AppCustomButtonConditionItemRepository extends BaseBizRepository<AppCustomButtonConditionItemMapper, AppCustomButtonConditionItemDO> {

    public List<AppCustomButtonConditionItemDO> findByButtonUuid(String buttonUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_uuid").eq(buttonUuid));
        List<AppCustomButtonConditionItemDO> result = list(queryWrapper);
        result.sort(Comparator.comparing(v -> v.getSortNo() == null ? 0 : v.getSortNo()));
        return result;
    }

    public List<AppCustomButtonConditionItemDO> findByGroupIds(Collection<Long> groupIds) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("group_id").in(groupIds));
        List<AppCustomButtonConditionItemDO> result = list(queryWrapper);
        result.sort(Comparator.comparing(v -> v.getSortNo() == null ? 0 : v.getSortNo()));
        return result;
    }

    public boolean removeByButtonUuid(String buttonUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_uuid").eq(buttonUuid));
        return remove(queryWrapper);
    }
}
