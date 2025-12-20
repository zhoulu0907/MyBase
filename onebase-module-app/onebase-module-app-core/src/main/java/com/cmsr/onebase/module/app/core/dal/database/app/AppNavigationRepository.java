package com.cmsr.onebase.module.app.core.dal.database.app;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppNavigationDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppNavigationMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppNavigationTableDef.APP_NAVIGATION;

/**
 * @Author：huangjie
 * @Date：2025/12/20 9:56
 */
@Repository
public class AppNavigationRepository extends BaseBizRepository<AppNavigationMapper, AppNavigationDO> {
    public AppNavigationDO findByApplicationId(Long id) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_NAVIGATION.APPLICATION_ID.eq(id));
        return this.getOne(queryWrapper);
    }
}
