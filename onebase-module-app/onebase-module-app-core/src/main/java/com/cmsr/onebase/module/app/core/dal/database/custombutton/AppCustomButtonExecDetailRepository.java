package com.cmsr.onebase.module.app.core.dal.database.custombutton;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppCustomButtonExecDetailDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppCustomButtonExecDetailMapper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AppCustomButtonExecDetailRepository extends BaseBizRepository<AppCustomButtonExecDetailMapper, AppCustomButtonExecDetailDO> {

    public List<AppCustomButtonExecDetailDO> findByExecLogId(Long execLogId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(new QueryColumn("exec_log_id").eq(execLogId));
        return list(queryWrapper);
    }
}
