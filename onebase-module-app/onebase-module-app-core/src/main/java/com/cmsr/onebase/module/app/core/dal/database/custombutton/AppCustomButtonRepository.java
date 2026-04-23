package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public class AppCustomButtonRepository extends BaseBizRepository<AppCustomButtonMapper, AppCustomButtonDO> {

    public List<AppCustomButtonDO> findByPageSetUuid(String pageSetUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("pageset_uuid").eq(pageSetUuid));
        List<AppCustomButtonDO> result = list(queryWrapper);
        result.sort(Comparator.comparing(v -> v.getSortNo() == null ? 0 : v.getSortNo()));
        return result;
    }

    public List<AppCustomButtonDO> findEnabledByPageSetUuidAndScope(String pageSetUuid, String scope) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("pageset_uuid").eq(pageSetUuid))
                .where(new QueryColumn("status").eq("ENABLE"))
                .where(new QueryColumn("operation_scope").eq(scope));
        List<AppCustomButtonDO> result = list(queryWrapper);
        result.sort(Comparator.comparing(v -> v.getSortNo() == null ? 0 : v.getSortNo()));
        return result;
    }

    public AppCustomButtonDO findByButtonCode(String buttonCode) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("button_code").eq(buttonCode));
        return getOne(queryWrapper);
    }

    public Long countByPageSetUuid(String pageSetUuid) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("pageset_uuid").eq(pageSetUuid));
        return count(queryWrapper);
    }

    public boolean existsByPageSetUuidAndButtonName(String pageSetUuid, String buttonName, Long excludeId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("pageset_uuid").eq(pageSetUuid))
                .where(new QueryColumn("button_name").eq(buttonName));
        if (excludeId != null) {
            queryWrapper.where(new QueryColumn("id").ne(excludeId));
        }
        return exists(queryWrapper);
    }
}
