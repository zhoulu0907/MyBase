package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonExecLogDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonExecLogMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class AppCustomButtonExecLogRepository extends BaseBizRepository<AppCustomButtonExecLogMapper, AppCustomButtonExecLogDO> {

    public AppCustomButtonExecLogDO getByExecLogId(Long execLogId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("id").eq(execLogId));
        return getOne(queryWrapper);
    }
}
